package io.fi0x.javalogger.logging;

/**
 * This class provides all information that are needed to print and / or save a {@link LogEntry}.
 * It contains the message that should be logged,
 * in which color it should be printed,
 * the logging-level it has,
 * an error code if required,
 * an exception if required
 * and weather it should be saved in a file or only printed.
 * It can also be set to be only visible if the {@link Logger} is in verbose- or debug-mode.
 */
public class LogEntry
{
    final String message;
    String color = Logger.RESET;
    String loglevel = "INF";
    int errorCode = 0;
    Exception exception = null;
    boolean fileEntry = true;
    boolean onlyVerbose = false;
    boolean onlyDebug = false;
    boolean plainText = false;
    boolean mixpanel = false;
    String mixpanelEventName = "LOG";

    /**
     * Create a new {@link LogEntry} with the given text.
     * @param text The message that should be logged.
     */
    public LogEntry(String text)
    {
        message = text;
    }
    /**
     * Create a new {@link LogEntry} with the provided text,
     * based on the given {@link LogTemplate}-name.
     * @param text The message that should be logged.
     * @param templateName The name of the {@link LogTemplate} that should be used for the logging-behaviour.
     * @throws IllegalArgumentException Will throw if a {@link LogTemplate} with the provided name does not exist.
     */
    public LogEntry(String text, String templateName) throws IllegalArgumentException
    {
        if(!Logger.templates.containsKey(templateName))
            throw new IllegalArgumentException("A LogTemplate with this name does not exist");

        message = text;

        LogTemplate t = Logger.templates.get(templateName);
        this.color = t.color;
        this.loglevel = t.loglevel;
        this.fileEntry = t.fileEntry;
        this.onlyVerbose = t.onlyVerbose;
        this.onlyDebug = t.onlyDebug;
        this.plainText = t.plainText;
        this.mixpanel = t.mixpanelMessage;
        this.mixpanelEventName = t.mixpanelEventName;
    }

    /**
     * Change the color that should be used when the {@link LogEntry} is printed.
     * @param colorCode The new color code
     *                  (Default will use the current console-color).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry COLOR(String colorCode)
    {
        color = colorCode;
        return this;
    }
    /**
     * Change the logging-level that should be used for this {@link LogEntry}.
     * @param levelEnum The new logging-level
     *                  (Default is INFO).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry LEVEL(String levelEnum)
    {
        loglevel = levelEnum;
        return this;
    }
    /**
     * Set a specific error code for the {@link LogEntry} to use.
     * @param exceptionCode The code that should be printed and saved
     *                      (Default will not use an error-code).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry CODE(int exceptionCode)
    {
        errorCode = exceptionCode;
        return this;
    }
    /**
     * Add an {@link Exception} to the {@link LogEntry}.
     * The {@link Exception} will only be visible in the log-file.
     * The message of this {@link LogEntry} will still be printed in the output.
     * @param e The {@link Exception} to save in the log-file
     *          (Default will not save any {@link Exception}s).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry EXCEPTION(Exception e)
    {
        exception = e;
        return this;
    }
    /**
     * Set weather the {@link LogEntry} should be stored in the log-file or not.
     * @param shouldWriteToFile If the {@link LogEntry} should be written to the log-file
     *                          or only be printed in the output
     *                          (Default will do both).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry FILE_ENTRY(boolean shouldWriteToFile)
    {
        fileEntry = shouldWriteToFile;
        return this;
    }
    /**
     * Set the debug-state of this {@link LogEntry}.
     * @param onlyInDebugMode If the {@link LogEntry} should only be active when the Logger is in debug-mode
     *                        (Default is false).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry DEBUG(boolean onlyInDebugMode)
    {
        onlyDebug = onlyInDebugMode;
        return this;
    }
    /**
     * Set the verbose-state of this {@link LogEntry}.
     * @param onlyInVerboseMode If the {@link LogEntry} should only be active when the Logger is in verbose-mode
     *                          (Default is false).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry VERBOSE(boolean onlyInVerboseMode)
    {
        onlyVerbose = onlyInVerboseMode;
        return this;
    }
    /**
     * Stop this {@link LogEntry} from using the prefix information.
     * The removed prefix information is the time, logging-level and error-code.
     * @param onlyPlaintext If the {@link LogEntry} should ignore the prefix
     *                      (Default is false).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry PLAINTEXT(boolean onlyPlaintext)
    {
        plainText = onlyPlaintext;
        return this;
    }
    /**
     * Require this {@link LogEntry} to send its information to Mixpanel.
     * This requires you to set up the {@link io.fi0x.javalogger.mixpanel.MixpanelHandler} correctly.
     * @param sendToMixpanel If the {@link LogEntry} should send its information to Mixpanel
     *                      (Default is false).
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry MIXPANEL(boolean sendToMixpanel)
    {
        mixpanel = sendToMixpanel;
        return this;
    }
    /**
     * Change the Mixpanel event name for this {@link LogEntry}.
     * Using Mixpanel Logs requires you to set up the {@link io.fi0x.javalogger.mixpanel.MixpanelHandler} correctly.
     * @param mixpanelEvent The event name under which this {@link LogEntry} should be displayed in Mixpanel
     *                      (Default is 'LOG').
     * @return The current {@link LogEntry} to be used further.
     */
    public LogEntry MIXPANELNAME(String mixpanelEvent)
    {
        mixpanelEventName = mixpanelEvent;
        return this;
    }
}
