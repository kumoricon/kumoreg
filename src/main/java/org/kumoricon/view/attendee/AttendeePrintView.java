package org.kumoricon.view.attendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.user.User;

import java.util.List;

public interface AttendeePrintView {
    void showPrintBadgeWindow(List<Attendee> attendeeList);
    void notify(String message);
    void notifyError(String message);
    User getCurrentUser();
    String getCurrentClientIPAddress();
}
