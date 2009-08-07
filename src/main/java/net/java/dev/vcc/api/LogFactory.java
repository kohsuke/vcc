package net.java.dev.vcc.api;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Aug 7, 2009 Time: 3:43:53 PM To change this template use File |
 * Settings | File Templates.
 */
public interface LogFactory {

    /**
     * Gets the {@link Log} by name.
     *
     * @param name The name of the {@link Log} to get.
     *
     * @return The {@link Log}.
     */
    Log getLog(String name);

    /**
     * Gets the {@link Log} named {@code clazz.getName()}.
     *
     * @param clazz The class to get the {@link Log} for.
     *
     * @return The {@link Log}.
     */
    Log getLog(Class clazz);

    /**
     * Gets the {@link Log} by name.
     *
     * @param name       The name of the {@link Log} to get.
     * @param bundleName name of ResourceBundle to be used for localizing messages for this logger. May be
     *                   {@code null} if none of the messages require localization.
     *
     * @return The {@link Log}.
     */
    Log getLog(String name, String bundleName);

    /**
     * Gets the {@link Log} named {@code clazz.getName()}.
     *
     * @param clazz The class to get the {@link Log} for.
     * @param bundleName name of ResourceBundle to be used for localizing messages for this logger. May be
     *                   {@code null} if none of the messages require localization.
     *
     * @return The {@link Log}.
     */
    Log getLog(Class clazz, String bundleName);
}
