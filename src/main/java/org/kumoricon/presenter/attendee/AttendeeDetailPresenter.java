package org.kumoricon.presenter.attendee;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.attendee.AttendeeDetailForm;
import org.kumoricon.view.attendee.AttendeeDetailView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class AttendeeDetailPresenter implements PrintBadgePresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private AttendeeDetailView view;
    private PrintBadgeWindow printBadgeWindow;

    public AttendeeDetailPresenter() {
    }

    public void showAttendee(int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            AttendeeDetailForm form = (AttendeeDetailForm) view.getDetailForm();
            form.setAvailableBadges(badgeRepository.findAll());
            form.show(attendee);
        } else {
            view.notify("Error: attendee " + id + " not found.");
        }
    }

    public void saveAttendee() {
        Attendee attendee = view.getAttendee();
        attendee = attendeeRepository.save(attendee);
        view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public void saveAttendeeAndReprintBadge() {
        Attendee attendee = view.getAttendee();
        attendee = attendeeRepository.save(attendee);

        // Todo: prompt for override if current user doesn't have rights to reprint badge
        if (view.currentUserHasRight("reprint_badge")) {
            List<Attendee> attendeeList = new ArrayList<>();
            attendeeList.add(attendee);
            showAttendeeBadgeWindow(attendeeList);
            // print badge
        } else {
            view.notifyError("Access denied");
        }

    }

    @Override
    public void showAttendeeBadgeWindow(List<Attendee> attendeeList) {
        if (attendeeList != null) {
            printBadgeWindow = new PrintBadgeWindow(this, attendeeList);
            view.showWindow(printBadgeWindow);
        }
    }

    @Override
    public void badgePrintSuccess() {
        if (printBadgeWindow != null) {
            printBadgeWindow.close();
        }
        Attendee attendee = view.getAttendee();
        view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    @Override
    public void reprintBadges(List<Attendee> attendeeList) {
        if (attendeeList.size() > 0) {
            view.notify("Reprinting badges");
        } else {
            view.notify("No attendees selected");
        }
    }

    public void cancelAttendee() {
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public AttendeeDetailView getView() { return view; }
    public void setView(AttendeeDetailView view) { this.view = view; }
}