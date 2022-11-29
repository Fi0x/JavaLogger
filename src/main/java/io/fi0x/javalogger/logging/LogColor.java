package io.fi0x.javalogger.logging;

public class LogColor
{
    public static final String RESET = "\033[0m";

    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    public static final String BLACK_BRIGHT = "\033[0;90m";
    public static final String RED_BRIGHT = "\033[0;91m";
    public static final String GREEN_BRIGHT = "\033[0;92m";
    public static final String YELLOW_BRIGHT = "\033[0;93m";
    public static final String BLUE_BRIGHT = "\033[0;94m";
    public static final String PURPLE_BRIGHT = "\033[0;95m";
    public static final String CYAN_BRIGHT = "\033[0;96m";
    public static final String WHITE_BRIGHT = "\033[0;97m";

    public static final String BLACK_BACKGROUND = "\033[40m";
    public static final String RED_BACKGROUND = "\033[41m";
    public static final String GREEN_BACKGROUND = "\033[42m";
    public static final String YELLOW_BACKGROUND = "\033[43m";
    public static final String BLUE_BACKGROUND = "\033[44m";
    public static final String PURPLE_BACKGROUND = "\033[45m";
    public static final String CYAN_BACKGROUND = "\033[46m";
    public static final String WHITE_BACKGROUND = "\033[47m";

    public static final String BLACK_BACKGROUND_BRIGHT = "\033[100m";
    public static final String RED_BACKGROUND_BRIGHT = "\033[101m";
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[102m";
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[103m";
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[104m";
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[105m";
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[106m";
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[107m";

    /**
     * Creates a String that corresponds to a usable color-code.
     * @param color The color that should be used.
     * @param design Special font-features if the color is a foreground
     *               (Default is 'Normal').
     * @param bright Weather or not the color should be bright or dim
     *               (Default is false).
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
     * @see #get(Color, Design, boolean, boolean)
     */
    public static String get(Color color, Design design, boolean bright)
    {
        return get(color, design, bright, true);
    }
    /**
     * @see #get(Color, Design, boolean, boolean)
     */
    public static String get(Color color, Design design)
    {
        return get(color, design, false, true);
    }
    /**
     * @see #get(Color, Design, boolean, boolean)
     */
    public static String get(Color color)
    {
        return get(color, Design.NORMAL, false, true);
    }

    public enum Color
    {
        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        PURPLE(35),
        CYAN(36),
        WHITE(37);

        private final int id;
        Color(int i)
        {
            id = i;
        }
    }
    public enum Design
    {
        NORMAL(0),
        BOLD(1),
        ITALIC(3),
        UNDERLINED(4),
        INVERTED(7),
        STRIKETHROUGH(9);

        private int style;
        Design(int i)
        {
            style = i;
        }
    }
}
