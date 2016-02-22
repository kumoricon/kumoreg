package org.kumoricon.presenter.attendee;

import org.kumoricon.model.attendee.Attendee;

import java.util.List;

public interface PrintBadgePresenter {
    void showAttendeeBadgeWindow(List<Attendee> attendeeList);
    void badgePrintSuccess();
    void reprintBadges(List<Attendee> attendeeList);
}
