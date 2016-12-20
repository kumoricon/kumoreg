package org.kumoricon.site.attendee;

import com.vaadin.ui.Window;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.user.User;

import java.util.List;

public interface AttendeePrintView {
    void showPrintBadgeWindow(List<Attendee> attendeeList);
    void notify(String message);
    void notifyError(String message);
    String getCurrentUsername();
    String getCurrentClientIPAddress();

    Boolean currentUserHasRight(String pre_reg_check_in);

    void showWindow(Window window);

    User getCurrentUser();

    void refresh();
}
