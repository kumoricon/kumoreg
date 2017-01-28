package org.kumoricon.model.order;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.kumoricon.model.Record;
import org.kumoricon.model.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment extends Record {
    @NotNull
    @Min(0)
    private BigDecimal amount;

    @ManyToOne
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    private User paymentTakenBy;

    @NotNull
    private PaymentType paymentType;
    private LocalDateTime paymentTakenAt;
    private String paymentLocation;
    private String authNumber;

    public enum PaymentType {
        CASH {
            public String toString() {
                return "Cash";
            }
        }, CHECK {
            public String toString() {
                return "Check/Money Order";
            }
        }, CREDIT {
            public String toString() {
                return "Credit Card";
            }
        }, PREREG {
            public String toString() {
                return "Pre Reg";
            }
        };

        public static PaymentType fromInteger(Integer typeId) {
            PaymentType[] paymentTypes = PaymentType.values();
            return paymentTypes[typeId];
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public User getPaymentTakenBy() {
        return paymentTakenBy;
    }
    public void setPaymentTakenBy(User paymentTakenBy) {
        this.paymentTakenBy = paymentTakenBy;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getPaymentTakenAt() {
        return paymentTakenAt;
    }
    public void setPaymentTakenAt(LocalDateTime paymentTakenAt) {
        this.paymentTakenAt = paymentTakenAt;
    }

    public String getPaymentLocation() {
        return paymentLocation;
    }
    public void setPaymentLocation(String paymentLocation) {
        this.paymentLocation = paymentLocation;
    }

    public String getAuthNumber() { return authNumber; }
    public void setAuthNumber(String authNumber) { this.authNumber = authNumber; }

    public String toString() {
        if (id != null) {
            return String.format("[Payment %s: %s]", id, getAmount());
        } else {
            return String.format("[Payment: %s]", getAmount());
        }
    }
}