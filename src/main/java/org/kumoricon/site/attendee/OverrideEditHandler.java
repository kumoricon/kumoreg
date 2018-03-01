package org.kumoricon.site.attendee;

import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;

public interface OverrideEditHandler {
    void overrideEditLogin(OverrideRequiredForEditWindow window, String username, String password,
                           AttendeeDetailView attendeeDetailWindow);
    void overrideEditCancel(OverrideRequiredForEditWindow window);
}
