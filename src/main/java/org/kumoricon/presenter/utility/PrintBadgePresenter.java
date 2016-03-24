package org.kumoricon.presenter.utility;

import org.kumoricon.attendee.BadgePrintService;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.util.BadgeGenerator;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.AttendeePrintView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.print.PrintException;
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
        // Because this is for test badges in this presenter, don't use any existing attendees - generate them
        attendeeList.clear();
        attendeeList.add(attendeeFactory.generateDemoAttendee());
        attendeeList.add(attendeeFactory.generateYouthAttendee());
        attendeeList.add(attendeeFactory.generateChildAttendee());
        // Todo: Fix this
//        printBadgeWindow = new PrintBadgeWindow(this, attendeeList);
        printBadges((BaseView)view, attendeeList);
        view.showPrintBadgeWindow(attendeeList);
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        if (printBadgeWindow != null) {
            printBadgeWindow.close();
        }
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (printBadgeWindow == null) { return; }
        BaseView view = printBadgeWindow.getParentView();
        if (attendeeList.size() > 0) {
            printBadges(view, attendeeList);
            view.notify("Reprinting badges");
        } else {
            view.notify("No attendees selected");
        }
    }

    private void printBadges(BaseView view, List<Attendee> attendeeList) {
        BadgeGenerator badgeGenerator = new BadgeGenerator(attendeeList);
        try {
            badgePrintService.printDocument(badgeGenerator.getStream(), "Test");
        } catch(PrintException e) {
            view.notifyError("Error printing. No printers found? More information in server logs");
            log.error(String.format("Error printing badge for %s: %s",
                    view.getCurrentUser().getUsername(), e.getMessage()), e);
        }
    }
}
