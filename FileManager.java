package Anticloud;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private static final String DATA_DIR = "password_manager_data";
    private static final String USERS_FILE = "users.dat";
    private static final String CREDENTIALS_DIR = "credentials";

    static {
        createDataDirectories();
    }

    private static void createDataDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(DATA_DIR, CREDENTIALS_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directories", e);
        }
    }

    // Save users to file
    public static void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(Paths.get(DATA_DIR, USERS_FILE).toString()))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("Error saving users", e);
        }
    }

    // Load users from file
    @SuppressWarnings("unchecked")
    public static Map<String, User> loadUsers() {
        Path usersPath = Paths.get(DATA_DIR, USERS_FILE);
        if (!Files.exists(usersPath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(usersPath.toString()))) {
            return (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Save credentials for a user
    public static void saveUserCredentials(String username, List<ServiceCredential> credentials) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(getUserCredentialsPath(username)))) {
            oos.writeObject(credentials);
        } catch (IOException e) {
            throw new RuntimeException("Error saving credentials", e);
        }
    }

    // Load credentials for a user
    @SuppressWarnings("unchecked")
    public static List<ServiceCredential> loadUserCredentials(String username) {
        String credentialsPath = getUserCredentialsPath(username);
        if (!Files.exists(Paths.get(credentialsPath))) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(credentialsPath))) {
            return (List<ServiceCredential>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading credentials: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static String getUserCredentialsPath(String username) {
        return Paths.get(DATA_DIR, CREDENTIALS_DIR, username + "_credentials.dat").toString();
    }
}