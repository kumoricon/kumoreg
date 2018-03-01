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
    public void setUp() {
        user = UserFactory.newUser("Alice", "Smith");
    }

    @Test
    public void setUsernameForcesLowercase() {
        user.setUsername("SomeUser");
        assertEquals("someuser", user.getUsername());
    }

    @Test
    public void resetPassword() {
        // Make sure to override the salt to a known value
        user.setSalt("ABCD");
        user.resetPassword();
        assertTrue(user.getResetPassword());
        assertEquals("774b7441f3d230f4d8761c45c890e766b8f4b5acc8b42fc680f2936e333fdfd7", user.getPassword());
    }

    @Test(expected = ValueException.class)
    public void setPasswordThrowsExceptionForNull() {
        user.setPassword(null);
    }

    @Test(expected = ValueException.class)
    public void setPasswordThrowsExceptionForEmpty() {
        user.setPassword(" ");
    }

    @Test
    public void getNextBadgeNumberIncrements() {
        int startingNumber = user.getLastBadgeNumberCreated();
        int newBadgeNumber = user.getNextBadgeNumber();
        int lastBadgeNumber = user.getNextBadgeNumber();
        assertEquals(startingNumber+1, newBadgeNumber);
        assertEquals(startingNumber+2, lastBadgeNumber);
        assertEquals(startingNumber+2, (int)user.getLastBadgeNumberCreated());
    }

    @Test
    public void hasRightReturnsFalseWhenNoRoleExists() {
        assertFalse(user.hasRight("Test"));
    }

    @Test
    public void hasRight() {
        Role tester = new Role("Tester");
        tester.addRight(new Right("do_stuff"));
        user.setRole(tester);
        assertFalse(user.hasRight("Test"));
        assertTrue(user.hasRight("do_stuff"));
    }

    @Test
    public void checkPasswordReturnsFalseForNull() {
        assertFalse(user.checkPassword(null));
    }

    @Test
    public void checkPassword() {
        user.setPassword("testing123");
        assertTrue(user.checkPassword("testing123"));
    }
}