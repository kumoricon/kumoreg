package org.kumoricon.model.attendee;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "attendees")
public class Attendee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String badgeName;                   // Badge name (optional)
    private String badgeNumber;
    private String zip;
    private String country;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private String emergencyContactFullName;
    private String emergencyContactPhone;
    private Boolean parentIsEmergencyContact;   // is emergency contact same as parent?
    private String parentFullName;
    private String parentPhone;
    private Boolean parentFormReceived;         // has parental consent form been received?
    private Boolean paid;                       // has attendee paid? True for $0 attendees (press/comped/etc)
    private BigDecimal paidAmount;              // Amount paid - not necessarily the same as the badge cost, but
                                                // usually should be
    @ManyToOne
    private Badge badge;                        // Badge type
    @ManyToOne
    private Order order;
    private Boolean checkedIn;                  // Has attendee checked in and received badge?
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInTime;                    // Timestamp when checked in
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "attendee")
    @OrderBy("timestamp desc")
    private List<AttendeeHistory> history;
    private boolean preRegistered;              // Did attendee register before con?


    public Attendee() {
        this.paidAmount = BigDecimal.ZERO;
        this.checkedIn = false;
        this.paid = false;
        this.preRegistered = false;
        this.parentIsEmergencyContact = false;
        this.history = new ArrayList<>();
    }


    /**
     * Returns true if attendee is < 18 years old or birthdate isn't set
     * @return minor status
     */
    public boolean isMinor() {
        if (birthDate != null) {
            return birthDate.isAfter(LocalDate.now().minusYears(18));
        } else {
            return true;    // If birthdate isn't set for some reason, treat them as a minor.
        }
    }



    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getName() { return firstName + " " + lastName; }

    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }

    public String getBadgeNumber() { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Long getAge() {
        if (birthDate == null) { return 0L; }
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        return ChronoUnit.YEARS.between(birthDate, now);
    }

    public String getEmergencyContactFullName() { return emergencyContactFullName; }
    public void setEmergencyContactFullName(String emergencyContactFullName) {
        this.emergencyContactFullName = emergencyContactFullName;
    }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public Boolean getParentIsEmergencyContact() { return parentIsEmergencyContact; }
    public void setParentIsEmergencyContact(Boolean parentIsEmergencyContact) { this.parentIsEmergencyContact = parentIsEmergencyContact; }

    public String getParentFullName() { return parentFullName; }
    public void setParentFullName(String parentFullName) { this.parentFullName = parentFullName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public Boolean getParentFormReceived() { return parentFormReceived; }
    public void setParentFormReceived(Boolean parentFormReceived) { this.parentFormReceived = parentFormReceived; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public Badge getBadge() { return badge; }
    public void setBadge(Badge badge) { this.badge = badge; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<AttendeeHistory> getHistory() { return history; }

    public void setHistory(List<AttendeeHistory> history) { this.history = history; }

    public void addHistoryEntry(User user, String message) {
        if (user != null && message != null && !message.trim().equals("")) {
            if (history == null) { history = new ArrayList<>(); }
            history.add(new AttendeeHistory(user, this, message.trim()));
        }
    }

    public Boolean getCheckedIn() { return checkedIn; }
    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
        if (checkedIn) {
            checkInTime = new Date();
        } else {
            checkInTime = null;
        }
    }

    public Date getCheckInTime() { return checkInTime; }

    public void setPreRegistered(boolean preRegistered) { this.preRegistered = preRegistered; }
    public Boolean isPreRegistered() { return preRegistered; }

    public String toString() {
        return String.format("[Attendee %s: %s %s]", id, firstName, lastName);
    }

    public Boolean validate() throws ValueException {
        if (isNullOrEmpty(firstName)) { throw new ValueException("First name is required"); }
        if (isNullOrEmpty(lastName)) { throw new ValueException("Last name is required"); }
        if (isNullOrEmpty(phoneNumber) && isNullOrEmpty(email)) {
            throw new ValueException("Phone Number or Email is required");
        }
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueException("Paid amount may not be negative");
        }
        if (birthDate == null || birthDate.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new ValueException("Birthdate may not be before 1/1/1900");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new ValueException("Birthdate may not be after today");
        }
        if (isNullOrEmpty(emergencyContactFullName)) {
            throw new ValueException("Emergency Contact Name required");
        }
        if (isNullOrEmpty(emergencyContactPhone)) {
            throw new ValueException("Emergency Contact Phone Number required");
        }
        if (badge == null) {
            throw new ValueException("Badge may not be empty");
        }
        if (isMinor()) {
            if (isNullOrEmpty(parentFullName)) {
                throw new ValueException("Minors must have parent name entered");
            }
            if (isNullOrEmpty(parentPhone)) {
                throw new ValueException("Minors must have parent phone number entered");
            }
        }
        return true;
    }

    private Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof Attendee) ) return false;

        final Attendee otherAttendee = (Attendee) other;

        if (!id.equals(otherAttendee.getId())) return false;
        if (!badgeNumber.equals(otherAttendee.getBadgeNumber())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result =0;
        if (id != null) { result = id.hashCode(); }
        if (badgeNumber != null) { result = (result * 29) + badgeNumber.hashCode(); }
        return result;
    }

    public AgeRange getCurrentAgeRange() {
        if (birthDate != null && badge != null) {
            for (AgeRange ageRange : getBadge().getAgeRanges()) {
                if (ageRange.isValidForAge(getAge())) {
                    return ageRange;
                }
            }
        }
        return null;
    }
}
