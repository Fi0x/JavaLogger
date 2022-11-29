import io.fi0x.javalogger.logging.LogEntry;
import io.fi0x.javalogger.logging.Logger;

public class TestLogger
{
    public static void main(String[] args)
    {
        LogEntry l = new LogEntry("Testing logger")
                .COLOR(Logger.RED)
                .BACKGROUND(Logger.GREEN_BACKGROUND)
                .LEVEL("ERR")
                .CODE(0)
                .FILE_ENTRY(false);
        Logger.log(l);
    }
}
