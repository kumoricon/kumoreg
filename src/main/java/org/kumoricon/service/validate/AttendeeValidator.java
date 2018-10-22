package org.kumoricon.service.validate;

import org.kumoricon.model.attendee.Attendee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Validates an attendee object. Can be configured with options set in the configuration file.
 */
@Service
public class AttendeeValidator extends Validator {
    @Value("${kumoreg.validation.attendee.requirePhoneAndEmail:true}")
    protected Boolean requirePhoneAndEmail;

    /**
     * Validate the given attendee object. Throws ValidationException on failure.
     * @param a Attendee
     * @throws ValidationException Exception message if any field fails validation
     */
    public void validate(Attendee a) throws ValidationException {
        if (isNullOrEmpty(a.getFirstName())) { throw new ValidationException("First name is required"); }
        if (isNullOrEmpty(a.getLastName())) { throw new ValidationException("Last name is required"); }
        if (requirePhoneAndEmail && isNullOrEmpty(a.getPhoneNumber())) {
            throw new ValidationException("Phone Number is required");
        }
        if (requirePhoneAndEmail && isNullOrEmpty(a.getEmail())) {
            throw new ValidationException("Email is required");
        }

        if (a.getPaidAmount() == null || a.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Paid amount may not be negative");
        }
        if (a.getBirthDate() == null || a.getBirthDate().isBefore(LocalDate.of(1900, 1, 1))) {
            throw new ValidationException("Birth date may not be before 1/1/1900");
        }
        if (a.getBirthDate() == null || a.getBirthDate().isAfter(LocalDate.now(ZoneId.of("America/Los_Angeles")))) {
            throw new ValidationException("Birth date may not be after today");
        }
        if (isNullOrEmpty(a.getEmergencyContactFullName())) {
            throw new ValidationException("Emergency Contact Name required");
        }
        if (isNullOrEmpty(a.getEmergencyContactPhone())) {
            throw new ValidationException("Emergency Contact Phone Number required");
        }
        if (a.getBadge() == null) {
            throw new ValidationException("Badge may not be empty");
        }
        if (a.isMinor()) {
            if (isNullOrEmpty(a.getParentFullName())) {
                throw new ValidationException("Minors must have parent name entered");
            }
            if (isNullOrEmpty(a.getParentPhone())) {
                throw new ValidationException("Minors must have parent phone number entered");
            }
            if (a.getParentFormReceived() == null || !a.getParentFormReceived()) {
                throw new ValidationException("Parental Consent form has not been received");
            }
        }
    }
}

