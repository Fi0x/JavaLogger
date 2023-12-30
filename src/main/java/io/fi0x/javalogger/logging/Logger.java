package io.fi0x.javalogger.logging;

import io.fi0x.javalogger.mixpanel.MixpanelHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class can be used for logging to the default output
 * and to a log-file.
 */
public class Logger
{
    private static Logger instance;

    private File logFolder;
    private File currentLogFile;
    private boolean isDebug;
    private boolean isVerbose;
    private int verboseLevel = 0;
    private boolean smallLog;
    private boolean consoleExceptions;
    private boolean projectName;

    static Map<String, LogTemplate> templates = new HashMap<>()
    {{
        put(LogLevel.VERBOSE.name(), new LogTemplate(LogColor.WHITE, "", "VER", false, true, 0, false, false, false, false, "LOG", ""));
        put(LogLevel.VVERBOSE.name(), new LogTemplate(LogColor.WHITE, "", "VER", false, true, 1, false, false, false, false, "LOG", ""));
        put(LogLevel.VVVERBOSE.name(), new LogTemplate(LogColor.WHITE, "", "VER", false, true, 2, false, false, false, false, "LOG", ""));
        put(LogLevel.INFO.name(), new LogTemplate(LogColor.WHITE_BRIGHT, "", "INF", true, false, 0, true, false, false, false, "LOG", ""));
        put(LogLevel.WARNING.name(), new LogTemplate(LogColor.YELLOW_BRIGHT, "", "WRN", true, false, 0, false, false, false, true, "LOG", ""));
        put(LogLevel.ERROR.name(), new LogTemplate(LogColor.RED_BRIGHT, "", "ERR", true, false, 0, false, false, true, true, "LOG", ""));
        put(LogLevel.SPECIAL.name(), new LogTemplate(LogColor.GREEN, "", "SPE", false, false, 0, false, false, false, false, "LOG", ""));
        put(LogLevel.RESPONSE.name(), new LogTemplate(LogColor.BLUE, "", "RES", false, false, 0, false, false, false, false, "LOG", ""));
        put(LogLevel.QUESTION.name(), new LogTemplate(LogColor.CYAN_BRIGHT, "", "QUE", false, false, 0, false, false, false, false, "LOG", ""));
        put(LogLevel.CLEAN_INFO.name(), new LogTemplate(LogColor.WHITE_BRIGHT, "", "INF", true, false, 0, true, true, false, false, "LOG", ""));
        put(LogLevel.CLEAN_SPECIAL.name(), new LogTemplate(LogColor.GREEN, "", "SPE", false, false, 0, false, true, false, false, "LOG", ""));
        put(LogLevel.CLEAN_RESPONSE.name(), new LogTemplate(LogColor.BLUE, "", "RES", false, false, 0, false, true, false, false, "LOG", ""));
        put(LogLevel.CLEAN_QUESTION.name(), new LogTemplate(LogColor.CYAN_BRIGHT, "", "QUE", false, false, 0, false, true, false, false, "LOG", ""));
    }};

    private Logger()
    {
        logFolder = new File(System.getenv("PROGRAMDATA") + File.separator + "JavaLogger");
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }
    /**
     * Get the {@link Logger}-singleton and create it if it does not exist yet.
     *
     * @return The instance of the {@link Logger}-singleton.
     */
    public static Logger getInstance()
    {
        if(instance == null)
            instance = new Logger();

        return instance;
    }
    /**
     * Change the folder where log-files should be stored.
     * If a log-file already exists in the current log-folder,
     * it will be ignored for any future logging
     * and the new log-folder location will be used instead.
     *
     * @param logFolder The path where future log-files should be stored
     *                  (Default is "PROGRAMDATA/JavaLogger").
     */
    public void setLogFolder(File logFolder)
    {
        this.logFolder = logFolder;
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }
    /**
     * Change the current debug-mode.
     * {@link LogEntry}s whose 'DEBUG' method was set,
     * are only visible if this method is set to true.
     *
     * @param isDebugMode Weather or not the {@link Logger} should work in debug-mode
     *                    (Default is false).
     */
    public void setDebug(boolean isDebugMode)
    {
        isDebug = isDebugMode;
    }
    /**
     * Change the current verbose-mode.
     * {@link LogEntry}s whose 'VERBOSE' method was set,
     * are only visible if this method is set to true.
     *
     * @param isVerboseMode Weather or not the {@link Logger} should work in verbose-mode
     *                      (Default is false).
     */
    public void setVerbose(boolean isVerboseMode)
    {
        isVerbose = isVerboseMode;
    }
    /**
     * Change the current verbose-level. This will show messages
     * with the selected verbose level or lower.
     *
     * @param level How many different verbose levels should be active.
     *              (Default is 0)
     */
    public void setVerboseLevel(int level)
    {
        verboseLevel = level;
    }
    /**
     * Change the way {@link LogEntry}s are displayed.
     * Using small-logs will remove all prefixes from logging.
     * This has the same effect as the plaintext setting in a single {@link LogEntry},
     * but will affect all logging.
     *
     * @param ignorePrefixes Weather or not the {@link Logger} should use small-logs
     *                       (Default is false).
     */
    public void setSmallLog(boolean ignorePrefixes)
    {
        smallLog = ignorePrefixes;
    }
    /**
     * Change the way Exceptions are displayed in the console.
     * Using console-exceptions will print out all StackTraces of exceptions in your console.
     * This has the same effect as the consoleException setting in a single {@link LogEntry},
     * but will affect all logging.
     *
     * @param showExceptionsInConsole Weather or not the {@link Logger} should print StackTraces in the console
     *                                (Default is false).
     */
    public void setConsoleExceptions(boolean showExceptionsInConsole)
    {
        consoleExceptions = showExceptionsInConsole;
    }
    /**
     * Deactivating this variable will remove all project names from showing up in logging.
     *
     * @param showProjectName Weather or not the {@link Logger} should add the project name to logging
     *                        (Default is true).
     */
    public void setProjectName(boolean showProjectName)
    {
        projectName = showProjectName;
    }

