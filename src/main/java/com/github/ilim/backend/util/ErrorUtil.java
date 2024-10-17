package com.github.ilim.backend.util;

public class ErrorUtil {
    
    public static String cleanMessage(String message) {
        var index = message.contains("(")
            ? message.indexOf("(")
            : message.length();
        return message.substring(0, index);
    }
}
