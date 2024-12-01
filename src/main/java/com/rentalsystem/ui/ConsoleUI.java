/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import com.rentalsystem.model.Payment;
import com.rentalsystem.model.PropertyStatus;
import com.rentalsystem.util.FileHandler;

import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.rentalsystem.manager.HostManager;
import com.rentalsystem.manager.HostManagerImpl;
import com.rentalsystem.manager.OwnerManager;
import com.rentalsystem.manager.OwnerManagerImpl;
import com.rentalsystem.manager.PropertyManager;
import com.rentalsystem.manager.PropertyManagerImpl;
import com.rentalsystem.manager.RentalManager;
import com.rentalsystem.manager.RentalManagerImpl;
import com.rentalsystem.manager.TenantManager;
import com.rentalsystem.manager.TenantManagerImpl;
import com.rentalsystem.model.CommercialProperty;
import com.rentalsystem.model.Host;
import com.rentalsystem.model.Owner;
import com.rentalsystem.model.Property;
import com.rentalsystem.model.RentalAgreement;
import com.rentalsystem.model.ResidentialProperty;
import com.rentalsystem.model.Tenant;
import com.rentalsystem.util.DateUtil;
import com.rentalsystem.util.InputValidator;

import static com.rentalsystem.util.InputValidator.isEmailTaken;


/**
 * ConsoleUI class handles the user interface for the Rental Management System.
 * It provides methods for displaying menus, handling user input, and interacting with the system's managers.
 */
public class ConsoleUI {
    // Manager instances for handling different entities
    private RentalManager rentalManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private HostManager hostManager;
    private PropertyManager propertyManager;


    // UI components
    private FileHandler fileHandler;
    private final LineReader reader;
    private final Terminal terminal;
    private final TableFormatter tableFormatter;


    // Date formatter for consistent date formatting
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    // ANSI color codes for console output
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";


    // ASCII art for the application logo
    private static final String[] RENTAL_ASCII = {
            "██████╗ ███████╗███╗   ██╗████████╗ █████╗ ██╗      ",
            "██╔══██╗██╔════╝████╗  ██║╚══██╔══╝██╔══██╗██║      ",
            "██████╔╝█████╗  ██╔██╗ ██║   ██║   ███████║██║      ",
            "██╔══██╗██╔══╝  ██║╚██╗██║   ██║   ██╔══██║██║      ",
            "██║  ██║███████╗██║ ╚████║   ██║   ██║  ██║███████╗ ",
            "╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝   ╚═╝  ╚═╝╚══════╝ "
    };


    private static final String[] MANAGER_ASCII = {
            "███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗██████╗  ",
            "████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝██╔══██╗ ",
            "██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██████╔╝ ",
            "██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██╔══██╗ ",
            "██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║  ██║ ",
            "╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝ "
    };


    private static final String[] SYSTEM_ASCII = {
            "███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗ ",
            "██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║ ",
            "███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║ ",
            "╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║ ",
            "███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║ ",
            "╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝ "
    };


    /**
     * Displays the exit message when the user chooses to quit the application.
     */
    private void displayExitMessage() {
        System.out.println(ANSI_GREEN +
                "╔════════════════════════════════════════════════════════════════════════════╗\n" +
                "║                                                                            ║\n" +
                "║ ████████╗██╗  ██╗ █████╗ ███╗   ██╗██╗  ██╗    ██╗   ██╗ ██████╗ ██╗   ██╗ ║\n" +
                "║ ╚══██╔══╝██║  ██║██╔══██╗████╗  ██║██║ ██╔╝    ╚██╗ ██╔╝██╔═══██╗██║   ██║ ║\n" +
                "║    ██║   ███████║███████║██╔██╗ ██║█████╔╝      ╚████╔╝ ██║   ██║██║   ██║ ║\n" +
                "║    ██║   ██╔══██║██╔══██║██║╚██╗██║██╔═██╗       ╚██╔╝  ██║   ██║██║   ██║ ║\n" +
                "║    ██║   ██║  ██║██║  ██║██║ ╚████║██║  ██╗       ██║   ╚██████╔╝╚██████╔╝ ║\n" +
                "║    ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝       ╚═╝    ╚═════╝  ╚═════╝  ║\n" +
                "║                                                                            ║\n" +
                "║                  for using the Rental Management System                    ║\n" +
                "║                                                                            ║\n" +
                "║                      We hope to see you again soon!                        ║\n" +
                "║                                                                            ║\n" +
                "╚════════════════════════════════════════════════════════════════════════════╝" +
                ANSI_RESET);
    }


    private static class LoaderSpinner {
        private static final String[] SPINNER_FRAMES = {"|", "/", "-", "\\"};
        private int currentFrame = 0;


        public void spin() {
            System.out.print("\r" + SPINNER_FRAMES[currentFrame]);
            currentFrame = (currentFrame + 1) % SPINNER_FRAMES.length;
        }


        public void stop() {
            System.out.print("\r");
        }
    }


