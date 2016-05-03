package org.kumoricon.site.attendee;

import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.model.attendee.Attendee;

import java.util.List;

public interface PrintBadgeHandler {
    void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList);
    void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees);
    void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList);
}
