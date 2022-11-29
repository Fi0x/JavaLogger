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
    boolean onlyDebug;
    boolean plainText;
    boolean mixpanelMessage;
    String mixpanelEventName;

    LogTemplate(String color, String background, String level, boolean writeToFile, boolean requireVerbose, boolean requireDebug, boolean noPrefix, boolean mixpanelMessage, String mixpanelEventName)
    {
        this.color = color;
        this.background = background;
        this.loglevel = level;
        this.fileEntry = writeToFile;
        this.onlyVerbose = requireVerbose;
        this.onlyDebug = requireDebug;
        this.plainText = noPrefix;
        this.mixpanelMessage = mixpanelMessage;
        this.mixpanelEventName = mixpanelEventName;
    }
}