    /**
     * Print the {@link LogEntry} provided with the settings that are stored in the {@link LogEntry}.
     * If the {@link LogEntry} is set to VERBOSE or DEBUG,
     * it will only be processed if the {@link Logger} has activated that mode.
     *
     * @param log The {@link LogEntry} that should be processed.
     */
    public static void log(LogEntry log)
    {
        String logOutput = createLogString(log);
        boolean debugOK = !log.onlyDebug || getInstance().isDebug;
        boolean verboseOK = !log.onlyVerbose || getInstance().isVerbose;
        boolean verboseLevelOK = log.verboseLevel <= getInstance().verboseLevel;

        if(debugOK && verboseOK && verboseLevelOK)
        {
            System.out.println(log.color + log.background + logOutput + LogColor.RESET);

            if(log.exception != null)
            {
                if(getInstance().consoleExceptions || log.consoleException)
                    log.exception.printStackTrace();
            }
        }

        if(log.fileEntry)
            getInstance().addEntryToLogFile(log, logOutput);

        if(log.mixpanel)
            sendMixpanelMessage(log);
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     *
     * @param text         The message to log.
     * @param templateName The name of the {@link LogTemplate} that should be used.
     * @param e            The exception that should get logged (Default is null).
     * @param errorCode    The code for the error that occured (Default is 0).
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    public static boolean log(String text, String templateName, Exception e, int errorCode)
    {
        LogEntry log;
        try
        {
            log = new LogEntry(text, templateName);
        } catch(IllegalArgumentException ignored)
        {
            return false;
        }

        log.EXCEPTION(e);
        log.CODE(errorCode);

        log(log);
        return true;
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     *
     * @param text         The message to log.
     * @param templateName The enum that should be used as a name for the {@link LogTemplate}.
     * @param e            The exception that should get logged (Default is null).
     * @param errorCode    The code for the error that occured (Default is 0).
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    public static boolean log(String text, Enum<?> templateName, Exception e, int errorCode)
    {
        return log(text, templateName.name(), e, errorCode);
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     *
     * @param text         The message to log.
     * @param templateName The name of the {@link LogTemplate} that should be used.
     * @param e            The exception that should get logged (Default is null).
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    @Deprecated
    public static boolean log(String text, String templateName, Exception e)
    {
        return log(text, templateName, e, 0);
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     *
     * @param text         The message to log.
     * @param templateName The enum that should be used as a name for the {@link LogTemplate}.
     * @param e            The exception that should get logged (Default is null).
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    @Deprecated
    public static boolean log(String text, Enum<?> templateName, Exception e)
    {
        return log(text, templateName, e, 0);
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     *
     * @param text         The message to log.
     * @param templateName The name of the {@link LogTemplate} that should be used.
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    public static boolean log(String text, String templateName)
    {
        return log(text, templateName, null);
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     *
     * @param text     The message to log.
     * @param template The enum that should be used as a name for the {@link LogTemplate}.
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    public static boolean log(String text, Enum<?> template)
    {
        return log(text, template, null);
    }

    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param verboseLevel        The minimum required level for messages with this {@link LogEntry} to be displayed if they are onlyVerbose.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @param projectName         The name of the project that should be included in the logs as a label.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, int verboseLevel, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName, String projectName)
    {
        boolean isNew = !templates.containsKey(templateName);
        templates.put(templateName, new LogTemplate(colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, verboseLevel, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName, projectName));
        return isNew;
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param verboseLevel        The minimum required level for messages with this {@link LogEntry} to be displayed if they are onlyVerbose.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @param projectName         The name of the project that should be included in the logs as a label.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, int verboseLevel, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName, String projectName)
    {
        return createNewTemplate(templateName.name(), colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, verboseLevel, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName, projectName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param verboseLevel        The minimum required level for messages with this {@link LogEntry} to be displayed if they are onlyVerbose.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, int verboseLevel, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, 0, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName, "");
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param verboseLevel        The minimum required level for messages with this {@link LogEntry} to be displayed if they are onlyVerbose.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, int verboseLevel, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, verboseLevel, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName, "");
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, 0, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, 0, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false, mixpanelMessage, mixpanelName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false, mixpanelMessage, mixpanelName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, "", logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName        The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, "", logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage     If the {@link LogEntry} should be sent to Mixpanel.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix          If only the actual message without timestamp, logging-level and error-code should be shown.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, false);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug           If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose         If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, false);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, false, false);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @param writeToFile         If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, false, false);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The name which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel)
    {
        return createNewTemplate(templateName, colorCode, logLevel, true);
    }
    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     *
     * @param templateName        The enum which is required to find the {@link LogTemplate} again.
     * @param colorCode           The color which will be used in the console output.
     * @param logLevel            The logging-level.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    @Deprecated
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel)
    {
        return createNewTemplate(templateName, colorCode, logLevel, true);
    }

    private static String createLogString(LogEntry log)
    {
        if(log.plainText || getInstance().smallLog)
            return log.message;

        String errorCode = log.errorCode == 0 ? "[---]" : "[" + log.errorCode + "]";
        String prefix = "[" + log.loglevel + "]";
        String projectName = getInstance().projectName ? "[" + log.projectName + "]" : "";

        return getLogEntryDate() + prefix + errorCode + projectName + log.message;
    }
    private static String getLogEntryDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();

        return "[" + dtf.format(now) + "]";
    }

    private void addEntryToLogFile(LogEntry log, String logOutput)
    {
        if(!currentLogFile.exists())
            createLogFile();

        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(currentLogFile.toPath(), StandardCharsets.UTF_8));

            fileContent.add(logOutput);
            if(log.exception != null) fileContent.add("\t" + Arrays.toString(log.exception.getStackTrace())
                    .replace(", ", "\n\t")
                    .replace("[", "")
                    .replace("]", ""));

            Files.write(currentLogFile.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            LogEntry l = new LogEntry("Something went wrong when writing to the log-file")
                    .COLOR(LogColor.RED_BRIGHT)
                    .LEVEL("ERR")
                    .EXCEPTION(e)
                    .CODE(600)
                    .FILE_ENTRY(false)
                    .PROJECTNAME("JavaLogger");
            Logger.log(l);
        }
    }

    private static void sendMixpanelMessage(LogEntry entry)
    {
        Map<String, String> props = new HashMap<>();

        props.put("message", createLogString(entry));
        props.put("logLevel", entry.loglevel);
        props.put("projectName", entry.projectName);
        if(entry.errorCode != 0)
            props.put("errorCode", String.valueOf(entry.errorCode));
        if(entry.exception != null)
            props.put("exception", Arrays.toString(entry.exception.getStackTrace()));

        MixpanelHandler.addMessage(entry.mixpanelEventName, props);
    }

    private static String getLogFileDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }
    private void createLogFile()
    {
        try
        {
            Files.createDirectories(logFolder.toPath());
        } catch(IOException e)
        {
            LogEntry l = new LogEntry("Could not create log-directory: " + logFolder)
                    .COLOR(LogColor.RED_BRIGHT)
                    .LEVEL("ERR")
                    .CODE(0)
                    .EXCEPTION(e)
                    .FILE_ENTRY(false);
            log(l);
        }
        try
        {
            currentLogFile.createNewFile();
        } catch(IOException e)
        {
            LogEntry l = new LogEntry("Could not create file: " + currentLogFile)
                    .COLOR(LogColor.RED_BRIGHT)
                    .LEVEL("ERR")
                    .CODE(0)
                    .EXCEPTION(e)
                    .FILE_ENTRY(false);
            log(l);
        }
    }
}
