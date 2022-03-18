package io.fi0x.javalogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private Logger()
    {
        logFolder = new File(System.getenv("PROGRAMDATA") + File.separator + "JavaLogger");
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }
    /**
     * Get the Logger-singleton and create it if it does not exist yet.
     * @return The instance of the Logger-singleton.
     */
    public static Logger getInstance()
    {
        if(instance == null)
            instance = new Logger();

        return instance;
    }
    /**
     * Change the folder where logs should be stored.
     * If a log-file already exists in the old log-folder,
     * it will be ignored for any future logging
     * and the new log-folder location will be used instead.
     * @param logFolder The path where future logs should be stored
     *                  (Default is "PROGRAMDATA/JavaLogger").
     */
    public void setLogFolder(File logFolder)
    {
        this.logFolder = logFolder;
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }
    /**
     * Change the current debug-mode.
     * Logs whose 'DEBUG' method was set,
     * are only visible if this method is set to true.
     * @param isDebugMode Weather or not the Logger should work in debug-mode
     *                    (Default is false).
     */
    public void setDebug(boolean isDebugMode)
    {
        isDebug = isDebugMode;
    }
    /**
     * Change the current verbose-mode.
     * Logs whose 'VERBOSE' method was set,
     * are only visible if this method is set to true.
     * @param isVerboseMode Weather or not the Logger should work in verbose-mode
     *                      (Default is false).
     */
    public void setVerbose(boolean isVerboseMode)
    {
        isVerbose = isVerboseMode;
    }

    /**
     * Create and print a VERBOSE log.
     * VERBOSE logs use the default-verbose-color,
     * use the VERBOSE log-level, create no entry in the log-file
     * and are only visible if the Logger is set to verbose-mode.
     * @param text The message that should be logged.
     */
    public static void VERBOSE(String text)
    {
        getInstance().log(Objects.requireNonNull(LogSettings.getLOGFromTemplate(text, "verbose")));
    }
    /**
     * Create and print an INFO log.
     * INFO logs use the default-info-color,
     * use the INFO log-level, create an entry in the log-file
     * and are only visible if the Logger is either set to debug-mode or verbose-mode
     * @param text The message that should be logged.
     */
    public static void INFO(String text)
    {
        getInstance().log(Objects.requireNonNull(LogSettings.getLOGFromTemplate(text, "info")));
    }
    /**
     * Create and print a WARNING log.
     * WARNING logs use the default-warning-color,
     * use the WARNING log-level, create an entry in the log-file
     * and are always visible.
     * @param text The message that should be logged.
     */
    public static void WARNING(String text)
    {
        getInstance().log(Objects.requireNonNull(LogSettings.getLOGFromTemplate(text, "warning")));
    }
    /**
     * Create and print an ERROR log.
     * ERROR logs use the default-error-color,
     * use the ERROR log-level, create an entry in the log-file
     * and are always visible.
     * @param text The message that should be logged.
     */
    public static void ERROR(String text)
    {
        getInstance().log(Objects.requireNonNull(LogSettings.getLOGFromTemplate(text, "error")));
    }

    /**
     * Print the log provided with the settings that are stored in the LOG.
     * If the LOG is set to VERBOSE or DEBUG,
     * it will only be processed if the Logger has activated that mode.
     * @param log The log that should be processed.
     */
    public void log(LOG log)
    {
        if((log.onlyDebug && isDebug) || (log.onlyVerbose && isVerbose) || (!log.onlyVerbose && !log.onlyDebug))
        {
            String logOutput = createLogString(log);
            System.out.println(log.color + logOutput + LogSettings.RESET);

            if(log.fileEntry)
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
                    System.out.println(LogSettings.RED + "Something went wrong when writing to the log-file" + LogSettings.RESET);
                }
            }
        }
    }

    private static String createLogString(LOG log)
    {
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
            LOG l = new LOG("Could not create log-directory: " + logFolder)
                    .COLOR(LogSettings.RED)
                    .LEVEL(LEVEL.ERR)
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
            LOG l = new LOG("Could not create file: " + currentLogFile)
                    .COLOR(LogSettings.RED)
                    .LEVEL(LEVEL.ERR)
                    .CODE(0)
                    .EXCEPTION(e)
                    .FILE_ENTRY(false);
            log(l);
        }
    }

    /**
     * This class provides all information that are needed to print and / or save a log-entry.
     * It contains the message that should be logged,
     * in which color it should be printed,
     * the log-level it has,
     * an error code if required,
     * an exception if required
     * and weather it should be saved in a file or only printed.
     * It can also be set to be only visible if the Logger is in verbose- or debug-mode.
     */
    public static class LOG
    {
        private final String message;
        private String color = LogSettings.RESET;
        private LEVEL loglevel = LEVEL.INF;
        private int errorCode = 0;
        private Exception exception = null;
        private boolean fileEntry = true;
        private boolean onlyVerbose = false;
        private boolean onlyDebug = false;

        /**
         * Create a new LOG with the given text.
         * @param text The message that should be logged.
         */
        public LOG(String text)
        {
            message = text;
        }

        /**
         * Change the color that should be used when the LOG is printed.
         * @param colorCode The new color code
         *                  (Default will use the current console-color).
         * @return The current LOG to be used further.
         */
        public LOG COLOR(String colorCode)
        {
            color = colorCode;
            return this;
        }
        /**
         * Change the log-level that should be used for this LOG.
         * @param levelEnum The new log-level
         *                  (Default is INFO).
         * @return The current LOG to be used further.
         */
        public LOG LEVEL(LEVEL levelEnum)
        {
            loglevel = levelEnum;
            return this;
        }
        /**
         * Set a specific error code for the LOG to use.
         * @param exceptionCode The code that should be printed and saved
         *                      (Default will not use an error-code).
         * @return The current LOG to be used further.
         */
        public LOG CODE(int exceptionCode)
        {
            errorCode = exceptionCode;
            return this;
        }
        /**
         * Add an exception to the LOG.
         * The exception will only be visible in the log-file.
         * The message of this LOG will still be printed in the output.
         * @param e The exception to save in the log-file
         *          (Default will not save any exceptions).
         * @return The current LOG to be used further.
         */
        public LOG EXCEPTION(Exception e)
        {
            exception = e;
            return this;
        }
        /**
         * Set weather the LOG should be stored in the log-file or not.
         * @param shouldWriteToFile If the LOG should be written to the log-file
         *                          or only be printed in the output.
         *                          (Default will do both).
         * @return The current LOG to be used further.
         */
        public LOG FILE_ENTRY(boolean shouldWriteToFile)
        {
            fileEntry = shouldWriteToFile;
            return this;
        }
        /**
         * Set the debug-state of this LOG.
         * @param onlyInDebugMode If the log should only be active when the Logger is in debug-mode.
         *                        (Default is false).
         * @return The current LOG to be used further.
         */
        public LOG DEBUG(boolean onlyInDebugMode)
        {
            onlyDebug = onlyInDebugMode;
            return this;
        }
        /**
         * Set the verbose-state of this LOG.
         * @param onlyInVerboseMode If the log should only be active when the Logger is in verbose-mode.
         *                          (Default is false).
         * @return The current LOG to be used further.
         */
        public LOG VERBOSE(boolean onlyInVerboseMode)
        {
            onlyVerbose = onlyInVerboseMode;
            return this;
        }

        static LOG copyLogSettings(String text, LOG original)
        {
            return new LOG(text).COLOR(original.color).LEVEL(original.loglevel).FILE_ENTRY(original.fileEntry).DEBUG(original.onlyDebug).VERBOSE(original.onlyVerbose);
        }
    }

    /**
     * This enum is used to set the severity of a log-entry.
     */
    public enum LEVEL
    {
        VER,
        INF,
        WRN,
        ERR
    }
}
