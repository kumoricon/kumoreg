package org.kumoricon.model.attendee;

import org.junit.Before;
import org.junit.Test;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

public class AttendeeTest {
    private Attendee attendee;
    @Before
    public void setUp() {
        attendee = new Attendee();
    }

    @Test
    public void isMinorChild() {
        LocalDate birthDate = LocalDate.now().minusYears(5);
        attendee.setBirthDate(birthDate);
        assertTrue("Child attendee is minor", attendee.isMinor());
    }

    @Test
    public void isMinorAdult() {
        LocalDate birthDate = LocalDate.now().minusYears(18);
        attendee.setBirthDate(birthDate);
        assertFalse("Adult attendee is not minor", attendee.isMinor());
    }

    @Test
    public void isMinorBirthdateNotSet() {
        assertTrue("Attendee is minor if birthdate is null", attendee.isMinor());
    }



    @Test
    public void getAge() {
        LocalDate birthDate = LocalDate.now().minusYears(18);
        attendee.setBirthDate(birthDate);
        assertEquals("Age calculated correctly", (Long) 18L, attendee.getAge());
    }

    @Test
    public void getAgeMinor() {
        LocalDate birthDate = LocalDate.now().minusYears(18).plusDays(1);
        attendee.setBirthDate(birthDate);
        assertEquals("Age calculated correctly", (Long) 17L, attendee.getAge());
    }

    @Test
    public void getAgeBirthdateNotSet() {
        assertNull("Birthdate is empty", attendee.getBirthDate());
        assertEquals("Age is 0 when birthdate null", (Long) 0L, attendee.getAge());
    }


    @Test
    public void addHistoryEntry() {
        User user = UserFactory.newUser("Test", "User");
        attendee.addHistoryEntry(user, "This is a test");
        Set<AttendeeHistory> results = attendee.getHistory();
        assertEquals("History entry added", 1, results.size());
        for (AttendeeHistory ah : results) {
            assertEquals("History user set", user, ah.getUser());
            assertEquals("History message set", "This is a test", ah.getMessage());
        }
    }

    @Test
    public void addHistoryEntryWontAddEmptyMessages() {
        User user = UserFactory.newUser("Test", "User");
        attendee.addHistoryEntry(user, null);
        attendee.addHistoryEntry(user, "");
        attendee.addHistoryEntry(user, " ");
        assertEquals("History entry not added", 0, attendee.getHistory().size());
    }

    @Test
    public void setCheckedInTrue() {
        attendee.setCheckedIn(true);
        assertTrue("checkedIn flag set", attendee.getCheckedIn());
        assertEquals("check in date set", new Date(), attendee.getCheckInTime());
    }

    @Test
    public void setCheckedInFalse() {
        attendee.setCheckedIn(false);
        assertFalse("checkedIn flag cleared", attendee.getCheckedIn());
        assertNull("check in date cleared", attendee.getCheckInTime());
    }

    @Test
    public void toStringTest() {
        attendee.setId(1);
        attendee.setFirstName("Test");
        attendee.setLastName("Guy");
        assertEquals("toString format", "[Attendee 1: Test Guy]", attendee.toString());
    }

}