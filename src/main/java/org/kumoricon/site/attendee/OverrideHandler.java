package org.kumoricon.site.attendee;

import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.kumoricon.model.attendee.Attendee;

import java.util.List;

public interface OverrideHandler {
    void overrideLogin(OverrideRequiredWindow window, String username, String password, List<Attendee> targets);
    void overrideCancel(OverrideRequiredWindow window);
}
