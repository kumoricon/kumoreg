package org.kumoricon.site.attendee;

import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.model.attendee.Attendee;

import java.util.List;

public interface PrintBadgeHandler {
    void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList, boolean forcePrintAll);
    void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees);
    void badgePrintSuccess(PrintBadgeView view, List<Attendee> attendees);
    void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList);
    void reprintBadges(BaseView view, List<Attendee> attendees);
    BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees);
    BadgePrintFormatter getBadgeFormatter(PrintBadgeView printBadgeView, List<Attendee> attendees);
}
