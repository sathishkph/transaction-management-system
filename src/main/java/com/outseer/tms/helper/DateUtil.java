package com.outseer.tms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    public static  LocalDateTime parseIsoDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) return null;

        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid ISO date format: " + dateTime);
        }
    }
}
