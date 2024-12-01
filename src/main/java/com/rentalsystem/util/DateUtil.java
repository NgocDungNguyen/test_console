/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */
package com.rentalsystem.util;

import org.jline.reader.LineReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling date-related operations.
 */
public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        DATE_FORMAT.setLenient(false);  // Strict date parsing
    }

    /**
     * Parses a date string into a Date object.
     * @param dateString The date string to parse
     * @return The parsed Date object
     * @throws ParseException if the date string is invalid
     */
    public static Date parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }

    /**
     * Formats a Date object into a string.
     * @param date The Date object to format
     * @return The formatted date string, or an empty string if the date is null
     */
    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }

    /**
     * Reads a date input from the console using a LineReader.
     * Continues to prompt the user until a valid date is entered.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The parsed Date object
     */
    public static Date readDate(LineReader reader, String prompt) {
        while (true) {
            String input = reader.readLine(prompt);
            try {
                return parseDate(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    /**
     * Reads an optional date input from the console using a LineReader.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The parsed Date object, or null if no input is provided or if the input is invalid
     */
    public static Date readOptionalDate(LineReader reader, String prompt) {
        String input = reader.readLine(prompt);
        if (input.isEmpty()) {
            return null;
        }
        try {
            return parseDate(input);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }

    /**
     * Validates if a given string is a valid date.
     * @param dateString The date string to validate
     * @return true if the date is valid, false otherwise
     */
    public static boolean isValidDate(String dateString) {
        try {
            parseDate(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}