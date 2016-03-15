package org.kumoricon.presenter.attendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.PrintBadgeWindow;

import java.util.List;

public interface PrintBadgeHandler {
    void showAttendeeBadgeWindow(BaseView view, List<Attendee> attendeeList);
    void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees);
    void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList);
}
