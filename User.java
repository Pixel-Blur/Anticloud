import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String hashedPassword;
    private byte[] salt;

    public User(String username, String password) {
        this.username = username;
        this.salt = CryptoUtil.generateSalt();
        this.hashedPassword = CryptoUtil.hashPassword(password, salt);
    }

    public boolean authenticate(String password) {
        String inputHash = CryptoUtil.hashPassword(password, salt);
        return hashedPassword.equals(inputHash);
    }

    // Getters
    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }
    public byte[] getSalt() { return salt; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
