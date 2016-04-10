package org.kumoricon.presenter.attendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.view.attendee.OverrideRequiredWindow;

import java.util.List;

public interface OverrideHandler {
    void overrideLogin(OverrideRequiredWindow window, String username, String password, List<Attendee> targets);
    void overrideCancel(OverrideRequiredWindow window);
}
