package org.kumoricon.presenter.utility;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.AttendeePrintView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class PrintBadgePresenter implements PrintBadgeHandler {

    @Autowired
    private AttendeeFactory attendeeFactory;

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
            view.notify("Reprinting badges");
        } else {
            view.notify("No attendees selected");
        }
    }
}
