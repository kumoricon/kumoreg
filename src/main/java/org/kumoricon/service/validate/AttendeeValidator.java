package org.kumoricon.service.validate;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.Attendee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Validates an attendee object. Can be configured with options set in the configuration file.
 */
@Service
public class AttendeeValidator extends Validator {
    @Value("${kumoreg.validation.attendee.requirePhoneOrEmail:true}")
    protected Boolean requirePhoneAndEmail;

    public Boolean validate(Attendee a) throws ValueException {
        if (isNullOrEmpty(a.getFirstName())) { throw new ValueException("First name is required"); }
        if (isNullOrEmpty(a.getLastName())) { throw new ValueException("Last name is required"); }
        if (requirePhoneAndEmail && isNullOrEmpty(a.getPhoneNumber()) && isNullOrEmpty(a.getEmail())) {
            throw new ValueException("Phone Number or Email is required");
        }
        if (a.getPaidAmount() == null || a.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueException("Paid amount may not be negative");
        }
        if (a.getBirthDate() == null || a.getBirthDate().isBefore(LocalDate.of(1900, 1, 1))) {
            throw new ValueException("Birthdate may not be before 1/1/1900");
        }
        if (a.getBirthDate() == null || a.getBirthDate().isAfter(LocalDate.now())) {
            throw new ValueException("Birthdate may not be after today");
        }
        if (isNullOrEmpty(a.getEmergencyContactFullName())) {
            throw new ValueException("Emergency Contact Name required");
        }
        if (isNullOrEmpty(a.getEmergencyContactPhone())) {
            throw new ValueException("Emergency Contact Phone Number required");
        }
        if (a.getBadge() == null) {
            throw new ValueException("Badge may not be empty");
        }
        if (a.isMinor()) {
            if (isNullOrEmpty(a.getParentFullName())) {
                throw new ValueException("Minors must have parent name entered");
            }
            if (isNullOrEmpty(a.getParentPhone())) {
                throw new ValueException("Minors must have parent phone number entered");
            }
        }
        return true;
    }
}

