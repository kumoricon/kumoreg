package org.kumoricon.site.attendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.print.PrintException;
import java.util.List;

/**
 * Common methods for printing badges used across multiple presenters
 */
public class BadgePrintingPresenter {
    protected static final Logger log = LoggerFactory.getLogger(BadgePrintingPresenter.class);

    @Autowired
    protected BadgePrintService badgePrintService;

    /**
     * Print badges for the given attendees and display any error or result messages
     * @param view Current view
     * @param attendeeList Attendees to print badges for
     */
    protected void printBadges(BaseView view, List<Attendee> attendeeList) {
        try {
            String result = badgePrintService.printBadgesForAttendees(
                    attendeeList, view.getCurrentClientIPAddress());
            view.notify(result);
        } catch (PrintException e) {
            log.error("Error printing badges for {}", view.getCurrentUsername(), e);
            view.notifyError(e.getMessage());
        }
    }

    public BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees, printBadgeWindow.getParentView().getCurrentClientIPAddress());
    }
}
