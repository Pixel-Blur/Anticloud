Anti-cloud Password Manager - User Guide
Prerequisites
You need Java installed on your computer:
Download and install Java JDK 8 or higher from Oracle or OpenJDK
Verify installation by opening a terminal/command prompt and typing: java -version
Compile the Code
Open a terminal/command prompt, navigate to your folder containing the Java files, and run:
javac *.java

Running the Application
Option 1: Graphical Interface (Recommended for beginners)
java MainApp --gui

Option 2: Command Line Interface
java MainApp

First Time Usage
1. Create Your Account
GUI Mode:
Click "Register"
Enter a username (this is just for the app, not your actual accounts)
Create a strong master password (at least 6 characters)
Confirm your password
Click "Register"
CLI Mode:
Choose option "1. Register"
Follow the prompts to enter username and password
2. Login
Use the username and master password you just created
This master password will encrypt all your stored passwords
3. Add Your First Password
GUI Mode:
Click "Add Credential"
Enter:
Service Name: e.g., "Gmail", "Facebook", "Banking"
Username: Your username/email for that service
Password: Your actual password for that service
Click "Save"
CLI Mode:
Choose "1. Add new credential"
Enter the service name, username, and password when prompted
4. View Your Stored Passwords
GUI Mode:
Your credentials appear in the table
Select a row and click "View Password"
Enter your master password to decrypt and view the actual password
CLI Mode:
Choose "2. View credentials" to see the list
Choose "3. View password" to decrypt and see a specific password
Important Security Notes
Remember your master password - If you forget it, you cannot recover your stored passwords
The app creates a folder called password_manager_data where your encrypted data is stored
Keep this data folder safe - Consider backing it up to a secure location
Use a strong master password - This protects all your other passwords
Features Overview
Secure encryption: Uses AES-256 encryption with PBKDF2 key derivation
User accounts: Multiple people can use the same installation
Password protection: Must enter master password to view stored passwords
Safe storage: Passwords are encrypted before being saved to disk
Cross-platform: Works on Windows, Mac, and Linux
Additional Operations
Removing Stored Credentials
GUI Mode:
Select the credential from the table
Click "Remove Credential"
Confirm the deletion
CLI Mode:
Choose "4. Remove credential"
Select the credential number to remove
Logging Out
GUI Mode:
Click "Logout" button
CLI Mode:
Choose "5. Logout"
Multiple Users
The application supports multiple users on the same computer. Each user will have their own encrypted credential storage, separated by username.
