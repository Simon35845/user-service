package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    private DateTimeUtil() {
    }

    public static String dateTimeToString(LocalDateTime localDateTime) {
        return (localDateTime != null)
                ? localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
                : null;
    }
}
