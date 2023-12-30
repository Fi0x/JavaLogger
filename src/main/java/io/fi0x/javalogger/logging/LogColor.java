package io.fi0x.javalogger.logging;

/**
 * This class defines colors and text-designs that can be used in the log-entries for the console.
 */
public class LogColor
{
    private LogColor()
    {
    }

    /**
     * This resets the current console-color to whatever its default values are.
     */
    public static final String RESET = "\033[0m";

    /**
     * Black.
     */
    public static final String BLACK = "\033[0;30m";

    /**
     * Red.
     */
    public static final String RED = "\033[0;31m";

    /**
     * Green.
     */
    public static final String GREEN = "\033[0;32m";

    /**
     * Yellow.
     */
    public static final String YELLOW = "\033[0;33m";

    /**
     * Blue.
     */
    public static final String BLUE = "\033[0;34m";

    /**
     * Purple.
     */
    public static final String PURPLE = "\033[0;35m";

    /**
     * Cyan.
     */
    public static final String CYAN = "\033[0;36m";

    /**
     * White.
     */
    public static final String WHITE = "\033[0;37m";


    /**
     * Bright Black.
     */
    public static final String BLACK_BRIGHT = "\033[0;90m";

    /**
     * Bright Red.
     */
    public static final String RED_BRIGHT = "\033[0;91m";

    /**
     * Bright Green.
     */
    public static final String GREEN_BRIGHT = "\033[0;92m";

    /**
     * Bright Yellow.
     */
    public static final String YELLOW_BRIGHT = "\033[0;93m";

    /**
     * Bright Blue.
     */
    public static final String BLUE_BRIGHT = "\033[0;94m";

    /**
     * Bright Purple.
     */
    public static final String PURPLE_BRIGHT = "\033[0;95m";

    /**
     * Bright Cyan.
     */
    public static final String CYAN_BRIGHT = "\033[0;96m";

    /**
     * Bright White.
     */
    public static final String WHITE_BRIGHT = "\033[0;97m";


    /**
     * Black Background.
     */
    public static final String BLACK_BACKGROUND = "\033[40m";

    /**
     * Red Background.
     */
    public static final String RED_BACKGROUND = "\033[41m";

    /**
     * Green Background.
     */
    public static final String GREEN_BACKGROUND = "\033[42m";

    /**
     * Yellow Background.
     */
    public static final String YELLOW_BACKGROUND = "\033[43m";

    /**
     * Blue Background.
     */
    public static final String BLUE_BACKGROUND = "\033[44m";

    /**
     * Purple Background.
     */
    public static final String PURPLE_BACKGROUND = "\033[45m";

    /**
     * Cyan Background.
     */
    public static final String CYAN_BACKGROUND = "\033[46m";

    /**
     * White Background.
     */
    public static final String WHITE_BACKGROUND = "\033[47m";


    /**
     * Bright Black Background.
     */
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[100m";

    /**
     * Bright Red Background.
     */
    public static final String RED_BACKGROUND_BRIGHT = "\033[101m";

    /**
     * Bright Green Background.
     */
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[102m";

    /**
     * Bright Yellow Background.
     */
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[103m";

    /**
     * Bright Blue Background.
     */
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[104m";

    /**
     * Bright Purple Background.
     */
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[105m";

    /**
     * Bright Cyan Background.
     */
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[106m";

    /**
     * Bright White Background.
     */
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[107m";

    /**
     * Creates a String that corresponds to a usable color-code.
     *
     * @param color      The color that should be used.
     * @param design     Special font-features if the color is a foreground
     *                   (Default is 'Normal').
     * @param bright     Weather or not the color should be bright or dim
     *                   (Default is false).
     * @param foreground Weather the color should be a foreground or background color
     *                   (Default is true).
     * @return A String that can be used to change the console color style.
     */
    public static String get(Color color, Design design, boolean bright, boolean foreground)
    {
        String result = "\33[";
        if(foreground)
            result += design.style + ";";

        int colorNumber = color.id;
        if(bright)
            colorNumber += 60;

        result += colorNumber + "m";
        return result;
    }
    /**
     * Creates a String that corresponds to a usable color-code.
     *
     * @param color  The color that should be used.
     * @param design Special font-features if the color is a foreground
     *               (Default is 'Normal').
     * @param bright Weather or not the color should be bright or dim
     *               (Default is false).
     * @return A String that can be used to change the console color style.
     */
    public static String get(Color color, Design design, boolean bright)
    {
        return get(color, design, bright, true);
    }
    /**
     * Creates a String that corresponds to a usable color-code.
     *
     * @param color  The color that should be used.
     * @param design Special font-features if the color is a foreground
     *               (Default is 'Normal').
     * @return A String that can be used to change the console color style.
     */
    public static String get(Color color, Design design)
    {
        return get(color, design, false);
    }
    /**
     * Creates a String that corresponds to a usable color-code.
     *
     * @param color The color that should be used.
     * @return A String that can be used to change the console color style.
     */
    public static String get(Color color)
    {
        return get(color, Design.NORMAL);
    }

    /**
     * This enum defines values for default colors.
     */
    public enum Color
    {
        /**
         * Black.
         */
        BLACK(30),
        /**
         * Red.
         */
        RED(31),
        /**
         * Green.
         */
        GREEN(32),
        /**
         * Yellow.
         */
        YELLOW(33),
        /**
         * Blue.
         */
        BLUE(34),
        /**
         * Purple.
         */
        PURPLE(35),
        /**
         * Cyan.
         */
        CYAN(36),
        /**
         * White.
         */
        WHITE(37);

        private final int id;
        Color(int i)
        {
            id = i;
        }
    }

    /**
     * This enum defines values for special designs in the log entries.
     */
    public enum Design
    {
        /**
         * The text will stay normal.
         */
        NORMAL(0),
        /**
         * The text will be displayed in bold letters.
         */
        BOLD(1),
        /**
         * The text will be displayed in italic letters.
         */
        ITALIC(3),
        /**
         * The text will be underlined.
         */
        UNDERLINED(4),
        /**
         * The text will be inverted. (Might be hard to read, but useful for special user-communication).
         */
        INVERTED(7),
        /**
         * The text will be crossed out.
         */
        STRIKETHROUGH(9);

        private int style;
        Design(int i)
        {
            style = i;
        }
    }
}
