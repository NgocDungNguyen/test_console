/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */
package com.rentalsystem.util;

import java.util.Scanner;


/**
 * Utility class for handling console input and output operations.
 */
public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);


    /**
     * Reads a string input from the console.
     * @param prompt The prompt to display to the user
     * @return The user's input as a trimmed string
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }


    /**
     * Reads an integer input from the console.
     * Continues to prompt the user until a valid integer is entered.
     * @param prompt The prompt to display to the user
     * @return The user's input as an integer
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }


    /**
     * Reads a double input from the console.
     * Continues to prompt the user until a valid double is entered.
     * @param prompt The prompt to display to the user
     * @return The user's input as a double
     */
    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }


    /**
     * Reads a boolean input from the console.
     * Accepts "yes", "y", "no", "n" as valid inputs (case-insensitive).
     * @param prompt The prompt to display to the user
     * @return true if the user inputs "yes" or "y", false if "no" or "n"
     */
    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }


    /**
     * Clears the console screen.
     * Note: This may not work in all console environments.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    /**
     * Prompts the user to press Enter to continue.
     */
    public static void pressEnterToContinue() {
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
}
