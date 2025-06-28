import java.util.*;
import javax.crypto.SecretKey;

public class PasswordManager {
    private Map<String, User> users;
    private User currentUser;
    private List<ServiceCredential> currentUserCredentials;
    private SecretKey currentUserKey;

    public PasswordManager() {
        this.users = FileManager.loadUsers();
        this.currentUserCredentials = new ArrayList<>();
    }

    // Register a new user
    public RegistrationResult registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return RegistrationResult.EMPTY_USERNAME;
        }
        if (password == null || password.length() < 6) {
            return RegistrationResult.WEAK_PASSWORD;
        }
        if (users.containsKey(username)) {
            return RegistrationResult.USER_EXISTS;
        }

        User newUser = new User(username, password);
        users.put(username, newUser);
        FileManager.saveUsers(users);
        return RegistrationResult.SUCCESS;
    }

    // Enum for registration results
    public enum RegistrationResult {
        SUCCESS, USER_EXISTS, EMPTY_USERNAME, WEAK_PASSWORD
    }

    // Login user
    public LoginResult login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return LoginResult.EMPTY_CREDENTIALS;
        }
        if (password == null || password.isEmpty()) {
            return LoginResult.EMPTY_CREDENTIALS;
        }

        User user = users.get(username);
        if (user == null) {
            return LoginResult.USER_NOT_FOUND;
        }
        if (!user.authenticate(password)) {
            return LoginResult.INVALID_PASSWORD;
        }

        currentUser = user;
        currentUserKey = CryptoUtil.deriveKey(password, user.getSalt());
        currentUserCredentials = FileManager.loadUserCredentials(username);
        return LoginResult.SUCCESS;
    }

    // Enum for login results
    public enum LoginResult {
        SUCCESS, USER_NOT_FOUND, INVALID_PASSWORD, EMPTY_CREDENTIALS
    }

    // Add new service credential
    public CredentialResult addCredential(String serviceName, String username, String password) {
        if (currentUser == null) {
            return CredentialResult.NOT_LOGGED_IN;
        }

        if (serviceName == null || serviceName.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            return CredentialResult.EMPTY_FIELDS;
        }

        try {
            CryptoUtil.EncryptedData encryptedPassword = CryptoUtil.encrypt(password, currentUserKey);
            ServiceCredential credential = new ServiceCredential(serviceName, username, encryptedPassword);

            currentUserCredentials.add(credential);
            FileManager.saveUserCredentials(currentUser.getUsername(), currentUserCredentials);
            return CredentialResult.SUCCESS;
        } catch (Exception e) {
            return CredentialResult.ENCRYPTION_ERROR;
        }
    }

    // Enum for credential operation results
    public enum CredentialResult {
        SUCCESS, NOT_LOGGED_IN, EMPTY_FIELDS, ENCRYPTION_ERROR, INVALID_MASTER_PASSWORD
    }

    // Logout current user
    public void logout() {
        currentUser = null;
        currentUserKey = null;
        if (currentUserCredentials != null) {
            currentUserCredentials.clear();
        }
        // Clear sensitive data from memory
        System.gc();
    }

    // Get all credentials for current user
    public List<ServiceCredential> getCredentials() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentUserCredentials);
    }

    // Decrypt and get password for a specific credential
    public PasswordRetrievalResult getDecryptedPassword(ServiceCredential credential, String masterPassword) {
        if (currentUser == null) {
            return new PasswordRetrievalResult(CredentialResult.NOT_LOGGED_IN, null);
        }

        // Verify master password
        if (!currentUser.authenticate(masterPassword)) {
            return new PasswordRetrievalResult(CredentialResult.INVALID_MASTER_PASSWORD, null);
        }

        try {
            String password = CryptoUtil.decrypt(credential.getEncryptedPassword(), currentUserKey);
            return new PasswordRetrievalResult(CredentialResult.SUCCESS, password);
        } catch (Exception e) {
            return new PasswordRetrievalResult(CredentialResult.ENCRYPTION_ERROR, null);
        }
    }

    // Result class for password retrieval
    public static class PasswordRetrievalResult {
        private final CredentialResult result;
        private final String password;

        public PasswordRetrievalResult(CredentialResult result, String password) {
            this.result = result;
            this.password = password;
        }

        public CredentialResult getResult() { return result; }
        public String getPassword() { return password; }
        public boolean isSuccess() { return result == CredentialResult.SUCCESS; }
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // Get current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Remove a credential
    public boolean removeCredential(int index) {
        if (currentUser == null) {
            return false;
        }

        if (index >= 0 && index < currentUserCredentials.size()) {
            currentUserCredentials.remove(index);
            FileManager.saveUserCredentials(currentUser.getUsername(), currentUserCredentials);
            return true;
        }
        return false;
    }
}