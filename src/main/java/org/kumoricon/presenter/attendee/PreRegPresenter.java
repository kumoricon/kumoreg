package org.kumoricon.presenter.attendee;

import org.kumoricon.attendee.BadgePrintService;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.BaseView;
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
public class PreRegPresenter implements PrintBadgeHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private BadgePrintService badgePrintService;

    private PreRegView view;
    private BadgeWarningWindow warningWindow;
    private static final Logger log = LoggerFactory.getLogger(PreRegPresenter.class);



    public PreRegPresenter() {
    }

    public void searchChanged(String searchString) {
        if (searchString != null) {
            view.navigateTo(view.VIEW_NAME + "/search/" + searchString.trim());
        }
    }

    public void searchFor(String searchString) {
        view.getAttendeeBeanList().removeAllItems();
        if (searchString != null && !searchString.trim().isEmpty()) {
            searchString = searchString.trim();
            List<Attendee> attendees;
            if (searchString.length() == 32) {
                // OrderId is 32 characters long - anything else, search by last name
                attendees = attendeeRepository.findByOrderNumber(searchString);
            } else {
                attendees = attendeeRepository.findNotCheckedInByLastName(searchString);
            }
            view.afterSuccessfulFetch(attendees);
            if (attendees.isEmpty()) {
                view.notify("No matching attendees found");
            }
        }
    }

    public void selectAttendee(Attendee attendee) {
        if (attendee.getBadge().getWarningMessage() == null) {
            continueCheckIn(attendee);
        } else {
            String requiredRight = attendee.getBadge().getRequiredRight();
            String warningMessage = attendee.getBadge().getWarningMessage();
            if (requiredRight == null || view.currentUserHasRight(requiredRight)) {
                continueCheckIn(attendee);
            } else {
                warningWindow = new BadgeWarningWindow(this, attendee);
                view.showWindow(warningWindow);
            }
        }
    }

    public void showAttendee(PreRegView view, int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        view.showAttendee(attendee, badgeRepository.findAll());
    }

    public void checkInAttendee(PreRegCheckInWindow window, Attendee attendee) {
        if (attendee != null) {
            List<Attendee> attendeeList = new ArrayList<>();
            attendeeList.add(attendee);
            if (validateBeforeCheckIn(window, attendee)) {
                window.close();
                showAttendeeBadgeWindow(window.getParentView(), attendeeList);
            }
        }
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        BaseView v = printBadgeWindow.getParentView();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s printing badges for: ", v.getCurrentUser()));
        for (Attendee attendee : attendeeList) {
            sb.append(attendee.getName());
            sb.append("; ");
        }
        sb.append(" (Reprint from window)");
        log.info(sb.toString());
        printBadgeWindow.getParentView().notify(
                badgePrintService.printBadgesForAttendees(attendeeList, v.getCurrentClientIPAddress()));
    }

    public Boolean validateBeforeCheckIn(PreRegCheckInWindow window, Attendee attendee) {
        if (attendee.isMinor()) {
            if (!window.parentalConsentFormReceived()) {
                window.getParentView().notify("Error: Parental consent form has not been received");
                return false;
            }
        }
        if (!window.informationVerified()) {
            window.getParentView().notify("Error: Information not verified");
            return false;
        }
        return true;
    }

    public void cancelAttendee(PreRegCheckInWindow window) {
        window.close();
    }


    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
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

    @Override
    public void badgePrintSuccess(PrintBadgeWindow window, List<Attendee> attendees) {
        for (Attendee attendee : attendees) {
            attendee.setCheckedIn(true);
        }
        attendeeRepository.save(attendees);
        Attendee attendee = attendees.get(0);
        window.close();
        window.getParentView().notify(String.format("%s %s is checked in", attendee.getFirstName(), attendee.getLastName()));
        window.getParentView().navigateTo(PreRegView.VIEW_NAME + "/search/" + attendee.getOrder().getOrderId());

    }

    public PreRegView getView() { return view; }
    public void setView(PreRegView view) { this.view = view; }

    public void continueCheckIn(Attendee attendee) {
        if (warningWindow != null) { warningWindow.close(); }
        view.navigateTo(PreRegView.VIEW_NAME + "/" + attendee.getId().toString());
    }

    public void abortCheckIn() {
        if (warningWindow != null) { warningWindow.close(); }
    }
}