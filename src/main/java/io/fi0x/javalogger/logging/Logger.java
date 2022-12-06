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
    private boolean consoleExceptions;

    static Map<String, LogTemplate> templates = new HashMap<String,LogTemplate>() {{
        put(TEMPLATE.VERBOSE.name(), new LogTemplate(LogColor.WHITE, "", "VER", false, true, false, false, false, false, "LOG"));
        put(TEMPLATE.INFO.name(), new LogTemplate(LogColor.WHITE, "", "INF", true, true, true, false, false, false, "LOG"));
        put(TEMPLATE.WARNING.name(), new LogTemplate(LogColor.YELLOW, "", "WRN", true, false, false, false, false, true, "LOG"));
        put(TEMPLATE.ERROR.name(), new LogTemplate(LogColor.RED, "", "ERR", true, false, false, false, false, true, "LOG"));
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
     * Change the way Exceptions are displayed in the console.
     * Using console-exceptions will print out all StackTraces of exceptions in your console.
     * This has the same effect as the consoleException setting in a single {@link LogEntry},
     * but will affect all logging.
     * @param showExceptionsInConsole Weather or not the {@link Logger} should print StackTraces in the console
     *                                (Default is false).
     */
    public void setConsoleExceptions(boolean showExceptionsInConsole)
    {
        consoleExceptions = showExceptionsInConsole;
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
            System.out.println(log.color + log.background + logOutput + LogColor.RESET);

            if(log.exception != null)
            {
                if(getInstance().consoleExceptions || log.consoleException)
                    log.exception.printStackTrace();
            }

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
     * @param e The exception that should get logged (Default is null).
     * @param errorCode The code for the error that occured (Default is 0).
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
     * @see #log(String, String, Exception, int)
     */
    public static boolean log(String text, Enum<?> templateName, Exception e, int errorCode)
    {
        return log(text, templateName.name(), e, errorCode);
    }
    /**
     * @see #log(String, String, Exception, int)
     */
    public static boolean log(String text, String templateName, Exception e)
    {
        return log(text, templateName, e, 0);
    }
    /**
     * @see #log(String, String, Exception, int)
     */
    public static boolean log(String text, Enum<?> templateName, Exception e)
    {
        return log(text, templateName, e, 0);
    }
    /**
     * @see #log(String, String, Exception, int)
     */
    public static boolean log(String text, String templateName)
    {
        return log(text, templateName, null);
    }
    /**
     * @see #log(String, String, Exception, int)
     */
    public static boolean log(String text, Enum<?> template)
    {
        return log(text, template, null);
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
     * @param exceptionsInConsole If {@link LogEntry}s whith these settings should print their exceptions in the console.
     * @param mixpanelMessage If the {@link LogEntry} should be sent to Mixpanel.
     * @param mixpanelName The name of the Mixpanel-event.
     * @return True if the {@link LogTemplate} was created successfully, False if the {@link LogTemplate} was overwritten.
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName)
    {
        boolean isNew = !templates.containsKey(templateName);
        templates.put(templateName, new LogTemplate(colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName));
        return isNew;
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean exceptionsInConsole, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName.name(), colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, exceptionsInConsole, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String backgroundColorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName.name(), colorCode, backgroundColorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, "", logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage, String mixpanelName)
    {
        return createNewTemplate(templateName, colorCode, "", logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, mixpanelName);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix, boolean mixpanelMessage)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, mixpanelMessage, "LOG");
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug, boolean hidePrefix)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, hidePrefix, false);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, false);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile, boolean onlyVerbose, boolean onlyDebug)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, onlyVerbose, onlyDebug, false);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, false, false);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(Enum<?> templateName, String colorCode, String logLevel, boolean writeToFile)
    {
        return createNewTemplate(templateName, colorCode, logLevel, writeToFile, false, false);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
    public static boolean createNewTemplate(String templateName, String colorCode, String logLevel)
    {
        return createNewTemplate(templateName, colorCode, logLevel, true);
    }
    /**
     * @see #createNewTemplate(String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean, String)
     */
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
            LogEntry l = new LogEntry("Something went wrong when writing to the log-file")
                    .COLOR(LogColor.RED)
                    .LEVEL("ERR")
                    .EXCEPTION(e)
                    .CODE(0)
                    .FILE_ENTRY(false);
            Logger.log(l);
        }
    }

    private static void sendMixpanelMessage(LogEntry entry)
    {
        Map<String, String> props = new HashMap<>();

        props.put("message", createLogString(entry));
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
                    .COLOR(LogColor.RED)
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
                    .COLOR(LogColor.RED)
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
