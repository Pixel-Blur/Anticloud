import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    private PasswordManager passwordManager;
    private Scanner scanner;
    private Console console;

    public MainApp() {
        this.passwordManager = new PasswordManager();
        this.scanner = new Scanner(System.in);
        this.console = System.console();
    }

    public static void main(String[] args) {
        // Check if GUI mode is requested
        if (args.length > 0 && args[0].equals("--gui")) {
            // Launch GUI
            javax.swing.SwingUtilities.invokeLater(() -> {
                new PasswordManagerGUI();
            });
        } else {
            // Launch CLI
            MainApp app = new MainApp();
            app.run();
        }
    }

    public void run() {
        System.out.println("=== Welcome to Secure Password Manager ===");
        System.out.println("(To use GUI mode, run with --gui argument)");

        while (true) {
            if (!passwordManager.isLoggedIn()) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                exit();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void showUserMenu() {
        System.out.println("\n=== Password Manager - " + passwordManager.getCurrentUser().getUsername() + " ===");
        System.out.println("1. Add new credential");
        System.out.println("2. View credentials");
        System.out.println("3. View password");
        System.out.println("4. Remove credential");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                addCredential();
                break;
            case 2:
                viewCredentials();
                break;
            case 3:
                viewPassword();
                break;
            case 4:
                removeCredential();
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        String password = readPassword("Enter password: ");
        String confirmPassword = readPassword("Confirm password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        PasswordManager.RegistrationResult result = passwordManager.registerUser(username, password);
        switch (result) {
            case SUCCESS:
                System.out.println("User registered successfully!");
                break;
            case USER_EXISTS:
                System.out.println("Username already exists.");
                break;
            case EMPTY_USERNAME:
                System.out.println("Username cannot be empty.");
                break;
            case WEAK_PASSWORD:
                System.out.println("Password must be at least 6 characters long.");
                break;
        }
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        String password = readPassword("Enter password: ");

        PasswordManager.LoginResult result = passwordManager.login(username, password);
        switch (result) {
            case SUCCESS:
                System.out.println("Login successful! Welcome, " + username);
                break;
            case USER_NOT_FOUND:
                System.out.println("User not found.");
                break;
            case INVALID_PASSWORD:
                System.out.println("Invalid password.");
                break;
            case EMPTY_CREDENTIALS:
                System.out.println("Username and password cannot be empty.");
                break;
        }
    }

    private void logout() {
        passwordManager.logout();
        System.out.println("Logged out successfully.");
    }

    private void addCredential() {
        System.out.print("Enter service name (e.g., Gmail, Facebook): ");
        String serviceName = scanner.nextLine().trim();

        System.out.print("Enter username/email: ");
        String username = scanner.nextLine().trim();

        String password = readPassword("Enter password: ");

        PasswordManager.CredentialResult result = passwordManager.addCredential(serviceName, username, password);
        switch (result) {
            case SUCCESS:
                System.out.println("Credential added successfully!");
                break;
            case EMPTY_FIELDS:
                System.out.println("All fields are required.");
                break;
            case NOT_LOGGED_IN:
                System.out.println("You must be logged in to add credentials.");
                break;
            case ENCRYPTION_ERROR:
                System.out.println("Error encrypting password.");
                break;
        }
    }

    private void viewCredentials() {
        List<ServiceCredential> credentials = passwordManager.getCredentials();

        if (credentials.isEmpty()) {
            System.out.println("No credentials stored.");
            return;
        }

        System.out.println("\n=== Stored Credentials ===");
        for (int i = 0; i < credentials.size(); i++) {
            System.out.println((i + 1) + ". " + credentials.get(i));
        }
    }

    private void viewPassword() {
        List<ServiceCredential> credentials = passwordManager.getCredentials();

        if (credentials.isEmpty()) {
            System.out.println("No credentials stored.");
            return;
        }

        viewCredentials();
        System.out.print("Enter the number of the credential to view password: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= credentials.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String masterPassword = readPassword("Enter your master password to view: ");

        PasswordManager.PasswordRetrievalResult result = passwordManager.getDecryptedPassword(
                credentials.get(index), masterPassword);

        switch (result.getResult()) {
            case SUCCESS:
                System.out.println("Password: " + result.getPassword());
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
                break;
            case INVALID_MASTER_PASSWORD:
                System.out.println("Invalid master password.");
                break;
            case NOT_LOGGED_IN:
                System.out.println("You must be logged in.");
                break;
            case ENCRYPTION_ERROR:
                System.out.println("Error decrypting password.");
                break;
        }
    }

    private void removeCredential() {
        List<ServiceCredential> credentials = passwordManager.getCredentials();

        if (credentials.isEmpty()) {
            System.out.println("No credentials stored.");
            return;
        }

        viewCredentials();
        System.out.print("Enter the number of the credential to remove: ");
        int index = getIntInput() - 1;

        if (passwordManager.removeCredential(index)) {
            System.out.println("Credential removed successfully!");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private String readPassword(String prompt) {
        System.out.print(prompt);
        if (console != null) {
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            // Fallback for IDEs that don't support Console
            return scanner.nextLine();
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void exit() {
        if (passwordManager.isLoggedIn()) {
            passwordManager.logout();
        }
        System.out.println("Thank you for using Secure Password Manager!");
        System.exit(0);
    }
}
