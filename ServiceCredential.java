import java.io.Serializable;

public class ServiceCredential implements Serializable {
    private static final long serialVersionUID = 1L;

    private String serviceName;
    private String username;
    private byte[] encryptedPassword;
    private byte[] iv;

    public ServiceCredential(String serviceName, String username, CryptoUtil.EncryptedData encryptedPassword) {
        this.serviceName = serviceName;
        this.username = username;
        this.encryptedPassword = encryptedPassword.getData();
        this.iv = encryptedPassword.getIv();
    }

    // Getters
    public String getServiceName() { return serviceName; }
    public String getUsername() { return username; }
    public CryptoUtil.EncryptedData getEncryptedPassword() {
        return new CryptoUtil.EncryptedData(encryptedPassword, iv);
    }

    @Override
    public String toString() {
        return String.format("Service: %s | Username: %s", serviceName, username);
    }
}