package net.quepierts.papyri;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import net.quepierts.papyri.service.LogService;

public class LogUtil {
    @Inject
    public static LogService logService;

    public static void title(String value) {
        info(center(ANSIColors.bold(value), 72, '-'));
    }

    public static void tab(String value) {
        info("------== " + value + " ==-------");
    }

    public static void entry(String value) {
        info("  - " + value);
    }

    public static void line(int length) {
        info(Strings.repeat("-", length));
    }

    public static void error(String message) {
        String[] strings = message.split("\n");
        for (String string : strings) {
            System.out.println("[" + ANSIColors.RED + "ERROR" + ANSIColors.RESET + "] " + string);
        }
    }
    
    public static void info(String message) {
        String[] strings = message.split("\n");
        for (String string : strings) {
            System.out.println("[" + ANSIColors.BLUE + "INFO" + ANSIColors.RESET + "] " + string);
        }
    }
    
    public static void result(String message) {
        String[] strings = message.split("\n");
        for (String string : strings) {
            System.out.println("[" + ANSIColors.GREEN + "RESULT" + ANSIColors.RESET + "] " + string);
        }
    }

    public static String center(String str, int totalLength, char fillChar) {
        if (str.length() >= totalLength) {
            return str;
        }

        int padding = totalLength - str.length();
        int paddingLeft = padding / 2;
        int paddingRight = padding - paddingLeft;

        return String.valueOf(fillChar).repeat(paddingLeft) +
                str +
                String.valueOf(fillChar).repeat(Math.max(0, paddingRight));
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
