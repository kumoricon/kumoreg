package org.kumoricon.model.attendee;

import org.junit.Before;
import org.junit.Test;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;

import java.time.LocalDate;
import java.util.Date;

import static junit.framework.TestCase.*;

public class AttendeeTest {
    Attendee attendee;
    @Before
    public void setUp() throws Exception {
        attendee = new Attendee();
    }

    @Test
    public void isMinorChild() throws Exception {
        LocalDate birthDate = LocalDate.now().minusYears(5);
        attendee.setBirthDate(birthDate);
        assertTrue("Child attendee is minor", attendee.isMinor());
    }

    @Test
    public void isMinorAdult() throws Exception {
        LocalDate birthDate = LocalDate.now().minusYears(18);
        attendee.setBirthDate(birthDate);
        assertFalse("Adult attendee is not minor", attendee.isMinor());
    }

    @Test
    public void isMinorBirthdateNotSet() throws Exception {
        assertTrue("Attendee is minor if birthdate is null", attendee.isMinor());
    }



    @Test
    public void getAge() throws Exception {
        LocalDate birthDate = LocalDate.now().minusYears(18);
        attendee.setBirthDate(birthDate);
        assertEquals("Age calculated correctly", (Long) 18L, attendee.getAge());
    }

    @Test
    public void getAgeMinor() throws Exception {
        LocalDate birthDate = LocalDate.now().minusYears(18).plusDays(1);
        attendee.setBirthDate(birthDate);
        assertEquals("Age calculated correctly", (Long) 17L, attendee.getAge());
    }

    @Test
    public void getAgeBirthdateNotSet() throws Exception {
        assertNull("Birthdate is empty", attendee.getBirthDate());
        assertEquals("Age is 0 when birthdate null", (Long) 0L, attendee.getAge());
    }


    @Test
    public void addHistoryEntry() throws Exception {
        User user = UserFactory.newUser("Test", "User");
        attendee.addHistoryEntry(user, "This is a test");
        assertEquals("History entry added", 1, attendee.getHistory().size());
        assertEquals("History user set", user, attendee.getHistory().get(0).getUser());
        assertEquals("History message set", "This is a test", attendee.getHistory().get(0).getMessage());
    }

    @Test
    public void addHistoryEntryWontAddEmptyMessages() throws Exception {
        User user = UserFactory.newUser("Test", "User");
        attendee.addHistoryEntry(user, null);
        attendee.addHistoryEntry(user, "");
        attendee.addHistoryEntry(user, " ");
        assertEquals("History entry not added", 0, attendee.getHistory().size());
    }

    @Test
    public void setCheckedInTrue() throws Exception {
        attendee.setCheckedIn(true);
        assertTrue("checkedIn flag set", attendee.getCheckedIn());
        assertEquals("check in date set", new Date(), attendee.getCheckInTime());
    }

    @Test
    public void setCheckedInFalse() throws Exception {
        attendee.setCheckedIn(false);
        assertFalse("checkedIn flag cleared", attendee.getCheckedIn());
        assertNull("check in date cleared", attendee.getCheckInTime());
    }

    @Test
    public void toStringTest() throws Exception {
        attendee.setId(1);
        attendee.setFirstName("Test");
        attendee.setLastName("Guy");
        assertEquals("toString format", "[Attendee 1: Test Guy]", attendee.toString());
    }

}