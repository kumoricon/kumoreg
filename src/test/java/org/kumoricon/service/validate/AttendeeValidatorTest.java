package org.kumoricon.service.validate;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.junit.Before;
import org.junit.Test;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class AttendeeValidatorTest {

    private AttendeeValidator validator = new AttendeeValidator();
    private Attendee attendee;

    @Before
    public void setUp() throws Exception {
        attendee = demoAttendee();
        validator.requirePhoneAndEmail = true;
    }

    @Test
    public void validateAllFieldsPresent() throws Exception {
        validator.validate(attendee);
    }


    @Test(expected = ValidationException.class)
    public void firstNameMissing() throws Exception {
        attendee.setFirstName(null);
        validator.validate(attendee);
    }


    @Test(expected = ValidationException.class)
    public void lastNameMissing() throws Exception {
        attendee.setLastName(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void badgeMissing() throws Exception {
        attendee.setBadge(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void phoneAndEmailMissing() throws Exception {
        // By default, either phone OR email is required
        attendee.setPhoneNumber(null);
        attendee.setEmail(null);
        validator.validate(attendee);
    }

    @Test
    public void phoneAndEmailNotRequired() throws Exception {
        validator.requirePhoneAndEmail = false;
        attendee.setPhoneNumber(null);
        attendee.setEmail(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void phoneMissing() throws Exception {
        attendee.setPhoneNumber(null);
        validator.validate(attendee);
    }

    @Test
    public void emailMissing() throws Exception {
        attendee.setEmail(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void emergencyContactMissing() throws Exception {
        attendee.setEmergencyContactFullName(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void emergencyPhoneMissing() throws Exception {
        attendee.setEmergencyContactPhone(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void birthdateMissing() throws Exception {
        attendee.setBirthDate(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void birthdateBefore1900() throws Exception {
        attendee.setBirthDate(LocalDate.of(1899, 12, 31));
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void birthdateAfterToday() throws Exception {
        attendee.setBirthDate(LocalDate.now().plusDays(1L));
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void minorMissingParentContact() throws Exception {
        attendee.setParentFullName(null);
        attendee.setParentPhone("123-123-1234");
        attendee.setBirthDate(LocalDate.now().minusYears(6L));
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void minorMissingParentPhone() throws Exception {
        attendee.setParentFullName("Mom");
        attendee.setParentPhone(null);
        attendee.setBirthDate(LocalDate.now().minusYears(6L));
        validator.validate(attendee);
    }

    @Test
    public void minorHasParentContact() throws Exception {
        attendee.setParentFullName("Mom");
        attendee.setParentPhone("123-123-1234");
        attendee.setBirthDate(LocalDate.now().minusYears(6L));
        validator.validate(attendee);
    }


    @Test
    public void adultDoesntNeedParentContact() throws Exception {
        attendee.setParentFullName(null);
        validator.validate(attendee);
    }

    @Test
    public void adultDoesntNeedParentPhone() throws Exception {
        attendee.setParentPhone(null);
        validator.validate(attendee);
    }

    @Test(expected = ValidationException.class)
    public void paidNegativeAmount() throws Exception {
        attendee.setPaidAmount(new BigDecimal(-1));
        validator.validate(attendee);
    }



    private static Attendee demoAttendee() {
        Attendee attendee = new Attendee();
        attendee.setFirstName("Test");
        attendee.setLastName("Guy");
        attendee.setBadgeName("SuperFlyGuy");
        attendee.setBadgeNumber("TST12340");
        attendee.setCountry("United States of America");
        attendee.setZip("97201");
        attendee.setPhoneNumber("123-123-1234");
        attendee.setCheckedIn(true);
        attendee.setEmergencyContactFullName("Mom");
        attendee.setEmergencyContactPhone("321-321-4321");
        attendee.setBirthDate(LocalDate.now().minusYears(30L));
        attendee.setPaid(true);
        attendee.setPaidAmount(new BigDecimal(45));
        attendee.setBadge(new Badge("Test"));
        return attendee;
    }


}