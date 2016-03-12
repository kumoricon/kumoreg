package org.kumoricon.presenter.attendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.attendee.AttendeeDetailForm;
import org.kumoricon.view.attendee.AttendeeDetailView;
import org.kumoricon.view.attendee.OverrideRequiredWindow;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class AttendeeDetailPresenter implements PrintBadgeHandler, OverrideHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    private AttendeeDetailView view;
    private PrintBadgeWindow printBadgeWindow;
    private OverrideRequiredWindow overrideRequiredWindow;

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
        view.navigateTo("");
    }

    public void saveAttendeeAndReprintBadge(User overrideUser) {
        // If overrideUser is null, get the current user from the view
        Attendee attendee = view.getAttendee();
        attendee = attendeeRepository.save(attendee);

        if (overrideUser == null) {
            if (view.currentUserHasRight("reprint_badge")) {
                List<Attendee> attendeeList = new ArrayList<>();
                attendeeList.add(attendee);
                showAttendeeBadgeWindow(attendeeList);
                // Todo: print badge
            } else {
                overrideRequiredWindow = new OverrideRequiredWindow(this, "reprint_badge");
                view.showWindow(overrideRequiredWindow);
            }
        } else {
            if (overrideUser.hasRight("reprint_badge")) {
                List<Attendee> attendeeList = new ArrayList<>();
                attendeeList.add(attendee);
                showAttendeeBadgeWindow(attendeeList);
                // Todo: print badge
            } else {
                view.notifyError("Override user does not have the required right");
                overrideRequiredWindow = new OverrideRequiredWindow(this, "reprint_badge");
                view.showWindow(overrideRequiredWindow);
            }
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
        view.navigateTo("");
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
        view.navigateTo("");
    }

    public AttendeeDetailView getView() { return view; }
    public void setView(AttendeeDetailView view) { this.view = view; }

    @Override
    public void overrideLogin(String username, String password) {
        User overrideUser = userRepository.findOneByUsernameIgnoreCase(username);
        if (overrideUser != null && overrideUser.checkPassword(password)) {
            overrideRequiredWindow.close();
            saveAttendeeAndReprintBadge(overrideUser);
        } else {
            view.notify("Bad username or password");
        }
    }

    @Override
    public void overrideCancel() {
        overrideRequiredWindow.close();
    }
}