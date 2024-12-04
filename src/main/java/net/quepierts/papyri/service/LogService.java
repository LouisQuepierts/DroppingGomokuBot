package net.quepierts.papyri.service;

import com.google.common.base.Strings;
import net.quepierts.papyri.ANSIColors;

public interface LogService {
    void info(String message);

    void info(String message, Object... args);

    void error(String message);
    
    void error(String message, Throwable throwable);
    
    void error(String message, Object... args);
    
    void warning(String message);
    
    void warning(String message, Throwable throwable);
    
    void warning(String message, Object... args);

    default void title(String value) {
        this.info(center(ANSIColors.bold(value), 72, '-'));
    }

    default void tab(String value) {
        this.info("------== " + value + " ==-------");
    }

    default void entry(String value) {
        this.info("  - " + value);
    }

    default void line(int length) {
        this.info(Strings.repeat("-", length));
    }

    default String center(String str, int totalLength, char fillChar) {
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
}
