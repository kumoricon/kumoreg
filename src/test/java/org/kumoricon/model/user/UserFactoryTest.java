package org.kumoricon.model.user;

import org.junit.Test;

import static junit.framework.TestCase.*;

public class UserFactoryTest {
    @Test
    public void newUser() throws Exception {
        User user = UserFactory.newUser();
        defaultFieldsSet(user);
    }

    @Test
    public void newUserWithName() throws Exception {
        User user = UserFactory.newUser("Test", "Guy");
        assertEquals("First name set", "Test", user.getFirstName());
        assertEquals("Last name set", "Guy", user.getLastName());
        assertEquals("Username set", "tguy", user.getUsername());
        defaultFieldsSet(user);
    }

    @Test
    public void generateUserName() throws Exception {
        assertEquals(UserFactory.generateUserName("Jim", "O'malley"), "jomalley");
    }

    @Test
    public void generateUserNameFromNull() throws Exception {
        assertEquals("generateUserName handles null", "", UserFactory.generateUserName(null, null));
    }

    /**
     * Make sure required fields are initialized for a new user
     * @param user User record
     * @throws Exception
     */
    private void defaultFieldsSet(User user) throws Exception {
        assertNull("ID should be null", user.getId());
        assertTrue("User enabled", user.getEnabled());
        assertTrue("Password flagged to be reset", user.getResetPassword());
        assertNotNull("Password not null", user.getPassword());
        assertNotNull("Salt set", user.getSalt());
        assertEquals("Session number set", 1, (int)user.getSessionNumber());
        assertNotNull("Last badge number set", user.getLastBadgeNumberCreated());
    }

}