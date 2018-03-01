package org.kumoricon.model.session;

import org.junit.Test;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SessionTest {
    @Test
    public void setStartOnlyIfNull() {
        // Only allow setting the start time if it is null - it shouldn't change
        Session s = new Session();
        LocalDateTime now = LocalDateTime.now();
        s.setStart(now);
        s.setStart(LocalDateTime.of(2015, 8, 1, 11, 11, 11));
        assertEquals(now, s.getStart());
    }

    @Test
    public void setEnd() {
        // Only allow setting the end time if it is null - it shouldn't change
        Session s = new Session();
        LocalDateTime now = LocalDateTime.now();
        s.setEnd(now);
        s.setEnd(LocalDateTime.of(2015, 8, 1, 11, 11, 11));
        assertEquals(now, s.getEnd());
    }

    @Test
    public void setUser() {
        // Only allow setting the user if it is null - it shouldn't change
        Session s = new Session();
        User bob = UserFactory.newUser("Bob", "Smith");
        User joe = UserFactory.newUser("Joe", "Jones");
        s.setUser(bob);
        s.setUser(joe);
        assertEquals(bob, s.getUser());
    }

    @Test
    public void setOpenTrue() {
        Session s = new Session();
        s.setOpen(true);
        assertTrue(s.isOpen());
    }

    @Test
    public void setOpenFalse() {
        Session s = new Session();
        s.setOpen(false);
        assertFalse(s.isOpen());
    }


}