package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.Event;
import com.vmware.vim25.LocalizedMethodFault;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VmPoweredOffEvent;
import com.vmware.vim25.VmPoweredOnEvent;
import com.vmware.vim25.VmReconfiguredEvent;
import com.vmware.vim25.VmResourcePoolMovedEvent;
import com.vmware.vim25.VmResumingEvent;
import com.vmware.vim25.VmStartingEvent;
import com.vmware.vim25.VmStoppingEvent;
import com.vmware.vim25.VmSuspendedEvent;
import com.vmware.vim25.VmSuspendingEvent;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ComputerSnapshot;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.Success;
import net.java.dev.vcc.api.commands.RestartComputer;
import net.java.dev.vcc.api.commands.StartComputer;
import net.java.dev.vcc.api.commands.StopComputer;
import net.java.dev.vcc.api.commands.SuspendComputer;
import net.java.dev.vcc.spi.AbstractComputer;
import net.java.dev.vcc.spi.AbstractManagedObject;
import net.java.dev.vcc.util.CompletedFuture;
import net.java.dev.vcc.util.FutureReference;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

final class ViComputer extends AbstractComputer implements ViEventReceiver {

    private final ViDatacenter datacenter;

    private final Object lock = new Object();

    private ViDatacenterResourceGroup parent;

    private String name;

    private VirtualMachineConfigInfo config;
    private VirtualMachineRuntimeInfo runtime;
    private VirtualMachineSnapshotInfo snapshot;
    private FutureReference<PowerState> futureState = null;

    ViComputer(ViDatacenter datacenter, ManagedObjectId<Computer> id, ViDatacenterResourceGroup parent, String name,
               VirtualMachineConfigInfo config, VirtualMachineRuntimeInfo runtime,
               VirtualMachineSnapshotInfo snapshot) {
        super(id);
        this.datacenter = datacenter;
        this.parent = parent;
        this.name = name;
        this.config = config;
        this.runtime = runtime;
        this.snapshot = snapshot;
    }

    public Set<Class<? extends Command>> getCommands() {
        List<Class<? extends Command>> classes = new ArrayList<Class<? extends Command>>();
        switch (getState()) {
            case STOPPED:
                classes.add(StartComputer.class);
                break;
            case SUSPENDED:
                classes.add(StartComputer.class);
                classes.add(StopComputer.class);
                break;
            case RUNNING:
                classes.add(SuspendComputer.class);
                classes.add(RestartComputer.class);
                classes.add(StopComputer.class);
                break;
        }
        return Collections.unmodifiableSet(new HashSet<Class<? extends Command>>(classes));
    }

    public <T extends Command> T execute(T command) {
        try {
            if (command instanceof StartComputer) {
                command.setSubmitted(datacenter.addPendingTask(
                        datacenter.getConnection().getProxy().powerOnVMTask(getId().getMORef(), null),
                        new SetPowerStateOnSuccess(VirtualMachinePowerState.POWERED_ON)
                ));
            } else if (command instanceof StopComputer) {
                command.setSubmitted(datacenter.addPendingTask(
                        datacenter.getConnection().getProxy().powerOffVMTask(getId().getMORef()),
                        new SetPowerStateOnSuccess(VirtualMachinePowerState.POWERED_OFF)
                ));
            } else if (command instanceof SuspendComputer) {
                command.setSubmitted(datacenter.addPendingTask(
                        datacenter.getConnection().getProxy().suspendVMTask((getId().getMORef())),
                        new SetPowerStateOnSuccess(VirtualMachinePowerState.SUSPENDED)
                ));
            } else {
                command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
            }
        } catch (Throwable e) {
            command.setSubmitted(new CompletedFuture(e.getMessage(), e));
        }
        return command;
    }

    public Host getHost() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isHostChanging() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Future<Host> getFutureHost() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PowerState getState() {
        synchronized (lock) {
            if (runtime == null || runtime.getPowerState() == null) {
                return PowerState.STOPPED;
            }
            switch (runtime.getPowerState()) {
                case POWERED_OFF:
                    return PowerState.STOPPED;
                case POWERED_ON:
                    return PowerState.RUNNING;
                case SUSPENDED:
                    return PowerState.SUSPENDED;
                default:
                    return PowerState.STOPPED;
            }
        }
    }

