package org.kumoricon.model.order;

import org.hibernate.validator.constraints.Length;
import org.kumoricon.model.attendee.Attendee;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @Length(min = 32, max = 32)
    private String orderId;
    @Min(0)
    private BigDecimal totalAmount;
    @NotNull
    private Boolean paid;
    private PaymentType paymentType;
    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Attendee> attendeeList;
    private String notes;

    public enum PaymentType {
        CASH {
            public String toString() { return "Cash"; }
        }, CHECK {
            public String toString() { return "Check"; }
        }, MONEYORDER {
            public String toString() { return "Money Order"; }
        }, CREDIT {
            public String toString() { return "Credit Card"; }
        }
    }

    public Order() {
        this.totalAmount = BigDecimal.ZERO;
        this.paid = false;
        this.attendeeList = new HashSet<>();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Set<Attendee> getAttendeeList() { return attendeeList; }
    public void setAttendeeList(Set<Attendee> attendeeList) { this.attendeeList = attendeeList; }
    public void addAttendee(Attendee attendee) {
        this.attendeeList.add(attendee);
    }

    public void removeAttendee(Attendee attendee) {
        this.attendeeList.remove(attendee);
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

    public void paymentComplete() {
        paid = true;
        for (Attendee attendee : attendeeList) {
            attendee.setCheckedIn(true);
            attendee.setPaid(true);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Order))
            return false;
        if (this.getId() == null) {
            return this == other;
        } else {
            Order o = (Order) other;
            return this.getId().equals(o.getId());
        }
    }

    @Override
    public int hashCode() { return getOrderId().hashCode(); }

    public String toString() { return orderId; }
}
