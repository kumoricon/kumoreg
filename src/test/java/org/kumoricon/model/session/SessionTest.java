package org.kumoricon.model.session;

import org.junit.Test;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SessionTest {
    @Test
    public void setStartOnlyIfNull() {
        // Only allow setting the start time if it is null - it shouldn't change
        Session s = new Session();
        Instant now = Instant.now();
        s.setStart(now);
        s.setStart(Instant.now().plus(10, ChronoUnit.SECONDS));
        assertEquals(now, s.getStart());
    }

    @Test
    public void setEnd() {
        // Only allow setting the end time if it is null - it shouldn't change
        Session s = new Session();
        Instant now = Instant.now();
        s.setEnd(now);
        s.setEnd(Instant.now().plus(10, ChronoUnit.SECONDS));
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