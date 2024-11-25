package com.rentalsystem.util;

import java.util.List;
import java.util.regex.Pattern;

import com.rentalsystem.model.Person;
import org.jline.reader.LineReader;

/**
 * Utility class for validating user input.
 */
public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    /**
     * Reads a non-empty string from the console.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The non-empty string entered by the user
     */
    public static String readNonEmptyString(LineReader reader, String prompt) {
        String input;
        do {
            input = reader.readLine(prompt).trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Validates an email address.
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Reads a string from the console.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The string entered by the user
     */
    public static String readString(LineReader reader, String prompt) {
        return reader.readLine(prompt).trim();
    }

    /**
     * Reads an integer from the console within a specified range.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The integer entered by the user
     */
    public static int readInteger(LineReader reader, String prompt, int min, int max) {
        while (true) {
            try {
                String input = reader.readLine(prompt);
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a double from the console within a specified range.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The double entered by the user
     */
    public static double readDouble(LineReader reader, String prompt, double min, double max) {
        while (true) {
            try {
                String input = reader.readLine(prompt);
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a boolean from the console.
     * @param reader The LineReader to use for input
     * @param prompt The prompt to display to the user
     * @return The boolean entered by the user
     */
    public static boolean readBoolean(LineReader reader, String prompt) {
        while (true) {
            String input = reader.readLine(prompt).trim().toLowerCase();
            if (input.equals("true") || input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("false") || input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    /**
     * Checks if an email is already taken by any person in a list.
     * @param allEmail The list of persons to check against
     * @param email The email to check
     * @return true if the email is taken, false otherwise
     */
    public static boolean isEmailTaken(List<? extends Person> allEmail, String email) {
        return allEmail.stream()
                .anyMatch(person -> person.getContactInformation().equalsIgnoreCase(email));
    }
}