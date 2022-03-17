package io.fi0x.javalogger;

import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Logger
{
    private static Logger instance;

    private File logFolder;
    private File currentLogFile;

    private static final String RESET = "\u001B[0m";
    public static final String WHITE = "\u001B[37m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    private Logger()
    {
        logFolder = new File(System.getenv("APPDATA") + File.separator + "JavaLogger");
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }
    public static Logger getInstance()
    {
        if(instance == null)
            instance = new Logger();

        return instance;
    }
    public void setLogFolder(File logFolder)
    {
        this.logFolder = logFolder;
        currentLogFile = new File(logFolder.getPath() + File.separator + getLogFileDate() + ".log");
    }

    public static void VERBOSE(String text)
    {
        LOG l = new LOG(text).COLOR(WHITE).LEVEL(LEVEL.VER).CODE(0).EXCEPTION(null).FILE_ENTRY(false);
        getInstance().log(l);
    }
    public static void INFO(String text)
    {
        LOG l = new LOG(text).COLOR(WHITE).LEVEL(LEVEL.INF).CODE(0).EXCEPTION(null).FILE_ENTRY(true);
        getInstance().log(l);
    }
    public static void WARNING(String text)
    {
        LOG l = new LOG(text).COLOR(YELLOW).LEVEL(LEVEL.WRN).CODE(0).EXCEPTION(null).FILE_ENTRY(true);
        getInstance().log(l);
    }
    public static void ERROR(String text)
    {
        LOG l = new LOG(text).COLOR(RED).LEVEL(LEVEL.ERR).CODE(0).EXCEPTION(null).FILE_ENTRY(true);
        getInstance().log(l);
    }

    public void log(LOG log)
    {
        String logOutput = createLogString(log);
        System.out.println(log.color + logOutput + RESET);

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
                System.out.println(RED + "Something went wrong when writing to the log-file" + RESET);
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
                    .COLOR(RED)
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
                    .COLOR(RED)
                    .LEVEL(LEVEL.ERR)
                    .CODE(0)
                    .EXCEPTION(e)
                    .FILE_ENTRY(false);
            log(l);
        }
    }

    public static class LOG
    {
        private final String message;
        private String color = RESET;
        private LEVEL loglevel = LEVEL.INF;
        private int errorCode = 0;
        private Exception exception = null;
        private boolean fileEntry = true;

        public LOG(String text)
        {
            message = text;
        }

        public LOG COLOR(String colorCode)
        {
            color = colorCode;
            return this;
        }
        public LOG LEVEL(LEVEL levelEnum)
        {
            loglevel = levelEnum;
            return this;
        }
        public LOG CODE(int exceptionCode)
        {
            errorCode = exceptionCode;
            return this;
        }
        public LOG EXCEPTION(Exception e)
        {
            exception = e;
            return this;
        }
        public LOG FILE_ENTRY(boolean shouldWriteToFile)
        {
            fileEntry = shouldWriteToFile;
            return this;
        }
    }

    public enum LEVEL
    {
        VER,
        INF,
        WRN,
        ERR
    }
}
