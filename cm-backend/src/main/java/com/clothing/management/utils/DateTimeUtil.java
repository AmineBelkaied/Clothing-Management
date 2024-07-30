package com.clothing.management.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    public static String formatToMySQLDateTime(String isoDateString) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDateString);
            LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + isoDateString, e);
        }
    }
}

