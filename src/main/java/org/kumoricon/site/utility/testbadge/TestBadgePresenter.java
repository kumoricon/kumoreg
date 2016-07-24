package org.kumoricon.site.utility.testbadge;

import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.site.attendee.PrintBadgeHandler;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.site.BaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;


@Controller
@Scope("request")
public class TestBadgePresenter implements PrintBadgeHandler {
    private static final Logger log = LoggerFactory.getLogger(BadgePrintService.class);
    private final AttendeeFactory attendeeFactory;
    private final BadgePrintService badgePrintService;

    @Autowired
    public TestBadgePresenter(BadgePrintService badgePrintService, AttendeeFactory attendeeFactory) {
        this.badgePrintService = badgePrintService;
        this.attendeeFactory = attendeeFactory;
    }

    /**
     * Show the attendee badge window with the given number of automatically generated badges.
     * Generates badges Adult - Child - Youth
     * @param view View
     * @param numberOfBadges Number of badges to generate, minimum: 1, maximum: 3)
     */
    public void showAttendeeBadgeWindow(AttendeePrintView view, Integer numberOfBadges) {
        if (numberOfBadges == null) { numberOfBadges = 1; }
        log.info("{} generating {} test badge(s)", view.getCurrentUser(), numberOfBadges);
        List<Attendee> attendees = new ArrayList<>();

        if (numberOfBadges >= 1) { attendees.add(attendeeFactory.generateDemoAttendee()); }
        if (numberOfBadges >= 2) { attendees.add(attendeeFactory.generateChildAttendee()); }
        if (numberOfBadges >= 3) { attendees.add(attendeeFactory.generateYouthAttendee()); }

        showAttendeeBadgeWindow(view, attendees);
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        if (attendeeList == null) { return; }
        printBadges((BaseView) view, attendeeList);
        view.showPrintBadgeWindow(attendeeList);
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        if (printBadgeWindow != null) {
            log.info("{} reports test badges printed successfully for {}",
                    printBadgeWindow.getParentView().getCurrentUser(), attendees);
            printBadgeWindow.close();
        }
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (printBadgeWindow == null) {
            return;
        }
        BaseView view = printBadgeWindow.getParentView();
        if (attendeeList.size() > 0) {
            log.info("{} reprinting test badges for {}", view.getCurrentUser(), attendeeList);
            printBadges(view, attendeeList);
            view.notify("Reprinting badges");
        } else {
            view.notify("No attendees selected");
        }
    }

    private void printBadges(BaseView view, List<Attendee> attendeeList) {
        log.info("{} printing test badges for {}", view.getCurrentUser(), attendeeList);
        view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
    }
}
