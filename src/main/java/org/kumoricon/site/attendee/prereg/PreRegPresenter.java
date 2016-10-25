package org.kumoricon.site.attendee.prereg;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.PrintBadgeHandler;
import org.kumoricon.site.attendee.reg.OrderView;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
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
    private static final Logger log = LoggerFactory.getLogger(PreRegPresenter.class);


    public PreRegPresenter() {
    }

    public void searchChanged(String searchString) {
        if (searchString != null) {
            view.navigateTo(view.VIEW_NAME + "/" + searchString.trim());
        }
    }

    public void searchFor(String searchString) {
        log.info("{} searched preregistered attendees for {}", view.getCurrentUsername(), searchString);
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
        log.info("{} viewed preregistered attendee {}", view.getCurrentUsername(), attendee);
        if (attendee.getBadge().getWarningMessage() == null) {
            continueCheckIn(attendee);
        } else {
            String requiredRight = attendee.getBadge().getRequiredRight();
            if (requiredRight == null || view.currentUserHasRight(requiredRight)) {
                continueCheckIn(attendee);
            } else {
                view.showBadgeWarningWindow(attendee);
            }
        }
    }

    public void showAttendee(PreRegView view, int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            if (attendee.getPaid()) {
                // Show prereg check in window that just asks to confirm information
                view.showAttendee(attendee, badgeRepository.findAll());
            } else {
                // Attendee hasn't paid, redirect to at-con registration flow with the order in progress
                log.info("{} checking in prereg attendee {} that had not paid, redirecting to order {}",
                        view.getCurrentUsername(), attendee, attendee.getOrder());
                view.navigateTo(OrderView.VIEW_NAME + "/" + attendee.getOrder().getId());
            }
        } else {
            log.error("{} tried to view preregistered attendee id {} but they were not found",
                    view.getCurrentUsername(), id);
        }
    }

    public void checkInAttendee(PreRegCheckInWindow window, Attendee attendee) {
        log.info("{} checked in preregistered attendee {}", window.getParentView().getCurrentUser(), attendee);
        if (attendee != null) {
            attendee.setParentFormReceived(window.parentalConsentFormReceived());
            attendee.addHistoryEntry(window.getParentView().getCurrentUser(), "Attendee Checked In");
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
        log.info("{} reprinting badges for preregistered attendee(s) {}", v.getCurrentUser(), attendeeList);
        printBadgeWindow.getParentView().notify(
                badgePrintService.printBadgesForAttendees(attendeeList, v.getCurrentClientIPAddress()));
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees, printBadgeWindow.getParentView().getCurrentClientIPAddress());
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
        view.navigateTo(view.VIEW_NAME + "/" + view.getSearchString());
    }


    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        log.info("{} printing badges for preregistered attendee(s) {}", view.getCurrentUsername(), attendeeList);
        view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
        view.showPrintBadgeWindow(attendeeList);
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow window, List<Attendee> attendees) {
        log.info("{} reports badges printed successfully for preregistered attendee(s) {}",
                view.getCurrentUsername(), attendees);
        for (Attendee attendee : attendees) {
            attendee.setCheckedIn(true);
        }
        attendeeRepository.save(attendees);
        Attendee attendee = attendees.get(0);
        window.close();
        window.getParentView().notify(String.format("%s %s is checked in", attendee.getFirstName(), attendee.getLastName()));
        window.getParentView().navigateTo(PreRegView.VIEW_NAME + "/" + attendee.getOrder().getOrderId());

    }

    public PreRegView getView() { return view; }
    public void setView(PreRegView view) { this.view = view; }

    public void continueCheckIn(Attendee attendee) {
        view.navigateTo(PreRegView.VIEW_NAME + "/" + view.getSearchString() + "/" + attendee.getId().toString());
    }

    /**
     * Enable the check in button for the given window if attendee info has been verified and the consent form
     * has been received for minors.
     * @param window Pre Reg check in window
     */
    public void checkIfAttendeeCanCheckIn(PreRegCheckInWindow window) {
        Attendee attendee = window.getAttendee();
        if (attendee.isMinor() && !window.parentalConsentFormReceived()) {
            window.setCheckInButtonEnabled(false);
        } else if (window.informationVerified()) {
            window.setCheckInButtonEnabled(true);
        } else {
            window.setCheckInButtonEnabled(false);
        }
    }
}