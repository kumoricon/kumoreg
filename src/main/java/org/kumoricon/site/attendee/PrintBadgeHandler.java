package org.kumoricon.site.attendee;

import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.site.attendee.reg.OrderPrintView;
import org.kumoricon.model.attendee.Attendee;

import java.util.List;

public interface PrintBadgeHandler {
    void badgePrintSuccess(OrderPrintView view, List<Attendee> attendees);
    void reprintBadges(OrderPrintView view, List<Attendee> attendees);
    BadgePrintFormatter getBadgeFormatter(PrintBadgeView printBadgeView, List<Attendee> attendees);
}
