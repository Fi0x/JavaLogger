package io.fi0x.javalogger;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the settings for default logging behaviour.
 */
public class LogSettings
{
    static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    static Map<String, Logger.LOG> defaultSettings = new HashMap<String, Logger.LOG>(){{
        put("verbose", new Logger.LOG(null).COLOR(WHITE).LEVEL(Logger.LEVEL.VER).FILE_ENTRY(false).DEBUG(false).VERBOSE(true));
        put("info", new Logger.LOG(null).COLOR(WHITE).LEVEL(Logger.LEVEL.INF).FILE_ENTRY(true).DEBUG(true).VERBOSE(true));
        put("warning", new Logger.LOG(null).COLOR(YELLOW).LEVEL(Logger.LEVEL.WRN).FILE_ENTRY(true).DEBUG(false).VERBOSE(false));
        put("error", new Logger.LOG(null).COLOR(RED).LEVEL(Logger.LEVEL.ERR).FILE_ENTRY(true).DEBUG(false).VERBOSE(false));
    }};

    /**
     * Create a new template for logging
     * that can be used to create new logs quickly.
     * @param templateName The name which is required to find the template again.
     * @param colorCode The color which will be used in the console output.
     * @param logLevel The severity level.
     * @param writeToFile If logs written with this template should be saved in a log-file.
     * @param onlyDebug If logs with these settings should only be visible in debug-mode.
     * @param onlyVerbose If logs with these settings should only be visible in verbose-mode.
     * @return True if the template was created successfully, False if the template already existed.
     */
    public static boolean createNewTemplate(String templateName, String colorCode, Logger.LEVEL logLevel, boolean writeToFile, boolean onlyDebug, boolean onlyVerbose)
    {
        if(defaultSettings.containsKey(templateName))
            return false;
        defaultSettings.put(templateName, new Logger.LOG(null).COLOR(colorCode).LEVEL(logLevel).FILE_ENTRY(writeToFile).DEBUG(onlyDebug).VERBOSE(onlyVerbose));
        return true;
    }

    /**
     * Create a new LOG with the settings specified by the template.
     * @param text The message for the log-entry.
     * @param templateName The name of the template that should be used.
     * @return A new LOG with the provided settings and text.
     */
    public static Logger.LOG getLOGFromTemplate(String text, String templateName)
    {
        if(!defaultSettings.containsKey(templateName))
            return null;

        return Logger.LOG.copyLogSettings(text, defaultSettings.get(templateName));
    }
}
