package org.kumoricon.model.attendee;

import org.kumoricon.model.Record;
import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Table(name = "attendees")
public class Attendee extends Record {
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private Boolean nameIsLegalName;
    private String fanName;                   // Fan Name (optional)
    @Column(unique=true)
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
    private Boolean compedBadge;                // True if the badge has been comped -- IE, is free
    @ManyToOne
    private Badge badge;                        // Badge type
    @ManyToOne
    private Order order;
    private Boolean checkedIn;                  // Has attendee checked in and received badge?
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInTime;                    // Timestamp when checked in
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "attendee")
    @OrderBy("timestamp desc")
    private Set<AttendeeHistory> history;
    private boolean preRegistered;              // Did attendee register before con?
    private boolean badgePrePrinted;            // Is a preprinted badge ready for this attendee?
    @Column(unique = true)
    private String staffIDNumber;               // May be a string in future, is int in 2017
    @Lob
    private ArrayList<String> staffPositions;
    private String staffDepartment;
    private String staffDepartmentColor;        // HTML color code for the department. Ex: "#00FF00"
    private String staffImageFilename;


    public Attendee() {
        this.paidAmount = BigDecimal.ZERO;
        this.checkedIn = false;
        this.paid = false;
        this.preRegistered = false;
        this.compedBadge = false;
        this.parentIsEmergencyContact = false;
        this.history = new HashSet<>();
        this.badgePrePrinted = false;
        this.nameIsLegalName = true;
    }


    /**
     * Returns true if attendee is < 18 years old or birthdate isn't set
     * @return minor status
     */
    public boolean isMinor() {
        // If birthdate isn't set for some reason, treat them as a minor.
        return birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(18));
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getName() { return firstName + " " + lastName; }

    public String getFanName() { return fanName; }
    public void setFanName(String fanName) { this.fanName = fanName; }

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

    public Boolean getNameIsLegalName() {
        return nameIsLegalName;
    }

    public void setNameIsLegalName(Boolean nameIsLegalName) {
        this.nameIsLegalName = nameIsLegalName;
    }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Long getAge() {
        if (birthDate == null) { return 0L; }
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        return ChronoUnit.YEARS.between(birthDate, now);
    }

    public Long getAge(LocalDate date) {
        if (birthDate == null) { return 0L; }
        return ChronoUnit.YEARS.between(birthDate, date);
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

    public Boolean getCompedBadge() { return compedBadge; }
    public void setCompedBadge(Boolean compedBadge) { this.compedBadge = compedBadge; }

    public Badge getBadge() { return badge; }
    public void setBadge(Badge badge) { this.badge = badge; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getLegalFirstName() { return legalFirstName; }
    public void setLegalFirstName(String legalFirstName) { this.legalFirstName = legalFirstName; }

    public String getLegalLastName() { return legalLastName; }
    public void setLegalLastName(String legalLastName) { this.legalLastName = legalLastName; }

    public boolean isBadgePrePrinted() {
        return badgePrePrinted;
    }

    public void setBadgePrePrinted(boolean badgePrePrinted) {
        this.badgePrePrinted = badgePrePrinted;
    }

    public Set<AttendeeHistory> getHistory() { return history; }

    public void setHistory(Set<AttendeeHistory> history) { this.history = history; }

    public void addHistoryEntry(User user, String message) {
        if (message != null && !message.trim().equals("")) {
            if (history == null) { history = new HashSet<>(); }
            history.add(new AttendeeHistory(user, this, message.trim()));
        }
    }

    public Boolean getCheckedIn() { return checkedIn; }
    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
        if (checkedIn) {
            if (checkInTime == null) {
                checkInTime = new Date();
            }
        } else {
            checkInTime = null;
        }
    }

    public Date getCheckInTime() {
        if (checkInTime == null) return null;
        return new Date(checkInTime.getTime());
    }

    public void setPreRegistered(boolean preRegistered) { this.preRegistered = preRegistered; }
    public Boolean isPreRegistered() { return preRegistered; }

    @Override
    public String toString() {
        if (id != null) {
            return String.format("[Attendee %s: %s %s]", id, firstName, lastName);
        } else {
            return String.format("[Attendee: %s %s]", firstName, lastName);
        }
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

    public String getStaffIDNumber() { return staffIDNumber; }
    public void setStaffIDNumber(String staffIDNumber) { this.staffIDNumber = staffIDNumber; }

    public ArrayList<String> getStaffPositions() {
        return staffPositions;
    }

    public void setStaffPositions(List<String> staffPositions) {
        // Must be an ArrayList internally for Hibernate to be able to serialize it
        this.staffPositions = new ArrayList<>(staffPositions);
    }

    public String getStaffDepartmentColor() {
        return staffDepartmentColor;
    }

    public void setStaffDepartmentColor(String staffDepartmentColor) {
        this.staffDepartmentColor = staffDepartmentColor;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    public String getStaffImageFilename() {
        return staffImageFilename;
    }

    public void setStaffImageFilename(String staffImageFilename) {
        this.staffImageFilename = staffImageFilename;
    }

    public boolean fieldsSameAs(Attendee attendee) {
        if (attendee == null) return false;

        if (preRegistered != attendee.preRegistered) return false;
        if (badgePrePrinted != attendee.badgePrePrinted) return false;
        if (firstName != null ? !firstName.equals(attendee.firstName) : attendee.firstName != null) return false;
        if (lastName != null ? !lastName.equals(attendee.lastName) : attendee.lastName != null) return false;
        if (legalFirstName != null ? !legalFirstName.equals(attendee.legalFirstName) : attendee.legalFirstName != null)
            return false;
        if (legalLastName != null ? !legalLastName.equals(attendee.legalLastName) : attendee.legalLastName != null)
            return false;
        if (nameIsLegalName != null ? !nameIsLegalName.equals(attendee.nameIsLegalName) : attendee.nameIsLegalName != null)
            return false;
        if (fanName != null ? !fanName.equals(attendee.fanName) : attendee.fanName != null) return false;
        if (badgeNumber != null ? !badgeNumber.equals(attendee.badgeNumber) : attendee.badgeNumber != null)
            return false;
        if (zip != null ? !zip.equals(attendee.zip) : attendee.zip != null) return false;
        if (country != null ? !country.equals(attendee.country) : attendee.country != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(attendee.phoneNumber) : attendee.phoneNumber != null)
            return false;
        if (email != null ? !email.equals(attendee.email) : attendee.email != null) return false;
        if (birthDate != null ? !birthDate.equals(attendee.birthDate) : attendee.birthDate != null) return false;
        if (emergencyContactFullName != null ? !emergencyContactFullName.equals(attendee.emergencyContactFullName) : attendee.emergencyContactFullName != null)
            return false;
        if (emergencyContactPhone != null ? !emergencyContactPhone.equals(attendee.emergencyContactPhone) : attendee.emergencyContactPhone != null)
            return false;
        if (parentIsEmergencyContact != null ? !parentIsEmergencyContact.equals(attendee.parentIsEmergencyContact) : attendee.parentIsEmergencyContact != null)
            return false;
        if (parentFullName != null ? !parentFullName.equals(attendee.parentFullName) : attendee.parentFullName != null)
            return false;
        if (parentPhone != null ? !parentPhone.equals(attendee.parentPhone) : attendee.parentPhone != null)
            return false;
        if (parentFormReceived != null ? !parentFormReceived.equals(attendee.parentFormReceived) : attendee.parentFormReceived != null)
            return false;
        if (paid != null ? !paid.equals(attendee.paid) : attendee.paid != null) return false;
        if (paidAmount != null ? !paidAmount.equals(attendee.paidAmount) : attendee.paidAmount != null) return false;
        if (compedBadge != null ? !compedBadge.equals(attendee.compedBadge) : attendee.compedBadge != null)
            return false;
        if (badge != null ? !badge.equals(attendee.badge) : attendee.badge != null) return false;
        if (order != null ? !order.equals(attendee.order) : attendee.order != null) return false;
        if (checkedIn != null ? !checkedIn.equals(attendee.checkedIn) : attendee.checkedIn != null) return false;
        if (checkInTime != null ? !checkInTime.equals(attendee.checkInTime) : attendee.checkInTime != null)
            return false;
        if (history != null ? !history.equals(attendee.history) : attendee.history != null) return false;
        if (staffIDNumber != null ? !staffIDNumber.equals(attendee.staffIDNumber) : attendee.staffIDNumber != null)
            return false;
        if (staffPositions != null ? !staffPositions.equals(attendee.staffPositions) : attendee.staffPositions != null)
            return false;
        if (staffDepartment != null ? !staffDepartment.equals(attendee.staffDepartment) : attendee.staffDepartment != null)
            return false;
        if (staffDepartmentColor != null ? !staffDepartmentColor.equals(attendee.staffDepartmentColor) : attendee.staffDepartmentColor != null)
            return false;
        return staffImageFilename != null ? staffImageFilename.equals(attendee.staffImageFilename) : attendee.staffImageFilename == null;
    }
}
