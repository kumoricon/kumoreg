package org.kumoricon.model.user;


import org.kumoricon.model.Record;
import org.kumoricon.model.role.Role;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

@Entity
@Table(name = "users")
public class User extends Record {
    public static final String DEFAULT_PASSWORD = "password";

    @NotNull
    @Column(unique=true)
    private String username;
    @NotNull
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    @NotNull
    private Boolean enabled;
    @NotNull
    private Boolean resetPassword;              // On login, prompt to reset password
    @ManyToOne(cascade=CascadeType.MERGE)
    private Role role;
    private String salt;
    @NotNull
    @Column(unique = true)
    private String badgePrefix;                 // User will generate badges with this prefix
    @NotNull
    private Integer lastBadgeNumberCreated;

    /**
     * Creating a new user? Use UserFactory instead of creating the user object directly
     */
    public User() {}

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username != null) { this.username = username.toLowerCase(); }
    }

    public String getPassword() { return password; }
    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.trim().equals("")) {
            throw new RuntimeException("Password cannot be blank");
        }
        this.password = hashPassword(newPassword, salt);
    }

    public Boolean checkPassword(String password) {
        if (password == null) return false;
        String hash = hashPassword(password, salt);

        return hash != null && hash.equals(this.password);
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getResetPassword() { return resetPassword; }
    public void setResetPassword(Boolean resetPassword) { this.resetPassword = resetPassword; }

    public void resetPassword() {
        setPassword(DEFAULT_PASSWORD);
        this.resetPassword = true;
    }

    public String getBadgePrefix() { return badgePrefix; }
    public void setBadgePrefix(String badgePrefix) { this.badgePrefix = badgePrefix; }

    public Integer getLastBadgeNumberCreated() { return lastBadgeNumberCreated; }
    public void setLastBadgeNumberCreated(Integer lastBadgeNumberCreated) { this.lastBadgeNumberCreated = lastBadgeNumberCreated; }
    public Integer getNextBadgeNumber() {
        if (lastBadgeNumberCreated == null) { lastBadgeNumberCreated = 0; }
        lastBadgeNumberCreated += 1;
        return lastBadgeNumberCreated;
    }

    private static String hashPassword(String password, String salt){
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(UTF_8);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, 1000, 256);
        try {
            SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPassword = key.generateSecret(spec).getEncoded();
            return String.format("%x", new BigInteger(hashedPassword));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String toString() {
        if (id != null) {
            return String.format("[User %s: %s]", id, username);
        } else {
            return String.format("[User: %s]", username);
        }
    }

    public boolean hasRight(String right) {
        return role != null && role.hasRight(right);
    }
}
