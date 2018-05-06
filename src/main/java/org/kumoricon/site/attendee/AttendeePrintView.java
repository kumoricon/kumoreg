package org.kumoricon.site.attendee;

import com.vaadin.ui.Window;
import org.kumoricon.model.user.User;

public interface AttendeePrintView {
    void notify(String message);
    void notifyError(String message);
    String getCurrentUsername();
    String getCurrentClientIPAddress();

    Boolean currentUserHasRight(String pre_reg_check_in);

    void showWindow(Window window);

    User getCurrentUser();

    void refresh();
}
