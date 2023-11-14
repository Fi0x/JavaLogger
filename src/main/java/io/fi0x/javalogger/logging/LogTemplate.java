package io.fi0x.javalogger.logging;

/**
 * This class is used internally to create {@link LogTemplate}
 * for default logging-behaviour.
 */
class LogTemplate
{
    String color;
    String background;
    String loglevel;
    boolean fileEntry;
    boolean onlyVerbose;
    int verboseLevel;
    boolean onlyDebug;
    boolean plainText;
    boolean consoleException;
    boolean mixpanelMessage;
    String mixpanelEventName;
    String projectName;

    LogTemplate(String color, String background, String level, boolean writeToFile, boolean requireVerbose, int verboseLevel, boolean requireDebug, boolean noPrefix, boolean showExceptionInConsole, boolean mixpanelMessage, String mixpanelEventName, String projectName)
    {
        this.color = color;
        this.background = background;
        this.loglevel = level;
        this.fileEntry = writeToFile;
        this.onlyVerbose = requireVerbose;
        this.verboseLevel = verboseLevel;
        this.onlyDebug = requireDebug;
        this.plainText = noPrefix;
        this.consoleException = showExceptionInConsole;
        this.mixpanelMessage = mixpanelMessage;
        this.mixpanelEventName = mixpanelEventName;
        this.projectName = projectName;
    }
}
