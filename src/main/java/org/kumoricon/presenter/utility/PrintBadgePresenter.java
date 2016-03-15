package org.kumoricon.presenter.utility;

import com.vaadin.ui.Window;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.kumoricon.view.utility.TestBadgeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class PrintBadgePresenter implements PrintBadgeHandler {

    private TestBadgeView view;

    @Autowired
    private AttendeeFactory attendeeFactory;

    private Window printBadgeWindow;

    public PrintBadgePresenter() {}

    public void setView(TestBadgeView view) {
        this.view = view;
    }

    @Override
    public void showAttendeeBadgeWindow(BaseView view, List<Attendee> attendeeList) {
        // Because this is for test badges in this presenter, don't use any existing attendees - generate them
        attendeeList.clear();
        attendeeList.add(attendeeFactory.generateDemoAttendee());
        attendeeList.add(attendeeFactory.generateYouthAttendee());
        attendeeList.add(attendeeFactory.generateChildAttendee());
        // Todo: Fix this
//        printBadgeWindow = new PrintBadgeWindow(this, attendeeList);
        view.showWindow(printBadgeWindow);
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        if (printBadgeWindow != null) {
            printBadgeWindow.close();
        }
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (attendeeList.size() > 0) {
            view.notify("Reprinting badges");
        } else {
            view.notify("No attendees selected");
        }
    }
}
