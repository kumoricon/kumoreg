package org.kumoricon.model.attendee;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.order.Order;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "attendees")
public class Attendee implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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
    private Boolean parentForm;                 // has parental consent form been received?
    private Boolean paid;                       // has attendee paid? True for $0 attendees (press/comped/etc)
    private BigDecimal paidAmount;              // Amount paid - not necessarily the same as the badge cost, but
                                                // usually should be
    @ManyToOne(cascade=CascadeType.MERGE)
    private Badge badge;                        // Badge type
    @ManyToOne(cascade=CascadeType.MERGE)
    private Order order;
    private Boolean checkedIn;                  // Has attendee checked in and received badge?
    private String notes;
    private boolean preRegistered;              // Did attendee register before con?


    public Attendee() {
        this.paidAmount = BigDecimal.ZERO;
        this.checkedIn = false;
        this.paid = false;
        this.preRegistered = false;
        this.parentIsEmergencyContact = false;
    }


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

    public Boolean getParentForm() { return parentForm; }
    public void setParentForm(Boolean parentForm) { this.parentForm = parentForm; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public Badge getBadge() { return badge; }
    public void setBadge(Badge badge) { this.badge = badge; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Boolean getCheckedIn() { return checkedIn; }
    public void setCheckedIn(Boolean checkedIn) { this.checkedIn = checkedIn; }
    public Boolean isCheckedIn() { return checkedIn; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void setPreRegistered(boolean preRegistered) { this.preRegistered = preRegistered; }
    public Boolean isPreRegistered() { return preRegistered; }

    public String toString() {
        return String.format("%s %s (Birthdate: %s)", firstName, lastName, birthDate);
    }

}
