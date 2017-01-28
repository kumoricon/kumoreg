package org.kumoricon.site.attendee;

import org.kumoricon.model.order.Payment;
import org.kumoricon.site.attendee.window.PaymentWindow;

public interface PaymentHandler {
    void addPayment(PaymentWindow window, Payment payment);
    void deletePayment(PaymentWindow window, Payment payment);
}