    /**
     * Constructor for the ConsoleUI class.
     * Initializes the terminal, line reader, and table formatter.
     * @throws IOException if there's an error initializing the terminal
     */
    public ConsoleUI() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();
        List<Completer> completers = new ArrayList<>();
        completers.add(new StringsCompleter("1", "2", "3", "4", "5", "6", "7"));
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new AggregateCompleter(completers))
                .build();


        tableFormatter = new TableFormatter(terminal);
        fileHandler = new FileHandler();  // Initialize fileHandler here
    }

    /**
     * Initializes all managers and loads data from files.
     * Displays a progress bar during initialization.
     */
    private void initializeManagers() {
        ProgressDisplay progressDisplay = new ProgressDisplay(terminal);
        System.out.println("Initializing system...");


        String[] steps = {"Initializing managers", "Syncing managers", "Loading hosts", "Loading tenants", "Loading owners", "Loading properties", "Loading rental agreements", "Loading payments"};
        int totalSteps = steps.length;


        for (int i = 0; i < totalSteps; i++) {
            progressDisplay.showProgress(steps[i], i + 1, totalSteps);


            switch (i) {
                case 0:
                    this.fileHandler = new FileHandler();
                    this.hostManager = new HostManagerImpl(fileHandler);
                    this.tenantManager = new TenantManagerImpl(fileHandler);
                    this.ownerManager = new OwnerManagerImpl(fileHandler);
                    this.propertyManager = new PropertyManagerImpl(fileHandler);
                    this.rentalManager = new RentalManagerImpl(fileHandler);
                    break;
                case 1:
                    // Set dependencies
                    ((PropertyManagerImpl)this.propertyManager).setDependencies(hostManager, tenantManager, ownerManager, rentalManager);
                    ((RentalManagerImpl)this.rentalManager).setDependencies(tenantManager, propertyManager, hostManager, ownerManager);
                    ((HostManagerImpl)this.hostManager).setDependencies(propertyManager, ownerManager);
                    ((OwnerManagerImpl)this.ownerManager).setDependencies(propertyManager, hostManager);
                    this.fileHandler.syncManager(rentalManager, tenantManager, ownerManager, hostManager, propertyManager);
                    break;
                case 2:
                    this.hostManager.load();
                    break;
                case 3:
                    this.tenantManager.load();
                    break;
                case 4:
                    this.ownerManager.load();
                    break;
                case 5:
                    this.propertyManager.load();
                    break;
                case 6:
                    this.rentalManager.load();
                    break;
                case 7:
                    this.tenantManager.loadPayments();
                    break;
            }


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        System.out.println("\nSystem initialization complete!");
    }


    /**
     * Starts the main application loop.
     * Displays the welcome message, initializes managers, and handles the main menu.
     */
    public void start() {
        clearScreen();
        printWelcomeMessage();
        initializeManagers();


        while (true) {
            String command = showMainMenu();
            switch (command) {
                case "1":
                    handleRentalAgreements();
                    break;
                case "2":
                    handleTenants();
                    break;
                case "3":
                    handleOwners();
                    break;
                case "4":
                    handleHosts();
                    break;
                case "5":
                    handleProperties();
                    break;
                case "6":
                    handleReports();
                    break;
                case "7":
                    handleSave();
                    displayExitMessage();
                    return;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }


    /**
     * Handles saving data to files.
     * Displays a spinner while saving is in progress.
     */
    private void handleSave() {
        LoaderSpinner spinner = new LoaderSpinner();
        System.out.print("Saving data ");


        Thread saveThread = new Thread(() -> {
            this.tenantManager.saveToFile();
            this.propertyManager.saveToFile();
            this.ownerManager.saveToFile();
            this.rentalManager.saveToFile();
            this.hostManager.saveToFile();
        });


        saveThread.start();


        while (saveThread.isAlive()) {
            spinner.spin();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        spinner.stop();
        System.out.println("Data saved successfully.");
    }

    /**
     * Clears the console screen.
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prints the welcome message with ASCII art.
     */
    private void printWelcomeMessage() {
        System.out.println(TableFormatter.ANSI_CYAN);
        System.out.println("═".repeat(80));
        printCenteredASCII(RENTAL_ASCII);
        printCenteredASCII(MANAGER_ASCII);
        printCenteredASCII(SYSTEM_ASCII);
        System.out.println();
        System.out.println(centerText("Welcome to the Rental Management System", 80));
        System.out.println("═".repeat(80));
        System.out.println(TableFormatter.ANSI_RESET);
    }


    /**
     * Displays the main menu and handles user input.
     * @return The user's menu choice.
     */

    private String showMainMenu() {
        List<String> options = Arrays.asList(
                "Manage Rental Agreements",
                "Manage Tenants",
                "Manage Owners",
                "Manage Hosts",
                "Manage Properties",
                "Generate Reports",
                "Exit"
        );
        tableFormatter.printTable("MAIN MENU", options, TableFormatter.ANSI_GREEN);
        printStatusBar();
        return readUserInput("Enter your choice: ");
    }

    /**
     * Prints centered ASCII art.
     * @param ascii The ASCII art to be centered and printed
     */
    private void printCenteredASCII(String[] ascii) {
        int maxWidth = Arrays.stream(ascii).mapToInt(String::length).max().orElse(0);
        for (String line : ascii) {
            int padding = (80 - line.length()) / 2;
            System.out.println(" ".repeat(padding) + line);
        }
    }

    /**
     * Centers text within a given width.
     * @param text The text to be centered
     * @param width The width to center within
     * @return The centered text
     */
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    /**
     * Prints the status bar with current user, date, and time.
     */
    private void printStatusBar() {
        String currentUser = "Admin";
        String currentDate = LocalDate.now().toString();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        tableFormatter.printStatusBar(currentUser, currentDate, currentTime);
    }

    private boolean confirmDeletion(String entityType) {
        while (true) {
            String confirmation = readUserInput("Are you sure you want to delete this " + entityType + "? (yes/no): ").toLowerCase();
            if (confirmation.equals("yes")) {
                return true;
            } else if (confirmation.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }


    /**
     * Handles the Rental Agreements submenu.
     * Allows adding, updating, deleting, listing, and searching rental agreements.
     */

    private void handleRentalAgreements() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Agreement", "Update Agreement", "Delete Agreement",
                    "List Agreements", "Search Agreements", "Add/Remove Tenant to Existing Agreement", "Back to Main Menu"
            );
            tableFormatter.printTable("RENTAL AGREEMENTS", options, TableFormatter.ANSI_BLUE);
            String choice = readUserInput("Enter your choice: ");


            switch (choice) {
                case "1":
                    addRentalAgreement();
                    break;
                case "2":
                    updateRentalAgreement();
                    break;
                case "3":
                    deleteRentalAgreement();
                    break;
                case "4":
                    listRentalAgreements();
                    break;
                case "5":
                    searchRentalAgreements();
                    break;
                case "6":
                    addRemoveTenantToExistingAgreement();  // New option for adding/removing tenant to existing agreement
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }
    /**
     * Handles the Tenants submenu.
     * Allows adding, updating, deleting, listing, and searching tenants.
     */
    private void handleTenants() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Tenant", "Update Tenant", "Delete Tenant",
                    "List Tenants", "Search Tenants", "Back to Main Menu"
            );
            tableFormatter.printTable("TENANTS", options, TableFormatter.ANSI_PURPLE);
            String choice = readUserInput("Enter your choice: ");


            switch (choice) {
                case "1":
                    addTenant();
                    break;
                case "2":
                    updateTenant();
                    break;
                case "3":
                    deleteTenant();
                    break;
                case "4":
                    listTenants();
                    break;
                case "5":
                    searchTenants();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }


    /**
     * Handles the Owners submenu.
     * Allows adding, updating, deleting, listing, and searching owners.
     */
    private void handleOwners() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Owner", "Update Owner", "Delete Owner",
                    "List Owners", "Search Owners", "Back to Main Menu"
            );
            tableFormatter.printTable("OWNERS", options, TableFormatter.ANSI_YELLOW);
            String choice = readUserInput("Enter your choice: ");


            switch (choice) {
                case "1":
                    addOwner();
                    break;
                case "2":
                    updateOwner();
                    break;
                case "3":
                    deleteOwner();
                    break;
                case "4":
                    listOwners();
                    break;
                case "5":
                    searchOwners();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }


    /**
     * Handles the Hosts submenu.
     * Allows adding, updating, deleting, listing, and searching hosts.
     */
    private void handleHosts() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Host", "Update Host", "Delete Host",
                    "List Hosts", "Search Hosts", "Back to Main Menu"
            );
            tableFormatter.printTable("HOSTS", options, TableFormatter.ANSI_CYAN);
            String choice = readUserInput("Enter your choice: ");


            switch (choice) {
                case "1":
                    addHost();
                    break;
                case "2":
                    updateHost();
                    break;
                case "3":
                    deleteHost();
                    break;
                case "4":
                    listHosts();
                    break;
                case "5":
                    searchHosts();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }


    /**
     * Handles the Properties submenu.
     * Allows adding, updating, deleting, listing, and searching properties.
     */
    private void handleProperties() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Property", "Update Property", "Delete Property",
                    "List Properties", "Search Properties", "Back to Main Menu"
            );
            tableFormatter.printTable("PROPERTIES", options, TableFormatter.ANSI_YELLOW);
            String choice = readUserInput("Enter your choice: ");


            switch (choice) {
                case "1":
                    addProperty();
                    break;
                case "2":
                    updateProperty();
                    break;
                case "3":
                    deleteProperty();
                    break;
                case "4":
                    listProperties();
                    break;
                case "5":
                    searchProperties();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }


    /**
     * Handles the Reports submenu.
     * Allows generating various reports.
     */
    private void handleReports() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Income Report", "Occupancy Report", "Tenant Report",
                    "Property Status Report", "Tenant Payment History", "Host Performance Report",
                    "Back to Main Menu"
            );
            tableFormatter.printTable("REPORTS", options, TableFormatter.ANSI_RED);
            String choice = readUserInput("Enter your choice: ");


            ((RentalManagerImpl) rentalManager).updateAgreementStatuses(); // Add this line




            switch (choice) {
                case "1":
                    generateIncomeReport();
                    break;
                case "2":
                    generateOccupancyReport();
                    break;
                case "3":
                    generateTenantReport();
                    break;
                case "4":
                    generatePropertyStatusReport();
                    break;
                case "5":
                    generateTenantPaymentHistoryReport();
                    break;
                case "6":
                    generateHostPerformanceReport();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }


    /**
     * Generates and displays a property status report.
     */
    private void generatePropertyStatusReport() {
        List<Property> properties = propertyManager.getAll();
        List<String> headers = Arrays.asList("Property ID", "Type", "Address", "Status", "Owner ID - Name", "Host ID - Name");
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            String hostInfo = property.getHosts().stream()
                    .map(h -> h.getId() + " - " + h.getFullName())
                    .collect(Collectors.joining(", "));
            data.add(Arrays.asList(
                    property.getPropertyId(),
                    property instanceof ResidentialProperty ? "Residential" : "Commercial",
                    property.getAddress(),
                    property.getStatus().toString(),
                    property.getOwner().getId() + " - " + property.getOwner().getFullName(),
                    hostInfo.isEmpty() ? "No Host" : hostInfo
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    /**
     * Generates and displays a tenant payment history report.
     */
    private void generateTenantPaymentHistoryReport() {
        String tenantId = readUserInput("Enter tenant ID: ");
        Tenant tenant = tenantManager.get(tenantId);
        if (tenant == null) {
            System.out.println("Tenant not found.");
            return;
        }
        List<Payment> payments = tenant.getPayments();
        if (payments.isEmpty()) {
            System.out.println("No payment history found for this tenant.");
            return;
        }
        List<String> headers = Arrays.asList("Payment ID", "Date", "Amount", "Method", "Agreement ID");
        List<List<String>> data = new ArrayList<>();
        for (Payment payment : payments) {
            data.add(Arrays.asList(
                    payment.getPaymentId(),
                    dateFormat.format(payment.getPaymentDate()),
                    String.format("%.2f", payment.getAmount()),
                    payment.getPaymentMethod(),
                    payment.getRentalAgreement().getAgreementId()
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    /**
     * Reads user input from the console.
     * @param prompt The prompt to display to the user
     * @return The user's input as a string
     */
    private String readUserInput(String prompt) {
        System.out.print(prompt);
        return reader.readLine().trim();
    }


    /**
     * Generates and displays a host performance report.
     */
    private void generateHostPerformanceReport() {
        List<Host> hosts = hostManager.getAll();
        List<String> headers = Arrays.asList("Host ID", "Name", "Managed Properties", "Active Agreements", "Total Rent");
        List<List<String>> data = new ArrayList<>();
        for (Host host : hosts) {
            List<RentalAgreement> activeAgreements = rentalManager.getAll().stream()
                    .filter(a -> a.getHost().getId().equals(host.getId()) && a.isCurrentlyActive())
                    .collect(Collectors.toList());
            int managedProperties = (int) propertyManager.getAll().stream()
                    .filter(p -> p.getHosts().stream().anyMatch(h -> h.getId().equals(host.getId())))
                    .count();
            double totalRent = activeAgreements.stream()
                    .mapToDouble(RentalAgreement::getRentAmount)
                    .sum();
            data.add(Arrays.asList(
                    host.getId(),
                    host.getFullName(),
                    String.valueOf(managedProperties),
                    String.valueOf(activeAgreements.size()),
                    String.format("%.2f", totalRent)
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    /**
     * Reads user input from the console, allowing empty input.
     * @param prompt The prompt to display to the user
     * @return The user's input as a string, or an empty string if no input is provided
     */
    private String readUserInputAllowEmpty(String prompt) {
        System.out.print(prompt);
        return reader.readLine();
    }

    /**
     * Reads user input from the console, allowing escape (ESC key) to return null.
     * @param prompt The prompt to display to the user
     * @return The user's input as a string, or null if ESC was pressed
     */
    private String readUserInputAllowEsc(String prompt) {
        System.out.print(prompt);
        String input = reader.readLine().trim();
        if (input.equals("\u001B")) {
            return null;
        }
        return input;
    }

    /**
     * Prompts the user to input a property ID and returns the corresponding Property object.
     * @return The selected Property object
     * @throws RuntimeException if the property is not found
     */
    private Property getUserInputProperty() {
        String id = "";
        while (id.isBlank()) {
            id = readUserInput("Enter property ID: ");
            Property obj = propertyManager.get(id);
            if (obj != null) {
                return obj;
            }
            logError("Property id: " + id + " not found");
            id = "";
        }
        throw new RuntimeException("THIS NEVER HAPPEN");
    }

    /**
     * Prompts the user to input an owner ID and returns the corresponding Owner object.
     * @return The selected Owner object
     * @throws RuntimeException if the owner is not found
     */
    private Owner getUserInputOwner() {
        String id = "";
        while (id.isBlank()) {
            id = readUserInput("Enter owner ID: ");
            Owner obj = ownerManager.get(id);
            if (obj != null) {
                return obj;
            }
            logError("Owner id: " + id + " not found");
            id = "";
        }
        throw new RuntimeException("THIS NEVER HAPPEN");
    }

    /**
     * Prompts the user to input a host ID and returns the corresponding Host object.
     * @return The selected Host object
     * @throws RuntimeException if the host is not found
     */
    private Host getUserInputHost() {
        String id = "";
        while (id.isBlank()) {
            id = readUserInput("Enter host ID: ");
            Host obj = hostManager.get(id);
            if (obj != null) {
                return obj;
            }
            logError("Host id: " + id + " not found");
            id = "";
        }
        throw new RuntimeException("THIS NEVER HAPPEN");
    }

    /**
     * Prompts the user to input a tenant ID and returns the corresponding Tenant object.
     * @return The selected Tenant object, or null if not found
     */
    private Tenant getUserInputTenant() {
        String tenantId = readUserInput("Enter tenant ID: ");
        Tenant tenant = tenantManager.get(tenantId);
        if (tenant == null) {
            System.out.println("Tenant with ID " + tenantId + " not found.");
            return null;
        }
        return tenant;
    }


    /**
     * Adds a new rental agreement to the system.
     */
    private void addRentalAgreement() {
        String agreementId = null;
        while (agreementId == null) {
            agreementId = readUserInput("Enter agreement ID (or 'back' to return): ");
            if (agreementId.equalsIgnoreCase("back")) {
                return;
            }
            try {
                if (rentalManager.get(agreementId) != null) {
                    System.out.println(TableFormatter.ANSI_RED + "Agreement with ID " + agreementId + " already exists." + TableFormatter.ANSI_RESET);
                    agreementId = null;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                agreementId = null;
            }
        }

        Property property = getUserInputProperty();
        if (property == null) return;

        Tenant tenant = getUserInputTenant();
        if (tenant == null) return;

        Owner owner = getUserInputOwner();
        if (owner == null) return;

        Host host = getUserInputHost();
        if (host == null) return;

        Date startDate = DateUtil.readDate(reader, "Enter start date (yyyy-MM-dd): ");
        if (startDate == null) return;

        Date endDate = null;
        while (endDate == null || endDate.before(startDate)) {
            endDate = DateUtil.readDate(reader, "Enter end date (yyyy-MM-dd): ");
            if (endDate == null) return;
            if (endDate.before(startDate)) {
                System.out.println(TableFormatter.ANSI_RED + "End date must be after start date." + TableFormatter.ANSI_RESET);
            }
        }

        double rentAmount = InputValidator.readDouble(reader, "Enter rent amount: ", 0, Double.MAX_VALUE);
        if (rentAmount < 0) return;

        RentalAgreement.RentalPeriod rentalPeriod = null;
        while (rentalPeriod == null) {
            try {
                rentalPeriod = RentalAgreement.RentalPeriod.valueOf(
                        readUserInput("Enter rental period (DAILY/WEEKLY/FORTNIGHTLY/MONTHLY): ").toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid rental period. Please try again." + TableFormatter.ANSI_RESET);
            }
        }

        try {
            RentalAgreement agreement = new RentalAgreement(agreementId, property, tenant, owner, host, startDate, endDate, rentAmount, rentalPeriod);
            rentalManager.add(agreement);
            System.out.println(TableFormatter.ANSI_GREEN + "Rental agreement added successfully." + TableFormatter.ANSI_RESET);
            tableFormatter.printRentalAgreementTable(List.of(agreement));
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    /**
     * Logs an error message with red color.
     * @param error The error message to log
     */
    private void logError(String error) {
        System.out.println(TableFormatter.ANSI_RED + error + TableFormatter.ANSI_RESET);
    }

    /**
     * Adds or removes a tenant to/from an existing rental agreement.
     */
    private void addRemoveTenantToExistingAgreement() {
        // Prompt user for action (add or remove tenant)
        String action = readUserInput("Would you like to 'Add' or 'Remove' a tenant? (type 'Add' or 'Remove', or 'back' to return): ");
        if (action.equalsIgnoreCase("back")) {
            return;  // Exit if user wants to go back
        }


        if (!action.equalsIgnoreCase("Add") && !action.equalsIgnoreCase("Remove")) {
            System.out.println("Invalid action. Please choose 'Add' or 'Remove'.");
            return;
        }


        // Ask for the rental agreement ID
        String agreementId = readUserInput("Enter rental agreement ID to " + action.toLowerCase() + " tenant to (or 'back' to return): ");
        if (agreementId.equalsIgnoreCase("back")) {
            return;  // Exit if user wants to go back
        }


        // Fetch the existing rental agreement using the ID
        RentalAgreement agreement = rentalManager.get(agreementId);
        if (agreement == null) {
            System.out.println("Rental agreement with ID " + agreementId + " not found.");
            return;
        }


        // Ask for the tenant ID to add or remove
        String tenantId = readUserInput("Enter tenant ID: ");
        Tenant tenant = tenantManager.get(tenantId);
        if (tenant == null) {
            System.out.println("Tenant with ID " + tenantId + " not found.");
            return;
        }


        // Perform the action based on the user's choice
        if (action.equalsIgnoreCase("Add")) {
            agreement.addSubTenant(tenant);  // Add tenant to agreement
            System.out.println("Tenant " + tenant.getFullName() + " added to rental agreement " + agreementId);
        } else if (action.equalsIgnoreCase("Remove")) {
            agreement.removeSubTenant(tenantId);  // Remove tenant from agreement
            System.out.println("Tenant with ID " + tenantId + " removed from rental agreement " + agreementId);
        }


        // Optionally, save the updated rental agreement back to your manager or file
        rentalManager.update(agreement);


        // Call the saveToFile() method from RentalManager to persist the changes
        rentalManager.saveToFile(); // Ensure this method is available in the RentalManager interface/implementation
    }

    /**
     * Updates an existing rental agreement in the system.
     */
    private void updateRentalAgreement() {
        while (true) {
            String id = readUserInputAllowEsc("Enter agreement ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                RentalAgreement agreement = rentalManager.get(id);
                if (agreement == null) {
                    throw new IllegalArgumentException("Rental agreement not found.");
                }

                Date endDate = DateUtil.readOptionalDate(reader, "Enter new end date (yyyy-MM-dd, press enter to keep current): ");
                if (endDate != null) {
                    if (endDate.before(agreement.getStartDate())) {
                        throw new IllegalArgumentException("End date must be after start date.");
                    }
                    agreement.setEndDate(endDate);
                }

                String rentAmountStr = readUserInputAllowEmpty("Enter new rent amount (press enter to keep current): ");
                if (!rentAmountStr.isEmpty()) {
                    try {
                        double rentAmount = Double.parseDouble(rentAmountStr);
                        if (rentAmount < 0) {
                            throw new IllegalArgumentException("Rent amount must be non-negative.");
                        }
                        agreement.setRentAmount(rentAmount);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid rent amount format.");
                    }
                }

                String statusStr = readUserInputAllowEmpty("Enter new status (NEW/ACTIVE/COMPLETED, press enter to keep current): ");
                if (!statusStr.isEmpty()) {
                    try {
                        agreement.setStatus(RentalAgreement.Status.valueOf(statusStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid status.");
                    }
                }

                rentalManager.update(agreement);
                System.out.println(TableFormatter.ANSI_GREEN + "Rental agreement updated successfully." + TableFormatter.ANSI_RESET);
                tableFormatter.printRentalAgreementTable(List.of(agreement));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }


    /**
     * Deletes a rental agreement from the system.
     */
    private void deleteRentalAgreement() {
        while (true) {
            String agreementId = readUserInput("Enter agreement ID to delete (or 'back' to return): ");
            if (agreementId.equalsIgnoreCase("back")) {
                return;
            }

            try {
                RentalAgreement agreement = rentalManager.get(agreementId);
                if (agreement == null) {
                    throw new IllegalArgumentException("Rental agreement not found.");
                }

                tableFormatter.printRentalAgreementTable(List.of(agreement));

                if (confirmDeletion("rental agreement")) {
                    rentalManager.delete(agreementId);
                    System.out.println(TableFormatter.ANSI_GREEN + "Rental agreement deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    /**
     * Lists all rental agreements, sorted by a specified criteria.
     */
    private void listRentalAgreements() {
        String sortBy = readUserInput("Enter sort criteria (id/propertyid/tenantname/ownername/hostname/startdate/enddate/rentamount/status): ");
        try {
            List<RentalAgreement> agreements = rentalManager.getSorted(sortBy);
            displayRentalAgreements(agreements);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    /**
     * Searches for rental agreements based on a keyword.
     */
    private void searchRentalAgreements() {
        String keyword = readUserInput("Enter search keyword: ");
        List<RentalAgreement> results = rentalManager.searchRentalAgreements(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No rental agreements found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Property", "Tenant", "Start Date", "End Date", "Rent Amount", "Status");
            List<List<String>> data = new ArrayList<>();
            for (RentalAgreement agreement : results) {
                data.add(Arrays.asList(
                        agreement.getAgreementId(),
                        agreement.getProperty().getPropertyId(),
                        agreement.getMainTenant().getFullName(),
                        agreement.getStartDate().toString(),
                        agreement.getEndDate().toString(),
                        String.format("%.2f", agreement.getRentAmount()),
                        agreement.getStatus().toString()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }


    private void addTenant() {
        String id = null;
        while (id == null) {
            id = readUserInputAllowEsc("Enter tenant ID (press ESC to return): ");
            if (id == null) return;
            if (tenantManager.get(id) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Tenant with ID " + id + " already exists." + TableFormatter.ANSI_RESET);
                id = null;
            }
        }

        String fullName = readUserInputAllowEsc("Enter full name (press ESC to return): ");
        if (fullName == null) return;

        Date dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = readUserInputAllowEsc("Enter date of birth (yyyy-MM-dd, press ESC to return): ");
            if (dobInput == null) return;
            dateOfBirth = DateUtil.parseDate(dobInput);
            if (dateOfBirth == null) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid date format. Please use yyyy-MM-dd." + TableFormatter.ANSI_RESET);
            }
        }

        String contactInfo = null;
        while (contactInfo == null) {
            contactInfo = readUserInputAllowEsc("Enter contact information (email, press ESC to return): ");
            if (contactInfo == null) return;
            if (!InputValidator.isValidEmail(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid email format." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            } else if (tenantManager.isEmailTaken(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Email is already in use by another tenant." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            }
        }

        try {
            Tenant tenant = new Tenant(id, fullName, dateOfBirth, contactInfo);
            tenantManager.add(tenant);
            System.out.println(TableFormatter.ANSI_GREEN + "Tenant added successfully." + TableFormatter.ANSI_RESET);
            displayTenantDetails(tenant);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    private void updateTenant() {
        while (true) {
            String id = readUserInputAllowEsc("Enter tenant ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Tenant tenant = tenantManager.get(id);
                if (tenant == null) {
                    throw new IllegalArgumentException("Tenant not found.");
                }

                String fullName = readUserInputAllowEmpty("Enter new full name (press enter to keep current): ");
                if (!fullName.isEmpty()) {
                    tenant.setFullName(fullName);
                }

                Date dateOfBirth = DateUtil.readOptionalDate(reader, "Enter new date of birth (yyyy-MM-dd, press enter to keep current): ");
                if (dateOfBirth != null) {
                    tenant.setDateOfBirth(dateOfBirth);
                }

                String contactInfo = readUserInputAllowEmpty("Enter new contact information (email, press enter to keep current): ");
                if (!contactInfo.isEmpty()) {
                    if (!InputValidator.isValidEmail(contactInfo)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    } else if (!contactInfo.equals(tenant.getContactInformation()) && tenantManager.isEmailTaken(contactInfo)) {
                        throw new IllegalArgumentException("Email is already in use by another tenant.");
                    }
                    tenant.setContactInformation(contactInfo);
                }

                tenantManager.update(tenant);
                System.out.println(TableFormatter.ANSI_GREEN + "Tenant updated successfully." + TableFormatter.ANSI_RESET);
                displayTenantDetails(tenant);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }


    private void deleteTenant() {
        while (true) {
            String id = readUserInput("Enter tenant ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Tenant tenant = tenantManager.get(id);
                if (tenant == null) {
                    throw new IllegalArgumentException("Tenant not found.");
                }

                displayTenantDetails(tenant);

                if (confirmDeletion("tenant")) {
                    tenantManager.delete(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Tenant deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void displayTenantDetails(Tenant tenant) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> data = List.of(Arrays.asList(
                tenant.getId(),
                tenant.getFullName(),
                tenant.getDateOfBirth(),
                tenant.getContactInformation()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    private void listTenants() {
        String sortBy = readUserInput("Enter sort criteria (id/name/dob/email): ");
        try {
            List<Tenant> tenants = tenantManager.getSorted(sortBy);
            displayTenants(tenants);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    private void searchTenants() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Tenant> results = tenantManager.search(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No tenants found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
            List<List<String>> data = new ArrayList<>();
            for (Tenant tenant : results) {
                data.add(Arrays.asList(
                        tenant.getId(),
                        tenant.getFullName(),
                        tenant.getDateOfBirth(),
                        tenant.getContactInformation()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }


    private void addOwner() {
        String id = null;
        while (id == null) {
            id = readUserInputAllowEsc("Enter owner ID (press ESC to return): ");
            if (id == null) return;
            if (ownerManager.get(id) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Owner with ID " + id + " already exists." + TableFormatter.ANSI_RESET);
                id = null;
            }
        }

        String fullName = readUserInputAllowEsc("Enter full name (press ESC to return): ");
        if (fullName == null) return;

        Date dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = readUserInputAllowEsc("Enter date of birth (yyyy-MM-dd, press ESC to return): ");
            if (dobInput == null) return;
            dateOfBirth = DateUtil.parseDate(dobInput);
            if (dateOfBirth == null) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid date format. Please use yyyy-MM-dd." + TableFormatter.ANSI_RESET);
            }
        }

        String contactInfo = null;
        while (contactInfo == null) {
            contactInfo = readUserInputAllowEsc("Enter contact information (email, press ESC to return): ");
            if (contactInfo == null) return;
            if (!InputValidator.isValidEmail(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid email format." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            } else if (isEmailTaken(ownerManager.getAll(), contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Email is already in use by another owner." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            }
        }

        try {
            Owner owner = new Owner(id, fullName, dateOfBirth, contactInfo);
            ownerManager.add(owner);
            System.out.println(TableFormatter.ANSI_GREEN + "Owner added successfully." + TableFormatter.ANSI_RESET);
            displayOwnerDetails(owner);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void updateOwner() {
        while (true) {
            String id = readUserInputAllowEsc("Enter owner ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Owner owner = ownerManager.get(id);
                if (owner == null) {
                    throw new IllegalArgumentException("Owner not found.");
                }

                String fullName = readUserInputAllowEmpty("Enter new full name (press enter to keep current): ");
                if (!fullName.isEmpty()) {
                    owner.setFullName(fullName);
                }

                Date dateOfBirth = DateUtil.readOptionalDate(reader, "Enter new date of birth (yyyy-MM-dd, press enter to keep current): ");
                if (dateOfBirth != null) {
                    owner.setDateOfBirth(dateOfBirth);
                }

                String contactInfo = readUserInputAllowEmpty("Enter new contact information (email, press enter to keep current): ");
                if (!contactInfo.isEmpty()) {
                    if (!InputValidator.isValidEmail(contactInfo)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    } else if (!contactInfo.equals(owner.getContactInformation()) && isEmailTaken(ownerManager.getAll(), contactInfo)) {
                        throw new IllegalArgumentException("Email is already in use by another owner.");
                    }
                    owner.setContactInformation(contactInfo);
                }

                ownerManager.update(owner);
                System.out.println(TableFormatter.ANSI_GREEN + "Owner updated successfully." + TableFormatter.ANSI_RESET);
                displayOwnerDetails(owner);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }


    private void deleteOwner() {
        while (true) {
            String id = readUserInput("Enter owner ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Owner owner = ownerManager.get(id);
                if (owner == null) {
                    throw new IllegalArgumentException("Owner not found.");
                }

                displayOwnerDetails(owner);

                if (confirmDeletion("owner")) {
                    ownerManager.delete(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Owner deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void displayOwnerDetails(Owner owner) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> data = List.of(Arrays.asList(
                owner.getId(),
                owner.getFullName(),
                owner.getDateOfBirth(),
                owner.getContactInformation()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    private void listOwners() {
        String sortBy = readUserInput("Enter sort criteria (id/name/dob/email): ");
        try {
            List<Owner> owners = ownerManager.getSorted(sortBy);
            displayOwners(owners);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    private void searchOwners() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Owner> results = ownerManager.search(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No owners found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
            List<List<String>> data = new ArrayList<>();
            for (Owner owner : results) {
                data.add(Arrays.asList(
                        owner.getId(),
                        owner.getFullName(),
                        owner.getDateOfBirth(),
                        owner.getContactInformation()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }


    private void addHost() {
        String id = null;
        while (id == null) {
            id = readUserInputAllowEsc("Enter host ID (press ESC to return): ");
            if (id == null) return;
            if (hostManager.get(id) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Host with ID " + id + " already exists." + TableFormatter.ANSI_RESET);
                id = null;
            }
        }

        String fullName = readUserInputAllowEsc("Enter full name (press ESC to return): ");
        if (fullName == null) return;

        Date dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = readUserInputAllowEsc("Enter date of birth (yyyy-MM-dd, press ESC to return): ");
            if (dobInput == null) return;
            dateOfBirth = DateUtil.parseDate(dobInput);
            if (dateOfBirth == null) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid date format. Please use yyyy-MM-dd." + TableFormatter.ANSI_RESET);
            }
        }

        String contactInfo = null;
        while (contactInfo == null) {
            contactInfo = readUserInputAllowEsc("Enter contact information (email, press ESC to return): ");
            if (contactInfo == null) return;
            if (!InputValidator.isValidEmail(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid email format." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            } else if (hostManager.isEmailTaken(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Email is already in use by another host." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            }
        }

        try {
            Host host = new Host(id, fullName, dateOfBirth, contactInfo);
            hostManager.add(host);
            System.out.println(TableFormatter.ANSI_GREEN + "Host added successfully." + TableFormatter.ANSI_RESET);
            displayHostDetails(host);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    private void updateHost() {
        while (true) {
            String id = readUserInputAllowEsc("Enter host ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Host host = hostManager.get(id);
                if (host == null) {
                    throw new IllegalArgumentException("Host not found.");
                }

                String fullName = readUserInputAllowEmpty("Enter new full name (press enter to keep current): ");
                if (!fullName.isEmpty()) {
                    host.setFullName(fullName);
                }

                Date dateOfBirth = DateUtil.readOptionalDate(reader, "Enter new date of birth (yyyy-MM-dd, press enter to keep current): ");
                if (dateOfBirth != null) {
                    host.setDateOfBirth(dateOfBirth);
                }

                String contactInfo = readUserInputAllowEmpty("Enter new contact information (email, press enter to keep current): ");
                if (!contactInfo.isEmpty()) {
                    if (!InputValidator.isValidEmail(contactInfo)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    } else if (!contactInfo.equals(host.getContactInformation()) && hostManager.isEmailTaken(contactInfo)) {
                        throw new IllegalArgumentException("Email is already in use by another host.");
                    }
                    host.setContactInformation(contactInfo);
                }

                hostManager.update(host);
                System.out.println(TableFormatter.ANSI_GREEN + "Host updated successfully." + TableFormatter.ANSI_RESET);
                displayHostDetails(host);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }


    private void deleteHost() {
        while (true) {
            String id = readUserInput("Enter host ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Host host = hostManager.get(id);
                if (host == null) {
                    throw new IllegalArgumentException("Host not found.");
                }

                displayHostDetails(host);

                if (confirmDeletion("host")) {
                    hostManager.delete(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Host deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void displayHostDetails(Host host) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> data = List.of(Arrays.asList(
                host.getId(),
                host.getFullName(),
                host.getDateOfBirth(),
                host.getContactInformation()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    private void listHosts() {
        String sortBy = readUserInput("Enter sort criteria (id/name/dob/email): ");
        try {
            List<Host> hosts = hostManager.getSorted(sortBy);
            displayHosts(hosts);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    private void searchHosts() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Host> results = hostManager.search(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No hosts found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info", "Managed Properties");
            List<List<String>> data = new ArrayList<>();
            for (Host host : results) {
                data.add(Arrays.asList(
                        host.getId(),
                        host.getFullName(),
                        host.getDateOfBirth(),
                        host.getContactInformation(),
                        String.valueOf(host.getManagedProperties().size())
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }


    private void addProperty() {
        String propertyId = null;
        while (propertyId == null) {
            propertyId = readUserInput("Enter property ID (or 'back' to return): ");
            if (propertyId.equalsIgnoreCase("back")) {
                return;
            }
            if (propertyManager.get(propertyId) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Property with ID " + propertyId + " already exists." + TableFormatter.ANSI_RESET);
                propertyId = null;
            }
        }

        String propertyType = null;
        while (propertyType == null) {
            propertyType = readUserInput("Enter property type (residential/commercial): ").toLowerCase();
            if (!propertyType.equals("residential") && !propertyType.equals("commercial")) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid property type. Please enter 'residential' or 'commercial'." + TableFormatter.ANSI_RESET);
                propertyType = null;
            }
        }

        String address = readUserInput("Enter address: ");
        double price = InputValidator.readDouble(reader, "Enter price: ", 0, Double.MAX_VALUE);
        PropertyStatus status = readPropertyStatus();
        Owner owner = getUserInputOwner();

        if (owner == null) {
            System.out.println(TableFormatter.ANSI_RED + "Owner not found. Please add the owner first." + TableFormatter.ANSI_RESET);
            return;
        }

        Property property;
        try {
            if ("residential".equals(propertyType)) {
                int bedrooms = InputValidator.readInteger(reader, "Enter number of bedrooms: ", 0, Integer.MAX_VALUE);
                boolean hasGarden = InputValidator.readBoolean(reader, "Has garden? (true/false): ");
                boolean isPetFriendly = InputValidator.readBoolean(reader, "Is pet friendly? (true/false): ");
                property = new ResidentialProperty(propertyId, address, price, status, owner, bedrooms, hasGarden, isPetFriendly);
            } else {
                String businessType = readUserInput("Enter business type: ");
                int parkingSpaces = InputValidator.readInteger(reader, "Enter number of parking spaces: ", 0, Integer.MAX_VALUE);
                double squareFootage = InputValidator.readDouble(reader, "Enter square footage: ", 0, Double.MAX_VALUE);
                property = new CommercialProperty(propertyId, address, price, status, owner, businessType, parkingSpaces, squareFootage);
            }

            propertyManager.add(property);
            System.out.println(TableFormatter.ANSI_GREEN + "Property added successfully." + TableFormatter.ANSI_RESET);
            displayPropertyDetails(property);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }


    private void updateProperty() {
        while (true) {
            String id = readUserInputAllowEsc("Enter property ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Property property = propertyManager.get(id);
                if (property == null) {
                    throw new IllegalArgumentException("Property not found.");
                }

                String address = readUserInputAllowEmpty("Enter new address (press enter to keep current): ");
                if (!address.isEmpty()) {
                    property.setAddress(address);
                }

                String priceStr = readUserInputAllowEmpty("Enter new price (press enter to keep current): ");
                if (!priceStr.isEmpty()) {
                    try {
                        double price = Double.parseDouble(priceStr);
                        if (price < 0) {
                            throw new IllegalArgumentException("Price must be non-negative.");
                        }
                        property.setPrice(price);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid price format.");
                    }
                }

                PropertyStatus status = readOptionalPropertyStatus("Enter new status (press enter to keep current): ");
                if (status != null) {
                    property.setStatus(status);
                }

                if (property instanceof ResidentialProperty) {
                    updateResidentialProperty((ResidentialProperty) property);
                } else if (property instanceof CommercialProperty) {
                    updateCommercialProperty((CommercialProperty) property);
                }

                propertyManager.update(property);
                System.out.println(TableFormatter.ANSI_GREEN + "Property updated successfully." + TableFormatter.ANSI_RESET);
                displayPropertyDetails(property);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void deleteProperty() {
        while (true) {
            String id = readUserInput("Enter property ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Property property = propertyManager.get(id);
                if (property == null) {
                    throw new IllegalArgumentException("Property not found.");
                }

                displayPropertyDetails(property);

                if (confirmDeletion("property")) {
                    propertyManager.delete(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Property deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private PropertyStatus readPropertyStatus() {
        while (true) {
            try {
                String input = readUserInput("Enter status (AVAILABLE/RENTED/UNDER_MAINTENANCE): ").toUpperCase();
                return PropertyStatus.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid status. Please try again." + TableFormatter.ANSI_RESET);
            }
        }
    }

    private PropertyStatus readOptionalPropertyStatus(String prompt) {
        String input = readUserInputAllowEmpty(prompt);
        if (input.isEmpty()) {
            return null;
        }
        return readPropertyStatus();
    }


    private void updateResidentialProperty(ResidentialProperty property) {
        String bedroomsStr = readUserInputAllowEmpty("Enter new number of bedrooms (press enter to keep current): ");
        if (!bedroomsStr.isEmpty()) {
            try {
                int bedrooms = Integer.parseInt(bedroomsStr);
                if (bedrooms < 0) {
                    throw new IllegalArgumentException("Number of bedrooms must be non-negative.");
                }
                property.setNumberOfBedrooms(bedrooms);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for bedrooms.");
            }
        }

        String hasGardenStr = readUserInputAllowEmpty("Has garden? (true/false, press enter to keep current): ");
        if (!hasGardenStr.isEmpty()) {
            property.setHasGarden(Boolean.parseBoolean(hasGardenStr));
        }

        String isPetFriendlyStr = readUserInputAllowEmpty("Is pet friendly? (true/false, press enter to keep current): ");
        if (!isPetFriendlyStr.isEmpty()) {
            property.setPetFriendly(Boolean.parseBoolean(isPetFriendlyStr));
        }
    }

    private void updateCommercialProperty(CommercialProperty property) {
        String businessType = readUserInputAllowEmpty("Enter new business type (press enter to keep current): ");
        if (!businessType.isEmpty()) {
            property.setBusinessType(businessType);
        }

        String parkingSpacesStr = readUserInputAllowEmpty("Enter new number of parking spaces (press enter to keep current): ");
        if (!parkingSpacesStr.isEmpty()) {
            try {
                int parkingSpaces = Integer.parseInt(parkingSpacesStr);
                if (parkingSpaces < 0) {
                    throw new IllegalArgumentException("Number of parking spaces must be non-negative.");
                }
                property.setParkingSpaces(parkingSpaces);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for parking spaces.");
            }
        }

        String squareFootageStr = readUserInputAllowEmpty("Enter new square footage (press enter to keep current): ");
        if (!squareFootageStr.isEmpty()) {
            try {
                double squareFootage = Double.parseDouble(squareFootageStr);
                if (squareFootage < 0) {
                    throw new IllegalArgumentException("Square footage must be non-negative.");
                }
                property.setSquareFootage(squareFootage);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for square footage.");
            }
        }
    }



    private void listProperties() {
        String sortBy = readUserInput("Enter sort criteria (id/type/address/price/status/owner): ");
        try {
            List<Property> properties = propertyManager.getSorted(sortBy);
            displayProperties(properties);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void searchProperties() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Property> results = propertyManager.search(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No properties found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Type", "Address", "Price", "Status", "Owner");
            List<List<String>> data = new ArrayList<>();
            for (Property property : results) {
                data.add(Arrays.asList(
                        property.getPropertyId(),
                        property instanceof ResidentialProperty ? "Residential" : "Commercial",
                        property.getAddress(),
                        String.format("%.2f", property.getPrice()),
                        property.getStatus().toString(),
                        property.getOwner().getFullName()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }


    private void displayPropertyDetails(Property property) {
        List<String> headers = Arrays.asList("ID", "Type", "Address", "Price", "Status", "Owner");
        List<List<String>> data = List.of(Arrays.asList(
                property.getPropertyId(),
                property instanceof ResidentialProperty ? "Residential" : "Commercial",
                property.getAddress(),
                String.format("%.2f", property.getPrice()),
                property.getStatus().toString(),
                property.getOwner().getFullName()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void displayProperties(List<Property> properties) {
        List<String> headers = Arrays.asList(
                "Property ID", "Type", "Address", "Price", "Status", "Owner", "Hosts", "Tenants"
        );
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            String propertyType = property instanceof ResidentialProperty ? "Residential" : "Commercial";

            String ownerInfo = property.getOwner().getId() + " - " + property.getOwner().getFullName();

            String hostsInfo = property.getHosts().stream()
                    .map(h -> h.getId() + " - " + h.getFullName())
                    .collect(Collectors.joining(", "));
            if (hostsInfo.isEmpty()) {
                hostsInfo = "None";
            }

            String tenantsInfo = property.getTenants().stream()
                    .map(t -> t.getId() + " - " + t.getFullName())
                    .collect(Collectors.joining(", "));
            if (tenantsInfo.isEmpty()) {
                tenantsInfo = "None";
            }

            data.add(Arrays.asList(
                    property.getPropertyId(),
                    propertyType,
                    property.getAddress(),
                    String.format("%.2f", property.getPrice()),
                    property.getStatus().toString(),
                    ownerInfo,
                    hostsInfo,
                    tenantsInfo
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    /**
     * Generates and displays an income report.
     */


    private void generateIncomeReport() {
        double totalIncome = rentalManager.getTotalRentalIncome();
        System.out.println(TableFormatter.ANSI_GREEN + "Total Rental Income: $" + String.format("%.2f", totalIncome) + TableFormatter.ANSI_RESET);


        List<RentalAgreement> agreements = rentalManager.getAll();
        List<String> headers = Arrays.asList("Agreement ID", "Property", "Tenant ID - Name", "Rent Amount");
        List<List<String>> data = new ArrayList<>();
        for (RentalAgreement agreement : agreements) {
            data.add(Arrays.asList(
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    agreement.getMainTenant().getId() + " - " + agreement.getMainTenant().getFullName(),
                    String.format("%.2f", agreement.getRentAmount())
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    /**
     * Generates and displays an occupancy report.
     */


    private void generateOccupancyReport() {
        int totalProperties = propertyManager.getTotalProperties();
        int occupiedProperties = propertyManager.getOccupiedProperties();
        double occupancyRate = totalProperties > 0 ? (double) occupiedProperties / totalProperties * 100 : 0;
        System.out.println(TableFormatter.ANSI_GREEN + "Occupancy Rate: " + String.format("%.2f%%", occupancyRate) + TableFormatter.ANSI_RESET);


        List<Property> properties = propertyManager.getAll();
        List<String> headers = Arrays.asList("Property ID", "Type", "Status");
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            data.add(Arrays.asList(
                    property.getPropertyId(),
                    property instanceof ResidentialProperty ? "Residential" : "Commercial",
                    property.getStatus().toString()
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    /**
     * Generates and displays a tenant report.
     */


    private void generateTenantReport() {
        List<Tenant> tenants = tenantManager.getAll();
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info", "Active Agreements");
        List<List<String>> data = new ArrayList<>();
        for (Tenant tenant : tenants) {
            long activeAgreements = rentalManager.getAll().stream()
                    .filter(a -> a.getMainTenant().getId().equals(tenant.getId()) && a.isCurrentlyActive())
                    .count();
            data.add(Arrays.asList(
                    tenant.getId(),
                    tenant.getFullName(),
                    tenant.getDateOfBirth(),
                    tenant.getContactInformation(),
                    String.valueOf(activeAgreements)
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }






    private void displayRentalAgreements(List<RentalAgreement> agreements) {
        List<String> headers = Arrays.asList(
                "Agreement ID", "Property ID", "Main Tenant", "Sub-Tenants", "Owner", "Host",
                "Start Date", "End Date", "Rent Amount", "Status"
        );
        List<List<String>> data = new ArrayList<>();

        for (RentalAgreement agreement : agreements) {
            String mainTenantInfo = agreement.getMainTenant().getId() + " - " + agreement.getMainTenant().getFullName();

            String subTenantsInfo = agreement.getSubTenants().stream()
                    .map(t -> t.getId() + " - " + t.getFullName())
                    .collect(Collectors.joining(", "));

            if (subTenantsInfo.isEmpty()) {
                subTenantsInfo = "None";
            }

            data.add(Arrays.asList(
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    mainTenantInfo,
                    subTenantsInfo,
                    agreement.getOwner().getId() + " - " + agreement.getOwner().getFullName(),
                    agreement.getHost().getId() + " - " + agreement.getHost().getFullName(),
                    dateFormat.format(agreement.getStartDate()),
                    dateFormat.format(agreement.getEndDate()),
                    String.format("%.2f", agreement.getRentAmount()),
                    agreement.getStatus().toString()
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    private void displayTenants(List<Tenant> tenants) {
        List<String> headers = Arrays.asList(
                "Tenant ID", "Name", "DOB", "Email", "Rented Property",
                "Rental Contract ID", "Payment Amount", "Payment Date", "Payment Method"
        );
        List<List<String>> data = new ArrayList<>();
        for (Tenant tenant : tenants) {
            String rentedProperty = "None";
            String rentalContractId = "None";
            String paymentAmount = "Not Paid Yet";
            String paymentDate = "Not Paid Yet";
            String paymentMethod = "Not Paid Yet";
            if (!tenant.getRentalAgreements().isEmpty()) {
                RentalAgreement agreement = tenant.getRentalAgreements().get(0);
                rentedProperty = agreement.getProperty().getPropertyId();
                rentalContractId = agreement.getAgreementId();
                List<Payment> payments = tenant.getPayments();
                if (!payments.isEmpty()) {
                    Payment lastPayment = payments.get(payments.size() - 1);
                    paymentAmount = String.format("%.2f", lastPayment.getAmount());
                    paymentDate = dateFormat.format(lastPayment.getPaymentDate());
                    paymentMethod = lastPayment.getPaymentMethod();
                }
            }
            data.add(Arrays.asList(
                    tenant.getId(),
                    tenant.getFullName(),
                    tenant.getDateOfBirth(),
                    tenant.getContactInformation(),
                    rentedProperty,
                    rentalContractId,
                    paymentAmount,
                    paymentDate,
                    paymentMethod
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }




    private void displayOwners(List<Owner> owners) {
        List<String> headers = Arrays.asList(
                "Owner ID", "Name", "DOB", "Email", "Owned Properties", "Managing Hosts"
        );
        List<List<String>> data = new ArrayList<>();
        for (Owner owner : owners) {
            String ownedProperties = owner.getOwnedProperties().stream()
                    .map(Property::getPropertyId)
                    .collect(Collectors.joining(", "));


            String managingHosts = owner.getOwnedProperties().stream()
                    .flatMap(property -> property.getHosts().stream())
                    .distinct()
                    .map(host -> host.getId() + " - " + host.getFullName())
                    .collect(Collectors.joining(", "));


            if (ownedProperties.isEmpty()) ownedProperties = "None";
            if (managingHosts.isEmpty()) managingHosts = "None";


            data.add(Arrays.asList(
                    owner.getId(),
                    owner.getFullName(),
                    owner.getDateOfBirth(),
                    owner.getContactInformation(),
                    ownedProperties,
                    managingHosts
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    private void displayHosts(List<Host> hosts) {
        List<String> headers = Arrays.asList(
                "Host ID", "Name", "DOB", "Email", "Managed Properties", "Property Owners"
        );
        List<List<String>> data = new ArrayList<>();
        for (Host host : hosts) {
            String managedProperties = host.getManagedProperties().stream()
                    .map(Property::getPropertyId)
                    .collect(Collectors.joining(", "));


            String propertyOwners = host.getManagedProperties().stream()
                    .map(property -> property.getOwner().getId() + " - " + property.getOwner().getFullName())
                    .distinct()
                    .collect(Collectors.joining(", "));


            if (managedProperties.isEmpty()) managedProperties = "None";
            if (propertyOwners.isEmpty()) propertyOwners = "None";


            data.add(Arrays.asList(
                    host.getId(),
                    host.getFullName(),
                    host.getDateOfBirth(),
                    host.getContactInformation(),
                    managedProperties,
                    propertyOwners
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    /**
     * Main method to run the Rental Management System.
     */


    public static void main(String[] args) {
        try {
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        } catch (IOException e) {
            System.err.println("Error initializing the application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
