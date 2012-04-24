package org.ethnochat.util;

public class DebuggingTools {

    public static final int DEBUGGING_OFF = 0;
    private static int debugLevel = DEBUGGING_OFF;

    public static void initializeDebugLogging(int level) {
        debugLevel = level;
    }

    public static void print(String str) {
        if (debugLevel > DEBUGGING_OFF) {
            System.err.print(str);
            System.err.flush();
        }
    }

    public static void print(String str, int minLevel) {
        if (debugLevel >= minLevel) {
            print(str);
        }
    }

    public static void println(String str) {
        if (debugLevel > DEBUGGING_OFF) {
            System.err.println(str);
            System.err.flush();
        }
    }

    public static void println(String str, int minLevel) {
        if (debugLevel >= minLevel) {
            println(str);
        }
    }
}
