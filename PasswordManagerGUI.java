package anticloud;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PasswordManagerGUI extends JFrame {
    private final PasswordManager passwordManager;
    private JPanel currentPanel;
    
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    
    // Main dashboard components
    private JTable credentialsTable;
    private DefaultTableModel tableModel;
    private JButton addCredentialBtn;
    private JButton viewPasswordBtn;
    private JButton removeCredentialBtn;
    private JButton logoutBtn;

    public PasswordManagerGUI() {
        this.passwordManager = new PasswordManager();
        initializeGUI();
        showLoginPanel();
    }

    private void initializeGUI() {
        setTitle("Secure Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Use default look and feel if system L&F fails
        }

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (passwordManager.isLoggedIn()) {
                    passwordManager.logout();
                }
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void showLoginPanel() {
        currentPanel = createLoginPanel();
        setContentPane(currentPanel);
        revalidate();
        repaint();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Secure Password Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        centerPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        centerPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> showRegisterPanel());
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        centerPanel.add(buttonPanel, gbc);

        // Status label
        gbc.gridy = 3;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        centerPanel.add(statusLabel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Add Enter key functionality
        passwordField.addActionListener(e -> handleLogin());

        return panel;
    }

    private void showRegisterPanel() {
        currentPanel = createRegisterPanel();
        setContentPane(currentPanel);
        revalidate();
        repaint();
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        centerPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        centerPanel.add(passwordField, gbc);

        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 2;
        centerPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        centerPanel.add(confirmPasswordField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");
        
        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> showLoginPanel());
        
        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);
        centerPanel.add(buttonPanel, gbc);

        // Status label
        gbc.gridy = 4;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        centerPanel.add(statusLabel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Add Enter key functionality
        confirmPasswordField.addActionListener(e -> handleRegister());

        return panel;
    }

    private void showDashboard() {
        currentPanel = createDashboardPanel();
        setContentPane(currentPanel);
        revalidate();
        repaint();
        refreshCredentialsTable();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title with user info
        String username = passwordManager.getCurrentUser().getUsername();
        JLabel titleLabel = new JLabel("Password Manager - " + username, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with table
        String[] columnNames = {"Service", "Username"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        credentialsTable = new JTable(tableModel);
        credentialsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        credentialsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(credentialsTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addCredentialBtn = new JButton("Add Credential");
        viewPasswordBtn = new JButton("View Password");
        removeCredentialBtn = new JButton("Remove Credential");
        logoutBtn = new JButton("Logout");

        addCredentialBtn.addActionListener(e -> showAddCredentialDialog());
        viewPasswordBtn.addActionListener(e -> showViewPasswordDialog());
        removeCredentialBtn.addActionListener(e -> handleRemoveCredential());
        logoutBtn.addActionListener(e -> handleLogout());

        buttonPanel.add(addCredentialBtn);
        buttonPanel.add(viewPasswordBtn);
        buttonPanel.add(removeCredentialBtn);
        buttonPanel.add(logoutBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        PasswordManager.LoginResult result = passwordManager.login(username, password);
        
        switch (result) {
            case SUCCESS:
                showDashboard();
                break;
            case USER_NOT_FOUND:
                showStatus("User not found.", true);
                break;
            case INVALID_PASSWORD:
                showStatus("Invalid password.", true);
                break;
            case EMPTY_CREDENTIALS:
                showStatus("Please enter username and password.", true);
                break;
        }
        
        // Clear password field
        passwordField.setText("");
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match.", true);
            return;
        }

        PasswordManager.RegistrationResult result = passwordManager.registerUser(username, password);
        
        switch (result) {
            case SUCCESS:
                showStatus("Registration successful! You can now login.", false);
                // Clear fields and go back to login after a delay
                Timer timer = new Timer(2000, e -> showLoginPanel());
                timer.setRepeats(false);
                timer.start();
                break;
            case USER_EXISTS:
                showStatus("Username already exists.", true);
                break;
            case EMPTY_USERNAME:
                showStatus("Username cannot be empty.", true);
                break;
            case WEAK_PASSWORD:
                showStatus("Password must be at least 6 characters long.", true);
                break;
        }
        
        // Clear password fields
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void handleLogout() {
        passwordManager.logout();
        showLoginPanel();
    }

    private void showAddCredentialDialog() {
        JDialog dialog = new JDialog(this, "Add New Credential", true);
        dialog.setSize(550, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Service name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Service Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField serviceField = new JTextField(30);
        serviceField.setPreferredSize(new Dimension(300, 25));
        panel.add(serviceField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField userField = new JTextField(30);
        userField.setPreferredSize(new Dimension(300, 25));
        panel.add(userField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPasswordField passField = new JPasswordField(30);
        passField.setPreferredSize(new Dimension(300, 25));
        panel.add(passField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            String serviceName = serviceField.getText().trim();
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            PasswordManager.CredentialResult result = passwordManager.addCredential(serviceName, username, password);
            
            if (result == PasswordManager.CredentialResult.SUCCESS) {
                refreshCredentialsTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Credential added successfully!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Error: " + getCredentialResultMessage(result), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showViewPasswordDialog() {
        int selectedRow = credentialsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a credential to view.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<ServiceCredential> credentials = passwordManager.getCredentials();
        ServiceCredential credential = credentials.get(selectedRow);

        // Ask for master password
        JPasswordField masterPasswordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, 
            new Object[]{"Enter your master password:", masterPasswordField},
            "Master Password Required", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String masterPassword = new String(masterPasswordField.getPassword());
            
            PasswordManager.PasswordRetrievalResult result = passwordManager.getDecryptedPassword(credential, masterPassword);
            
            if (result.isSuccess()) {
                // Show password in a dialog
                JDialog passwordDialog = new JDialog(this, "Password for " + credential.getServiceName(), true);
                passwordDialog.setSize(400, 300);
                passwordDialog.setLocationRelativeTo(this);

                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBorder(new EmptyBorder(20, 20, 20, 20));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);

                gbc.gridx = 0; gbc.gridy = 0;
                panel.add(new JLabel("Service:"), gbc);
                gbc.gridx = 1;
                panel.add(new JLabel(credential.getServiceName()), gbc);

                gbc.gridx = 0; gbc.gridy = 1;
                panel.add(new JLabel("Username:"), gbc);
                gbc.gridx = 1;
                panel.add(new JLabel(credential.getUsername()), gbc);

                gbc.gridx = 0; gbc.gridy = 2;
                panel.add(new JLabel("Password:"), gbc);
                gbc.gridx = 1;
                JTextField passwordDisplay = new JTextField(result.getPassword());
                passwordDisplay.setEditable(false);
                passwordDisplay.setBackground(Color.LIGHT_GRAY);
                panel.add(passwordDisplay, gbc);

                gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
                JButton closeBtn = new JButton("Close");
                closeBtn.addActionListener(e -> passwordDialog.dispose());
                panel.add(closeBtn, gbc);

                passwordDialog.add(panel);
                passwordDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, getCredentialResultMessage(result.getResult()), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRemoveCredential() {
        int selectedRow = credentialsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a credential to remove.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove this credential?", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (passwordManager.removeCredential(selectedRow)) {
                refreshCredentialsTable();
                JOptionPane.showMessageDialog(this, "Credential removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error removing credential.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCredentialsTable() {
        tableModel.setRowCount(0);
        List<ServiceCredential> credentials = passwordManager.getCredentials();
        
        for (ServiceCredential credential : credentials) {
            tableModel.addRow(new Object[]{credential.getServiceName(), credential.getUsername()});
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : Color.GREEN);
    }

    private String getCredentialResultMessage(PasswordManager.CredentialResult result) {
        switch (result) {
            case EMPTY_FIELDS:
                return "All fields are required.";
            case NOT_LOGGED_IN:
                return "You must be logged in.";
            case ENCRYPTION_ERROR:
                return "Error with encryption/decryption.";
            case INVALID_MASTER_PASSWORD:
                return "Invalid master password.";
            default:
                return "Unknown error.";
        }
    }
}