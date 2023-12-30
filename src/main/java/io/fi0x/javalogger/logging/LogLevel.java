package io.fi0x.javalogger.logging;

/**
 * This enum contains the names of pre-defined log-templates.
 */
public enum LogLevel
{
    /**
     * A template that will only appear in verbose mode
     */
    VERBOSE,
    /**
     * A template that will only appear in verbose mode with verbose level 1
     */
    VVERBOSE,
    /**
     * A template that will only appear in verbose mode with verbose level 2
     */
    VVVERBOSE,
    /**
     * A template that will log only to the console and not to a file.
     */
    INFO,
    /**
     * A template that will log in a different color, but only in debug-mode.
     * It will write to the .log-file.
     */
    WARNING,
    /**
     * A template that will log in a different color and also write to the .log-file.
     */
    ERROR,
    /**
     * A template for special messages, usually with prefix and in a special color.
     */
    SPECIAL,
    /**
     * A template for special messages, usually with prefix and in a special color.
     */
    RESPONSE,
    /**
     * A template for special messages, usually with prefix and in a special color.
     */
    QUESTION,
    /**
     * A template that will log only to the console and not to a file. Usually without prefix.
     */
    CLEAN_INFO,
    /**
     * A template for special messages, usually without prefix and in a special color.
     */
    CLEAN_SPECIAL,
    /**
     * A template for special messages, usually without prefix and in a special color.
     */
    CLEAN_RESPONSE,
    /**
     * A template for special messages, usually without prefix and in a special color.
     */
    CLEAN_QUESTION
}
