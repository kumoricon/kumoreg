package org.kumoricon.model.user;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.role.Role;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name = "users")
public class User implements Serializable {
    public static final String DEFAULT_PASSWORD = "password";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
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
    @ManyToOne(cascade=CascadeType.MERGE)
    private Role role;
    private String salt;

    public User() {
        this.id = null;
        this.salt = "TempSalt"; // Todo: randomly generate salt
        this.enabled = true;
    }

    public User(String firstName, String lastName) {
        this();
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(DEFAULT_PASSWORD);
        setUsername(generateUserName(firstName, lastName));
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username != null) { this.username = username.toLowerCase(); }
    }

    public String getPassword() { return password; }
    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.trim().equals("")) {
            throw new ValueException("Password cannot be blank");
        }
        this.password = hashPassword(newPassword, salt);
    }

    public Boolean checkPassword(String password) {
        return (hashPassword(password, salt).equals(this.password));
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    private static String hashPassword(String password, String salt){
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(
                passwordChars,
                saltBytes,
                1000,
                256);
        byte[] hashedPassword;
        try {
            SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hashedPassword = key.generateSecret(spec).getEncoded();
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

    public static String generateUserName(String firstName, String lastName) {
        // Remove anything that isn't a letter and combine to first initial + last name
        // Doesn't do uniqueness checking
        String username;
        firstName = firstName.replaceAll("[^\\p{L}]", "");
        if (firstName.length() > 0) {
            firstName = firstName.substring(0, 1);
        }
        lastName = lastName.replaceAll("[^\\p{L}]", "");

        username = firstName + lastName;
        return username.toLowerCase();
    }

    public String toString() {
        if (firstName != null && lastName != null) {
            return String.format("%s (%s %s)", username, firstName, lastName);
        } else {
            return username;
        }
    }
}
