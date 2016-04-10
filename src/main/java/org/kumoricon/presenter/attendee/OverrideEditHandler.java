package org.kumoricon.presenter.attendee;

import org.kumoricon.view.attendee.AttendeeDetailWindow;
import org.kumoricon.view.attendee.OverrideRequiredForEditWindow;

public interface OverrideEditHandler {
    void overrideEditLogin(OverrideRequiredForEditWindow window, String username, String password,
                           AttendeeDetailWindow attendeeDetailWindow);
    void overrideEditCancel(OverrideRequiredForEditWindow window);
}
