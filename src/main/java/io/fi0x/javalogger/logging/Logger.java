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
    private boolean smallLog;

    static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";

    static Map<String, LogTemplate> templates = new HashMap<String,LogTemplate>() {{
        put(TEMPLATE.VERBOSE.name(), new LogTemplate(WHITE, BLACK_BACKGROUND, "VER", false, true, false, false, false, "LOG"));
        put(TEMPLATE.INFO.name(), new LogTemplate(WHITE, BLACK_BACKGROUND, "INF", true, true, true, false, false, "LOG"));
        put(TEMPLATE.WARNING.name(), new LogTemplate(YELLOW, BLACK_BACKGROUND, "WRN", true, false, false, false, true, "LOG"));
        put(TEMPLATE.ERROR.name(), new LogTemplate(RED, BLACK_BACKGROUND, "ERR", true, false, false, false, true, "LOG"));
    }};

    private Logger()
    {
        logFolder = new File(System.getenv("PROGRAMDATA") + File.separator + "JavaLogger");
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }
    /**
     * Get the {@link Logger}-singleton and create it if it does not exist yet.
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
     * @param isVerboseMode Weather or not the {@link Logger} should work in verbose-mode
     *                      (Default is false).
     */
    public void setVerbose(boolean isVerboseMode)
    {
        isVerbose = isVerboseMode;
    }
    /**
     * Change the way {@link LogEntry}s are displayed.
     * Using small-logs will remove all prefixes from logging.
     * This has the same effect as the plaintext setting in a single {@link LogEntry},
     * but will affect all logging.
     * @param ignorePrefixes Weather or not the {@link Logger} should use small-logs
     *                       (Default is false).
     */
    public void setSmallLog(boolean ignorePrefixes)
    {
        smallLog = ignorePrefixes;
    }

    /**
     * Print the {@link LogEntry} provided with the settings that are stored in the {@link LogEntry}.
     * If the {@link LogEntry} is set to VERBOSE or DEBUG,
     * it will only be processed if the {@link Logger} has activated that mode.
     * @param log The {@link LogEntry} that should be processed.
     */
    public static void log(LogEntry log)
    {
        if((log.onlyDebug && getInstance().isDebug) || (log.onlyVerbose && getInstance().isVerbose) || (!log.onlyVerbose && !log.onlyDebug))
        {
            String logOutput = createLogString(log);
            System.out.println(log.color + log.background + logOutput + RESET);

            if(log.fileEntry)
                getInstance().addEntryToLogFile(log, logOutput);

            if(log.mixpanel)
                sendMixpanelMessage(log);
        }
    }
    /**
     * Create a {@link LogEntry} with the specified text and {@link LogTemplate}.
     * @param text The message to log.
     * @param templateName The name of the {@link LogTemplate} that should be used.
     * @return True if logging was successful, False if the {@link LogTemplate} does not exist.
     */
    public static boolean log(String text, String templateName)
    {
        LogEntry log;
        try
        {
            log = new LogEntry(text, templateName);
        } catch(IllegalArgumentException ignored)
        {
            return false;
        }

        log(log);
        return true;
    }
    /**
     * @see #log(String, String)
     */
    public static boolean log(String text, Enum<?> template)
    {
        return log(text, template.name());
    }

    /**
     * Create a new {@link LogTemplate} for logging
     * that can be used to quickly create a new {@link LogEntry}.
     * @param templateName The name which is required to find the {@link LogTemplate} again.
     * @param colorCode The color which will be used in the console output.
     * @param backgroundColorCode The background color which will be used in the console output.
     * @param logLevel The logging-level.
     * @param writeToFile If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        boolean isNew = !templates.containsKey(templateName);
        templates.put(templateName, new LogTemplate(colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName));
        return isNew;
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName.name(), colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName.name(), colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, false, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, false, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, false, false, false, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, writeToFile, false, false, false, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, true, false, false, false, false, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel)
    {
        return createNewTemplate(templateName, colorCode, BLACK_BACKGROUND, logLevel, true, false, false, false, false, "LOG");
    }
    /**
     * Update a specific {@link LogTemplate}.
     * @deprecated Use the createNewTemplate method instead to overwrite existing templates.
     * @param templateName The name of the {@link LogTemplate}.
     * @param colorCode The new color.
     * @param logLevel The new logging-level.
     * @param writeToFile If {@link LogEntry}s written with this {@link LogTemplate} should be saved in a log-file.
     * @param onlyDebug If {@link LogEntry}s with these settings should only be visible in debug-mode.
     * @param onlyVerbose If {@link LogEntry}s with these settings should only be visible in verbose-mode.
     * @param hidePrefix If only the actual message without timestamp, logging-level and error-code should be shown.
     * @param mixpanelMessage If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was updated successfully, False if the name was not found.
     */
    @Deprecated(since = "1.1.6", forRemoval = true)
    public static boolean updateTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        if(!templates.containsKey(templateName))
            return false;
        templates.put(templateName, new LogTemplate(colorCode, BLACK_BACKGROUND, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName));
        return true;
    }
    /**
     * @see #updateTemplate(String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    @Deprecated(since = "1.1.6", forRemoval = true)
    public static boolean updateTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return updateTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * @see #updateTemplate(String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    @Deprecated(since = "1.1.6", forRemoval = true)
    public static boolean updateTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return updateTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false);
    }
    /**
     * @see #updateTemplate(String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    @Deprecated(since = "1.1.6", forRemoval = true)
    public static boolean updateTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return updateTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, false, false);
    }
    /**
     * @see #updateTemplate(String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    @Deprecated(since = "1.1.6", forRemoval = true)
    public static boolean updateTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return updateTemplate(templateName, colorCode, logLevel, writeToFile, false, false, false, false);
    }
    /**
     * @see #updateTemplate(String, String, String, boolean, boolean, boolean, boolean, boolean, String)
     */
    @Deprecated(since = "1.1.6", forRemoval = true)
    public static boolean updateTemplate(String templateName, String colorCode, String logLevel)
    {
        return updateTemplate(templateName, colorCode, logLevel, true, false, false, false, false);
    }

    private static String createLogString(LogEntry log)
    {
        if(log.plainText || getInstance().smallLog)
            return log.message;

        String errorCode = log.errorCode == 0 ? "[---]" : "[" + log.errorCode + "]";
        String prefix = "[" + log.loglevel + "]";

        return getLogEntryDate() + prefix + errorCode + log.message;
    }
    private static String getLogEntryDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
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
            System.out.println(RED + "Something went wrong when writing to the log-file" + RESET);
        }
    }

    private static void sendMixpanelMessage(LogEntry entry)
    {
        Map<String, String> props = new HashMap<>();

        props.put(createLogString(entry), entry.message);
        props.put("logLevel", entry.loglevel);
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
                    .COLOR(RED)
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
                    .COLOR(RED)
                    .LEVEL("ERR")
                    .CODE(0)
                    .EXCEPTION(e)
                    .FILE_ENTRY(false);
            log(l);
        }
    }

    public enum TEMPLATE
    {
        VERBOSE,
        INFO,
        WARNING,
        ERROR
    }
}
