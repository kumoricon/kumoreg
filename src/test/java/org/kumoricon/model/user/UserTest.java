package org.kumoricon.model.user;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.junit.Before;
import org.junit.Test;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserTest {
    private User user;

    @Before
    public void setUp() throws Exception {
        user = UserFactory.newUser("Alice", "Smith");
    }

    @Test
    public void setUsernameForcesLowercase() throws Exception {
        user.setUsername("SomeUser");
        assertEquals("someuser", user.getUsername());
    }

    @Test
    public void resetPassword() throws Exception {
        // Make sure to override the salt to a known value
        user.setSalt("ABCD");
        user.resetPassword();
        assertTrue(user.getResetPassword());
        assertEquals("774b7441f3d230f4d8761c45c890e766b8f4b5acc8b42fc680f2936e333fdfd7", user.getPassword());
    }

    @Test(expected = ValueException.class)
    public void setPasswordThrowsExceptionForNull() throws Exception {
        user.setPassword(null);
    }

    @Test(expected = ValueException.class)
    public void setPasswordThrowsExceptionForEmpty() throws Exception {
        user.setPassword(" ");
    }

    @Test
    public void getNextBadgeNumberIncrements() throws Exception {
        int startingNumber = user.getLastBadgeNumberCreated();
        int newBadgeNumber = user.getNextBadgeNumber();
        int lastBadgeNumber = user.getNextBadgeNumber();
        assertEquals(startingNumber+1, newBadgeNumber);
        assertEquals(startingNumber+2, lastBadgeNumber);
        assertEquals(startingNumber+2, (int)user.getLastBadgeNumberCreated());
    }

    @Test
    public void hasRightReturnsFalseWhenNoRoleExists() throws Exception {
        assertFalse(user.hasRight("Test"));
    }

    @Test
    public void hasRight() throws Exception {
        Role tester = new Role("Tester");
        tester.addRight(new Right("do_stuff"));
        user.setRole(tester);
        assertFalse(user.hasRight("Test"));
        assertTrue(user.hasRight("do_stuff"));
    }

    @Test
    public void checkPasswordReturnsFalseForNull() throws Exception {
        assertFalse(user.checkPassword(null));
    }

    @Test
    public void checkPassword() throws Exception {
        user.setPassword("testing123");
        assertTrue(user.checkPassword("testing123"));
    }

    @Test
    public void testEquals() throws Exception {
        User user1 = new User();
        User user2 = new User();
        user1.setUsername("bob");
        user2.setUsername("bob");

        assertEquals(user1, user2);
    }

    @Test
    public void testEqualsNullUsername() throws Exception {
        User user1 = new User();
        User user2 = new User();
        user1.setUsername(null);
        user2.setUsername(null);

        assertEquals(user1, user2);
    }

}