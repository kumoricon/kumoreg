package org.kumoricon.model.role;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RoleTest {
    Role role;
    Right doStuff = new Right("do_stuff", "Can do stuff");
    Right doThings = new Right("do_things", "Can do things");

    @Before
    public void setUp() {
        role = new Role("Tester");
        role.addRight(doStuff);
        role.addRight(doThings);
    }

    @Test
    public void addRight() {
        Right testRight = new Right("test");
        role.addRight(testRight);
        assertTrue("Right was added", role.getRights().contains(testRight));
        assertEquals("Correct number of rights", 3, role.getRights().size());
    }

    @Test
    public void addRights() {
        Right r1 = new Right("right1");
        Right r2 = new Right("right2");
        Set<Right> rights = new HashSet<>(2);
        rights.add(r1);
        rights.add(r2);

        role.addRights(rights);
        assertTrue("Right was added", role.getRights().contains(r1));
        assertTrue("Right was added", role.getRights().contains(r2));
        assertEquals("Correct number of rights", 4, role.getRights().size());
    }

    @Test
    public void removeRight() {
        // Remove the right with the given name; tests finding a right with the given name
        role.removeRight("do_stuff");
        assertFalse("Right was removed", role.getRights().contains(doStuff));
        assertTrue(role.getRights().contains(doThings));
        assertEquals("Correct number of rights after deletion", 1, role.getRights().size());
    }

    @Test
    public void removeRightNotCaseSensitive() {
        // Remove the right with the given name; tests finding a right with the given name
        role.removeRight("Do_Stuff");
        assertFalse("Right was removed", role.getRights().contains(doStuff));
        assertTrue(role.getRights().contains(doThings));
        assertEquals("Correct number of rights after deletion", 1, role.getRights().size());
    }

    @Test
    public void hasRight() {
        assertTrue(role.hasRight("do_stuff"));
        assertTrue(role.hasRight("do_things"));
        assertFalse(role.hasRight("not_this"));
    }

    @Test
    public void hasRightNotCaseSensitive() {
        assertTrue(role.hasRight("Do_Stuff"));
        assertTrue(role.hasRight("Do_Things"));
        assertFalse(role.hasRight("Not_This"));
    }

    @Test
    public void hasRightReturnsTrueForSuperAdmin() {
        role.addRight(new Right("super_admin", "Can do it all"));
        assertTrue(role.hasRight("do_stuff"));
        assertTrue(role.hasRight("do_things"));
        assertTrue(role.hasRight("not_this"));
    }

}