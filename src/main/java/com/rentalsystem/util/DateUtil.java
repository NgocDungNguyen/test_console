package com.rentalsystem.util;

import org.jline.reader.LineReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling date-related operations.
 */
public class DateUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static {
        dateFormat.setLenient(false);  // Strict date parsing
    }

    /**
     * Parses a date string into a Date object.
     * @param dateString The date string to parse
     * @return The parsed Date object, or null if parsing fails
     */
    public static Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }

    /**
     * Reads a date input from the console using a LineReader.
     * Continues to prompt the user until a valid date is entered.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The parsed Date object
     */
    public static Date readDate(LineReader reader, String prompt) {
        Date date = null;
        while (date == null) {
            String input = reader.readLine(prompt);
            try {
                date = dateFormat.parse(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
        return date;
    }

    /**
     * Reads an optional date input from the console using a LineReader.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The parsed Date object, or null if no input is provided
     */
    public static Date readOptionalDate(LineReader reader, String prompt) {
        String input = reader.readLine(prompt);
        if (input.isEmpty()) {
            return null;
        }
        try {
            return dateFormat.parse(input);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }
}