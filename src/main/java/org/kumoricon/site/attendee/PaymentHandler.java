package org.kumoricon.site.attendee;

import org.kumoricon.model.order.Payment;

public interface PaymentHandler {
    void addPayment(Payment payment);
    void deletePayment(Payment payment);
}
