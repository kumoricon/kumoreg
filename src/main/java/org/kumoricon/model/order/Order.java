package org.kumoricon.model.order;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.Length;
import org.kumoricon.model.Record;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a single order. It is assumed that either all attendees in an order have not paid,
 * or all attendees in an order have paid - no support for partial orders. This shouldn't come up
 * during regular usage, but could with imported data.
 */
@Entity
@Table(name = "orders")
public class Order extends Record {
    @Length(min = 32, max = 32)
    @NotNull
    private String orderId;
    @NotNull
    private Boolean paid;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Attendee> attendeeList;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    private Set<Payment> payments;

    private String notes;

    public Order() {
        this.paid = false;
        this.attendeeList = new HashSet<>();
        this.payments = new HashSet<>();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (Attendee a : getAttendees()) {
            total = total.add(a.getPaidAmount());
        }
        return total; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public BigDecimal getTotalPaid() {
        BigDecimal total = BigDecimal.ZERO;
        for (Payment p : payments) {
            total = total.add(p.getAmount());
        }
        return total;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Set<Attendee> getAttendeeList() { return attendeeList; }
    public void setAttendeeList(Set<Attendee> attendeeList) { this.attendeeList = attendeeList; }
    public void addAttendee(Attendee attendee) {
        this.attendeeList.add(attendee);
    }

    public void removeAttendee(Attendee attendee) {
        this.attendeeList.remove(attendee);
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void addPayment(Payment payment) {
        payment.setOrder(this);
        payments.add(payment);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        if (getTotalPaid().compareTo(getTotalAmount()) >= 0) {
            paid = true;
            for (Attendee attendee : attendeeList) {
                attendee.setCheckedIn(true);
                attendee.setPaid(true);
            }
        } else {
            paid = false;
        }
    }

    public List<Attendee> getAttendees() {
        List<Attendee> attendees = new ArrayList<>();
        attendees.addAll(attendeeList);
        return attendees;
    }

    public static String generateOrderId() {
        String symbols = "abcdefghijklmnopqrstuvwxyz01234567890";
        Random random = new Random();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            output.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return output.toString();
    }

    public void paymentComplete(User currentUser) {
        if (currentUser != null) {
            paid = true;
            for (Attendee attendee : attendeeList) {
                if (!attendee.getCheckedIn()) {
                    attendee.setCheckedIn(true);
                    attendee.setPaid(true);
                    attendee.addHistoryEntry(currentUser, "Attendee checked in");
                }
            }
        }
    }

    public String toString() {
        if (id != null) {
            return String.format("[Order %s: %s]", id, orderId);
        } else {
            return String.format("[Order: %s]", orderId);
        }
    }
}
