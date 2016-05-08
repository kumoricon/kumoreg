package org.kumoricon.site.attendee;


import org.kumoricon.site.attendee.search.AttendeeDetailWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;

public interface OverrideEditHandler {
    void overrideEditLogin(OverrideRequiredForEditWindow window, String username, String password,
                           AttendeeDetailWindow attendeeDetailWindow);
    void overrideEditCancel(OverrideRequiredForEditWindow window);
}
