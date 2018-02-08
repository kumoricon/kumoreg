package org.kumoricon.site.attendee.reg;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.user.User;

public interface PaymentView {
    void showOrder(Order order);
    User getCurrentUser();
    String getCurrentUsername();
    void notifyError(String message);
    void notify(String message);
    void close();
    Boolean currentUserHasRight(String right);
    String getCurrentClientIPAddress();
}
