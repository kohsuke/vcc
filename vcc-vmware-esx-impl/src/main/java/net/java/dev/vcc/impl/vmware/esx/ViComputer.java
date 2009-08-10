package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ComputerSnapshot;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.Success;
import net.java.dev.vcc.api.commands.StartComputer;
import net.java.dev.vcc.api.commands.StopComputer;
import net.java.dev.vcc.api.commands.SuspendComputer;
import net.java.dev.vcc.api.commands.RestartComputer;
import net.java.dev.vcc.spi.AbstractComputer;
import net.java.dev.vcc.spi.AbstractManagedObject;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;

import com.vmware.vim25.Event;
import com.vmware.vim25.VmResourcePoolMovedEvent;
import com.vmware.vim25.VmReconfiguredEvent;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VmPoweredOnEvent;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VmPoweredOffEvent;
import com.vmware.vim25.VmSuspendedEvent;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.VmConfigFaultFaultMsg;

final class ViComputer extends AbstractComputer implements ViEventReceiver {

    private final ViDatacenter datacenter;

    private ViDatacenterResourceGroup parent;

    private String name;

    private VirtualMachineConfigInfo config;
    private VirtualMachineRuntimeInfo runtime;
    private VirtualMachineSnapshotInfo snapshot;

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
        if (command instanceof StartComputer) {
            try {
                datacenter.addPendingTask(command, datacenter.getConnection().getProxy().powerOnVMTask(getId().getMORef(), null)
                );
            } catch (FileFaultFaultMsg e) {
                ((StartComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (InsufficientResourcesFaultFaultMsg e) {
                ((StartComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (InvalidStateFaultMsg e) {
                ((StartComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (RuntimeFaultFaultMsg e) {
                ((StartComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (TaskInProgressFaultMsg e) {
                ((StartComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (VmConfigFaultFaultMsg e) {
                ((StartComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            }
        } else if (command instanceof StopComputer) {
            try {
                datacenter.addPendingTask(command, datacenter.getConnection().getProxy().powerOffVMTask(getId().getMORef())
                );
            } catch (InvalidStateFaultMsg e) {
                ((StopComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (RuntimeFaultFaultMsg e) {
                ((StopComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (TaskInProgressFaultMsg e) {
                ((StopComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            }
        } else if (command instanceof SuspendComputer) {
            try {
                datacenter.addPendingTask(command, datacenter.getConnection().getProxy().suspendVMTask((getId().getMORef()))
                );
            } catch (InvalidStateFaultMsg e) {
                ((SuspendComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (RuntimeFaultFaultMsg e) {
                ((SuspendComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            } catch (TaskInProgressFaultMsg e) {
                ((SuspendComputer) command).setSubmitted(new CompletedFuture<Success>(e.getMessage(), e));
            }
        } else {
            command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
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

    public boolean isStateChanging() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Future<PowerState> getFutureState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<ComputerSnapshot> getSnapshots() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Host> getAllowedHosts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return config == null ? null : config.getAnnotation();
    }

    @Override
    public ViComputerId getId() {
        return (ViComputerId) super.getId();
    }

    void setParent(ViDatacenterResourceGroup parent) {
        this.parent = parent;
    }

    void setName(String name) {
        this.name = name;
    }

    public synchronized void receiveEvent(Event event) {
        if (event instanceof VmResourcePoolMovedEvent) {
            VmResourcePoolMovedEvent rpMoved = (VmResourcePoolMovedEvent) event;
            AbstractManagedObject oldParent = datacenter.getManagedObject(rpMoved.getOldParent().getResourcePool());
            AbstractManagedObject newParent = datacenter.getManagedObject(rpMoved.getOldParent().getResourcePool());
            if (oldParent instanceof ViHostResourceGroup) {
                ((ViHostResourceGroup) oldParent).removeComputer(this);
                datacenter.getLog().info("Removing {0} from {1}", this, oldParent);
            } else if (oldParent instanceof ViHost) {
                ((ViHost) oldParent).removeComputer(this);
                datacenter.getLog().info("Removing {0} from {1}", this, oldParent);
            } else {
                datacenter.getLog().info("No old parent");
            }
            if (newParent instanceof ViHostResourceGroup) {
                ((ViHostResourceGroup) newParent).addComputer(this);
                datacenter.getLog().info("Adding {0} to {1}", this, newParent);
            } else if (newParent instanceof ViHost) {
                ((ViHost) newParent).addComputer(this);
                datacenter.getLog().info("Adding {0} to {1}", this, newParent);
            } else {
                datacenter.getLog().info("No new parent");
            }
        } else if (event instanceof VmReconfiguredEvent) {
            VmReconfiguredEvent reconf = (VmReconfiguredEvent) event;
            // TODO update the new config spec
        } else if (event instanceof VmPoweredOnEvent) {
            runtime.setPowerState(VirtualMachinePowerState.POWERED_ON);
        } else if (event instanceof VmPoweredOffEvent) {
            runtime.setPowerState(VirtualMachinePowerState.POWERED_OFF);
        } else if (event instanceof VmSuspendedEvent) {
            runtime.setPowerState(VirtualMachinePowerState.SUSPENDED);
        }
    }
}
