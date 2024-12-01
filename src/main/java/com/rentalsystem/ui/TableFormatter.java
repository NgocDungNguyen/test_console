package com.rentalsystem.ui;


import com.rentalsystem.model.*;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A class to format and print tables in the console.
 */
public class TableFormatter {
    private final Terminal terminal;


    // ANSI color codes for console output
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";


    /**
     * Constructs a TableFormatter with the given terminal.
     * @param terminal The terminal to use for output
     */
    public TableFormatter(Terminal terminal) {
        this.terminal = terminal;
    }


    /**
     * Prints a table with a title and options.
     * @param title The title of the table
     * @param options The list of options to display
     * @param color The color to use for the table
     */
    public void printTable(String title, List<String> options, String color) {
        int maxWidth = Math.max(
                title.length(),
                options.stream().mapToInt(String::length).max().orElse(0)
        ) + 6; // Increased padding


        printBorder('╔', '╗', '═', maxWidth);
        printCenteredText(title, maxWidth);
        printBorder('╠', '╣', '═', maxWidth);


        for (int i = 0; i < options.size(); i++) {
            printText((i + 1) + ". " + options.get(i), maxWidth);
        }


        printBorder('╚', '╝', '═', maxWidth);
    }


    /**
     * Prints a single line of text in the table.
     * @param text The text to print
     * @param width The width of the table
     */
    private void printText(String text, int width) {
        if (text.length() > width - 3) {
            // If the text is too long, truncate it and add ellipsis
            text = text.substring(0, width - 6) + "...";
        }
        int padding = Math.max(0, width - text.length() - 3);
        System.out.println("║ " + text + " ".repeat(padding) + "║");
    }


    /**
     * Prints a data table with headers and rows.
     * @param headers The headers of the table
     * @param rows The data rows of the table
     * @param color The color to use for the table
     */
    public void printDataTable(List<String> headers, List<List<String>> rows, String color) {
        List<Integer> columnWidths = getColumnWidths(headers, rows);


        printDataBorder('┌', '┐', '─', '┬', columnWidths);
        printDataRow(headers, columnWidths, true);
        printDataBorder('├', '┤', '─', '┼', columnWidths);


        for (List<String> row : rows) {
            printDataRow(row, columnWidths, false);
        }
        printDataBorder('└', '┘', '─', '┴', columnWidths);
    }


    /**
     * Prints a border for the table.
     * @param left The left corner character
     * @param right The right corner character
     * @param fill The fill character
     * @param width The width of the table
     */
    private void printBorder(char left, char right, char fill, int width) {
        System.out.println(left + String.valueOf(fill).repeat(width - 2) + right);
    }


    /**
     * Prints centered text in the table.
     * @param text The text to center
     * @param width The width of the table
     */
    private void printCenteredText(String text, int width) {
        int padding = (width - text.length()) / 2;
        System.out.println("║" + " ".repeat(padding) + text + " ".repeat(width - text.length() - padding - 2) + "║");
    }


    /**
     * Prints a border for the data table.
     * @param left The left corner character
     * @param right The right corner character
     * @param fill The fill character
     * @param separator The separator character
     * @param columnWidths The widths of each column
     */
    private void printDataBorder(char left, char right, char fill, char separator, List<Integer> columnWidths) {
        System.out.print(left);
        for (int i = 0; i < columnWidths.size(); i++) {
            System.out.print(String.valueOf(fill).repeat(columnWidths.get(i) + 2));
            if (i < columnWidths.size() - 1) {
                System.out.print(separator);
            }
        }
        System.out.println(right);
    }


    /**
     * Prints a row of the data table.
     * @param cells The cells of the row
     * @param columnWidths The widths of each column
     * @param isHeader Whether this row is a header row
     */
    private void printDataRow(List<String> cells, List<Integer> columnWidths, boolean isHeader) {
        System.out.print("│");
        for (int i = 0; i < cells.size(); i++) {
            String cell = cells.get(i);
            System.out.print(" " + String.format("%-" + columnWidths.get(i) + "s", cell) + " ");
            if (i < cells.size() - 1) {
                System.out.print("│");
            }
        }
        System.out.println("│");
    }


    /**
     * Calculates the widths of each column in the table.
     * @param headers The headers of the table
     * @param rows The data rows of the table
     * @return A list of column widths
     */
    private List<Integer> getColumnWidths(List<String> headers, List<List<String>> rows) {
        List<Integer> widths = new ArrayList<>(headers.size());
        for (int i = 0; i < headers.size(); i++) {
            int maxWidth = headers.get(i).length();
            for (List<String> row : rows) {
                maxWidth = Math.max(maxWidth, row.get(i).length());
            }
            widths.add(maxWidth);
        }
        return widths;
    }


    /**
     * Prints a status bar with current user, date, and time.
     * @param currentUser The current user
     * @param date The current date
     * @param time The current time
     */
    public void printStatusBar(String currentUser, String date, String time) {
        List<String> headers = Arrays.asList("Current User", "Date", "Time");
        List<List<String>> data = Arrays.asList(Arrays.asList(currentUser, date, time));
        printDataTable(headers, data, ANSI_YELLOW);
    }


    /**
     * Prints a table of owners.
     * @param owners The list of owners to display
     */
    public void printOwnerTable(List<Owner> owners) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> rows = new ArrayList<>();


        for (Owner owner : owners) {
            rows.add(Arrays.asList(
                    owner.getId(),
                    owner.getFullName(),
                    owner.getDateOfBirth(),
                    owner.getContactInformation()
            ));
        }


        printDataTable(headers, rows, ANSI_CYAN);
    }


    /**
     * Prints a table of hosts.
     * @param hosts The list of hosts to display
     */
    public void printHostTable(List<Host> hosts) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> rows = new ArrayList<>();


        for (Host host : hosts) {
            rows.add(Arrays.asList(
                    host.getId(),
                    host.getFullName(),
                    host.getDateOfBirth(),
                    host.getContactInformation()
            ));
        }


        printDataTable(headers, rows, ANSI_CYAN);
    }


    /**
     * Prints a table of properties.
     * @param properties The list of properties to display
     */
    public void printPropertyTable(List<Property> properties) {
        List<String> headers = Arrays.asList(
                "Property ID", "Type", "Address", "Price", "Status", "Owner", "Tenants"
        );
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            String tenantInfo = property.getTenants().stream()
                    .map(t -> t.getId() + " - " + t.getFullName())
                    .collect(Collectors.joining(", "));


            data.add(Arrays.asList(
                    property.getPropertyId(),
                    property instanceof ResidentialProperty ? "Residential" : "Commercial",
                    property.getAddress(),
                    String.format("%.2f", property.getPrice()),
                    property.getStatus().toString(),
                    property.getOwner().getId() + " - " + property.getOwner().getFullName(),
                    tenantInfo
            ));
        }
        printDataTable(headers, data, ANSI_CYAN);
    }


    /**
     * Prints a table of rental agreements.
     * @param agreements The list of rental agreements to display
     */
    public void printRentalAgreementTable(List<RentalAgreement> agreements) {
        List<String> headers = Arrays.asList("ID", "Property", "Tenant", "Start Date", "End Date", "Rent Amount", "Status");
        List<List<String>> rows = new ArrayList<>();


        for (RentalAgreement agreement : agreements) {
            rows.add(Arrays.asList(
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    agreement.getMainTenant().getFullName(),
                    agreement.getStartDate().toString(),
                    agreement.getEndDate().toString(),
                    String.format("%.2f", agreement.getRentAmount()),
                    agreement.getStatus().toString()
            ));
        }


        printDataTable(headers, rows, ANSI_CYAN);
    }
}
