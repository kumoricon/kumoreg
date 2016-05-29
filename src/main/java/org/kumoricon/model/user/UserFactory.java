package org.kumoricon.model.user;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.SecureRandom;
import java.util.Random;

public class UserFactory {
    private static final Random random = new SecureRandom();

    /**
     * Generate a blank User record with random password salt and default password set
     * Must be used to initialize required fields
     * @return User
     */
    public static User newUser() {
        User u = new User();
        u.setId(null);
        u.setSalt(generateRandomSalt());
        u.setEnabled(true);
        u.resetPassword();                  // Set to default password and force it to be changed on login
        u.setLastBadgeNumberCreated(1213);  // Start at an arbitrary number instead of 0
        u.setSessionNumber(1);
        return u;
    }

    /**
     * Generate a new User record with the given first and last names and the default password.
     * Username will be first initial + last name, lower case.
     * @param firstName First Name
     * @param lastName Last Name
     * @return User
     */
    public static User newUser(String firstName, String lastName) {
        User u = newUser();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setUsername(generateUserName(firstName, lastName));
        return u;
    }

    private static String generateRandomSalt() {
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    /**
     * Remove anything that isn't a letter and combine to first initial + last name. Doesn't do
     * uniqueness checking with existing users
     * @param firstName First Name
     * @param lastName Last Name
     * @return
     */
    protected static String generateUserName(String firstName, String lastName) {
        String username;
        if (firstName == null) { firstName = ""; }
        if (lastName == null) { lastName = ""; }

        firstName = firstName.replaceAll("[^\\p{L}]", "");
        if (firstName.length() > 0) {
            firstName = firstName.substring(0, 1);
        }

        lastName = lastName.replaceAll("[^\\p{L}]", "");

        username = String.format("%s%s", firstName, lastName);
        return username.toLowerCase();
    }

}
