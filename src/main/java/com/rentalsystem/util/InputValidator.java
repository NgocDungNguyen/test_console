package com.rentalsystem.util;

import java.util.List;
import java.util.regex.Pattern;

import com.rentalsystem.manager.HostManager;
import com.rentalsystem.manager.OwnerManager;
import com.rentalsystem.manager.PropertyManager;
import com.rentalsystem.manager.RentalManager;
import com.rentalsystem.manager.TenantManager;
import com.rentalsystem.model.Owner;
import com.rentalsystem.model.Person;
import com.rentalsystem.model.Tenant;
import org.jline.reader.LineReader;

public class InputValidator {
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

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

//    public static boolean isValidEmail(String email) {
//        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
//        return email != null && email.matches(emailRegex);
//    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static String readString(LineReader reader, String prompt) {
        return reader.readLine(prompt).trim();
    }

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

    public static boolean isEmailTaken(List<? extends Person> allEmail, String email) {
        return allEmail.stream()
                .anyMatch(owner -> owner.getContactInformation().equalsIgnoreCase(email));
    }
}
