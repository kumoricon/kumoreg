package org.kumoricon.presenter.attendee;

public interface OverrideHandler {
    void overrideLogin(String username, String password);
    void overrideCancel();
}
