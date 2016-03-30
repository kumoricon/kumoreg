package org.kumoricon.presenter.attendee;

import org.kumoricon.attendee.BadgePrintService;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.attendee.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private BadgePrintService badgePrintService;

    private static final Logger log = LoggerFactory.getLogger(AttendeeDetailPresenter.class);

    private AttendeeDetailView view;
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
                log.info(view.getCurrentUser() + " reprinting badge(s)");
                showAttendeeBadgeWindow(view, attendeeList);
            } else {
                overrideRequiredWindow = new OverrideRequiredWindow(this, "reprint_badge");
                view.showWindow(overrideRequiredWindow);
            }
        } else {
            if (overrideUser.hasRight("reprint_badge")) {
                List<Attendee> attendeeList = new ArrayList<>();
                attendeeList.add(attendee);
                log.info(view.getCurrentUser() + " reprinting badge(s)");
                showAttendeeBadgeWindow(view, attendeeList);
            } else {
                view.notifyError("Override user does not have the required right");
                overrideRequiredWindow = new OverrideRequiredWindow(this, "reprint_badge");
                view.showWindow(overrideRequiredWindow);
            }
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

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        if (attendeeList != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s printing badges for: ", view.getCurrentUser()));
            for (Attendee attendee : attendeeList) {
                sb.append(attendee.getName());
                sb.append("; ");
            }
            log.info(sb.toString());
            view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
            view.showPrintBadgeWindow(attendeeList);
        }
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        if (printBadgeWindow != null) {
            printBadgeWindow.close();
        }
        Attendee attendee = view.getAttendee();
        view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
        view.navigateTo("");
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (attendeeList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s printing badges for: ", view.getCurrentUser()));
            for (Attendee attendee : attendeeList) {
                sb.append(attendee.getName());
                sb.append("; ");
            }
            sb.append(" (Reprint from window)");
            log.info(sb.toString());
            view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
        } else {
            view.notify("No attendees selected");
        }
    }
}