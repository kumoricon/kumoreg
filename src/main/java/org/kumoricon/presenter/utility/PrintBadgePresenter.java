package org.kumoricon.presenter.utility;

import org.kumoricon.attendee.BadgePrintService;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.AttendeePrintView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class PrintBadgePresenter implements PrintBadgeHandler {
    private static final Logger log = LoggerFactory.getLogger(BadgePrintService.class);

    @Autowired
    private AttendeeFactory attendeeFactory;

    @Autowired
    private BadgePrintService badgePrintService;

    public PrintBadgePresenter() {}

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        log.info("{} printing test badges", view.getCurrentUser());
        // Because this is for test badges in this presenter, don't use any existing attendees - generate them
        attendeeList.clear();
        attendeeList.add(attendeeFactory.generateDemoAttendee());
        attendeeList.add(attendeeFactory.generateYouthAttendee());
        attendeeList.add(attendeeFactory.generateChildAttendee());

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
