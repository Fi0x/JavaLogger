package io.fi0x.javalogger.logging;

/**
 * This class is designed to enable an easy way to log important messages with pre-defined settings.
 * These settings can be changed by overriding the default LogLevel types.
 */
public class LOG
{
    private LOG()
    {
    }
    /**
     * This method will create a log message with the INFO template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     */
    public static void INFO(String message, String projectName)
    {
        LogEntry l = new LogEntry(message, LogLevel.INFO)
                .PROJECTNAME(projectName);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the INFO template.
     *
     * @param message The message that will be displayed in the log.
     */
    public static void INFO(String message)
    {
        LogEntry l = new LogEntry(message, LogLevel.INFO);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     * @param errorCode   The error code that will be displayed.
     * @param exception   The exception that will be printed.
     */
    public static void WARN(String message, String projectName, int errorCode, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .PROJECTNAME(projectName)
                .CODE(errorCode)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message   The message that will be displayed in the log.
     * @param errorCode The error code that will be displayed.
     * @param exception The exception that will be printed.
     */
    public static void WARN(String message, int errorCode, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .CODE(errorCode)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     * @param errorCode   The error code that will be displayed.
     */
    public static void WARN(String message, String projectName, int errorCode)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .PROJECTNAME(projectName)
                .CODE(errorCode);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     * @param exception   The exception that will be printed.
     */
    public static void WARN(String message, String projectName, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .PROJECTNAME(projectName)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message   The message that will be displayed in the log.
     * @param errorCode The error code that will be displayed.
     */
    public static void WARN(String message, int errorCode)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .CODE(errorCode);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     */
    public static void WARN(String message, String projectName)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .PROJECTNAME(projectName);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message   The message that will be displayed in the log.
     * @param exception The exception that will be printed.
     */
    public static void WARN(String message, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the WARNING template.
     *
     * @param message The message that will be displayed in the log.
     */
    public static void WARN(String message)
    {
        LogEntry l = new LogEntry(message, LogLevel.WARNING);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     * @param errorCode   The error code that will be displayed.
     * @param exception   The exception that will be printed.
     */
    public static void ERROR(String message, String projectName, int errorCode, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .PROJECTNAME(projectName)
                .CODE(errorCode)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message   The message that will be displayed in the log.
     * @param errorCode The error code that will be displayed.
     * @param exception The exception that will be printed.
     */
    public static void ERROR(String message, int errorCode, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .CODE(errorCode)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     * @param errorCode   The error code that will be displayed.
     */
    public static void ERROR(String message, String projectName, int errorCode)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .PROJECTNAME(projectName)
                .CODE(errorCode);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     * @param exception   The exception that will be printed.
     */
    public static void ERROR(String message, String projectName, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .PROJECTNAME(projectName)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message   The message that will be displayed in the log.
     * @param errorCode The error code that will be displayed.
     */
    public static void ERROR(String message, int errorCode)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .CODE(errorCode);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message     The message that will be displayed in the log.
     * @param projectName The name of the project this log was created in.
     */
    public static void ERROR(String message, String projectName)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .PROJECTNAME(projectName);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message   The message that will be displayed in the log.
     * @param exception The exception that will be printed.
     */
    public static void ERROR(String message, Exception exception)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR)
                .EXCEPTION(exception);
        Logger.log(l);
    }
    /**
     * This method will create a log message with the ERROR template.
     *
     * @param message The message that will be displayed in the log.
     */
    public static void ERROR(String message)
    {
        LogEntry l = new LogEntry(message, LogLevel.ERROR);
        Logger.log(l);
    }
}