    public boolean isStateChanging() {
        synchronized (lock) {
            if (futureState == null) {
                return false;
            }
            if (futureState.isDone()) {
                futureState = null;
                return false;
            }
            return true;
        }
    }

    public Future<PowerState> getFutureState() {
        synchronized (lock) {
            return futureState == null ? new CompletedFuture<PowerState>(getState()) : futureState;
        }
    }

    public Set<ComputerSnapshot> getSnapshots() {
        return Collections.emptySet(); // TODO
    }

    public Set<Host> getAllowedHosts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        synchronized (lock) {
            return name;
        }
    }

    public String getDescription() {
        synchronized (lock) {
            return config == null ? null : config.getAnnotation();
        }
    }

    @Override
    public ViComputerId getId() {
        return (ViComputerId) super.getId();
    }

    void setParent(ViDatacenterResourceGroup parent) {
        synchronized (lock) {
            this.parent = parent;
        }
    }

    void setName(String name) {
        synchronized (lock) {
            this.name = name;
        }
    }

    public void receiveEvent(Event event) {
        synchronized (lock) {
            if (event instanceof VmResourcePoolMovedEvent) {
                VmResourcePoolMovedEvent rpMoved = (VmResourcePoolMovedEvent) event;
                AbstractManagedObject oldParent = datacenter.getManagedObject(rpMoved.getOldParent().getResourcePool());
                AbstractManagedObject newParent = datacenter.getManagedObject(rpMoved.getOldParent().getResourcePool());
                if (oldParent instanceof ViHostResourceGroup) {
                    ((ViHostResourceGroup) oldParent).removeComputer(this);
                    datacenter.getLog().debug("Removing {0} from {1}", this, oldParent);
                } else if (oldParent instanceof ViHost) {
                    ((ViHost) oldParent).removeComputer(this);
                    datacenter.getLog().debug("Removing {0} from {1}", this, oldParent);
                } else {
                    datacenter.getLog().debug("No old parent");
                }
                if (newParent instanceof ViHostResourceGroup) {
                    ((ViHostResourceGroup) newParent).addComputer(this);
                    datacenter.getLog().debug("Adding {0} to {1}", this, newParent);
                } else if (newParent instanceof ViHost) {
                    ((ViHost) newParent).addComputer(this);
                    datacenter.getLog().debug("Adding {0} to {1}", this, newParent);
                } else {
                    datacenter.getLog().debug("No new parent");
                }
            } else if (event instanceof VmReconfiguredEvent) {
                VmReconfiguredEvent reconf = (VmReconfiguredEvent) event;
                // TODO update the new config spec
            } else if (event instanceof VmPoweredOnEvent) {
                setState(VirtualMachinePowerState.POWERED_ON);
            } else if (event instanceof VmPoweredOffEvent) {
                setState(VirtualMachinePowerState.POWERED_OFF);
            } else if (event instanceof VmSuspendedEvent) {
                setState(VirtualMachinePowerState.SUSPENDED);
            } else if (event instanceof VmSuspendingEvent || event instanceof VmResumingEvent
                    || event instanceof VmStartingEvent || event instanceof VmStoppingEvent) {
                datacenter.getLog().debug("{0} is changing state", this);
                if (futureState == null || futureState.isDone()) {
                    futureState = new FutureReference<PowerState>();
                }
            }
        }
    }

    private void setState(VirtualMachinePowerState state) {
        synchronized (lock) {
            if (runtime.getPowerState().equals(state)) {
                return;
            }
            runtime.setPowerState(state);
            PowerState powerState = getState();
            if (futureState != null && !futureState.isDone()) {
                futureState.set(powerState);
                futureState = null;
            }
            datacenter.getLog().info("{0} has changed state to {1}", this, powerState);
        }
    }

    private class SetPowerStateOnSuccess extends ViTaskContinuation<Success> {
        private final VirtualMachinePowerState newState;

        public SetPowerStateOnSuccess(VirtualMachinePowerState newState) {
            this.newState = newState;
        }

        public void onSuccess() {
            setState(newState);
            set(Success.getInstance());
        }

        public void onError(LocalizedMethodFault error) {
            RemoteException remoteException = new RemoteException(error.getLocalizedMessage());
            set(remoteException.getMessage(), remoteException);
        }
    }
}
