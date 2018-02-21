package org.kumoricon.site.attendee.search;

import com.vaadin.ui.Window;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.*;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.AttendeeSearchService;
import org.kumoricon.service.validate.AttendeeValidator;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.*;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
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
public class AttendeeSearchPresenter extends BadgePrintingPresenter implements PrintBadgeHandler, OverrideHandler, OverrideEditHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private AttendeeSearchService attendeeSearchService;

    @Autowired
    private AttendeeHistoryRepository attendeeHistoryRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendeeValidator attendeeValidator;

    private static final Logger log = LoggerFactory.getLogger(AttendeeSearchPresenter.class);

    private AttendeeSearchView view;

    public AttendeeSearchPresenter() {
    }

    public void showAttendee(AttendeeDetailView bView, int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            log.info("{} displayed Attendee {}", bView.getCurrentUsername(), attendee);
            bView.showAttendee(attendee, badgeRepository.findAll());
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUsername(), id);
            bView.notify("Error: attendee " + id + " not found.");
        }
    }

    public void showAttendee(AttendeeSearchDetailView dView, int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            dView.showAttendee(attendee, badgeRepository.findAll());
            log.info("{} displayed Attendee {}", dView.getCurrentUsername(), attendee);
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUsername(), id);
            dView.notify("Error: attendee " + id + " not found.");
        }
    }
    public Attendee saveAttendee(AttendeeDetailView view, Attendee attendee) {
        try {
            attendeeValidator.validate(attendee);
            attendee = attendeeRepository.save(attendee);
            view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
            log.info("{} saved {}", view.getCurrentUsername(), attendee);
            return attendee;
        } catch (ValidationException e) {
            log.error("{} tried to save {} and got error {}",
                    view.getCurrentUser(), attendee, e.getMessage());
            view.notifyError(e.getMessage());
            return null;
        }
    }

    public void saveAttendeeAndReprintBadge(AttendeeDetailView view, Attendee attendee, User overrideUser) {
        try {
            if (view.currentUserHasRight("attendee_edit")) {
                attendeeValidator.validate(attendee);        // Only validate fields if the user actually has the ability to edit them
            }
            String historyMessage;
            if (overrideUser != null) {
                historyMessage = String.format("Badge reprinted with override by %s", overrideUser);
            } else {
                historyMessage = "Badge reprinted";
            }
            attendee.addHistoryEntry(view.getCurrentUser(), historyMessage);
            attendee = attendeeRepository.save(attendee);
            log.info("{} saved {}", view.getCurrentUsername(), attendee);
            view.refresh();
        } catch (ValidationException e) {
            view.notifyError(e.getMessage());
            log.error("{} tried to save {} and got error {}", view.getCurrentUsername(), attendee, e.getMessage());
            return;
        }

        List<Attendee> attendeeList = new ArrayList<>();
        attendeeList.add(attendee);
        // If no override user, check permissions on the current user

        if (overrideUser == null) {
            if (view.currentUserHasRight("reprint_badge")) {
                log.info("{} reprinting badge(s) for {}", view.getCurrentUsername(), attendee);
                showAttendeeBadgeWindow(view, attendeeList, false);
            } else {
                view.showOverrideRequiredWindow(this, attendeeList);
            }
        } else {
            if (overrideUser.hasRight("reprint_badge")) {
                log.info("{} reprinting badge(s) for {} with override from {}",
                        view.getCurrentUsername(), attendee, overrideUser);
                showAttendeeBadgeWindow(view, attendeeList, false);
            } else {
                view.notifyError("Override user does not have the required right");
                log.error("{} requested an override to reprint a badge for {} but {} did not have the reprint_badge right",
                        view.getCurrentUsername(), attendee, overrideUser);
                view.showOverrideRequiredWindow(this, attendeeList);
            }
        }
    }

    public void saveAttendeeAndPrePrintBadge(AttendeeDetailView view, Attendee attendee) {
        try {
            if (view.currentUserHasRight("attendee_edit")) {
                attendeeValidator.validate(attendee);        // Only validate fields if the user actually has the ability to edit them
            }
            attendee.addHistoryEntry(view.getCurrentUser(), "Pre-printed badge");
            attendee.setBadgePrePrinted(true);
            attendee = attendeeRepository.save(attendee);
            log.info("{} saved {}", view.getCurrentUsername(), attendee);
            view.refresh();
        } catch (ValidationException e) {
            view.notifyError(e.getMessage());
            log.error("{} tried to save {} and got error {}", view.getCurrentUsername(), attendee, e.getMessage());
            return;
        }

        List<Attendee> attendeeList = new ArrayList<>();
        attendeeList.add(attendee);

        if (view.currentUserHasRight("pre_print_badges")) {
                log.info("{} pre-printing badge(s) for {}", view.getCurrentUsername(), attendee);
                showAttendeeBadgeWindow(view, attendeeList, true);
        }
    }

    public AttendeeSearchView getView() { return view; }
    public void setView(AttendeeSearchView view) { this.view = view; }

    @Override
    public void overrideLogin(OverrideRequiredWindow window, String username, String password, List<Attendee> targets) {
        User overrideUser = userRepository.findOneByUsernameIgnoreCase(username);
        if (overrideUser != null && overrideUser.checkPassword(password) && overrideUser.hasRight("reprint_badge")) {
            log.info("{} got reprint badges override from {}", view.getCurrentUsername(), overrideUser);
//            saveAttendeeAndReprintBadge(window, targets.get(0), overrideUser);
            throw new RuntimeException("Not implemented"); //TODO
        } else {
            view.notify("Bad username or password");
        }
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList, boolean forcePrintAll) {
        if (attendeeList != null) {
            if (forcePrintAll) {
                printBadges((BaseView)view, attendeeList);
            } else {
                List<Attendee> attendeesToPrint = new ArrayList<>();
                for (Attendee attendee : attendeeList) {
                    if (!attendee.isBadgePrePrinted()) {
                        attendeesToPrint.add(attendee);
                    }
                }
                printBadges((BaseView)view, attendeesToPrint);
            }
            view.showPrintBadgeWindow(attendeeList);
        }
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        log.info("{} reported badge(s) printed successfully for {}",
                printBadgeWindow.getParentView().getCurrentUser(), attendees);
        if (printBadgeWindow != null) {
            if (attendees.size() > 0) {
                Attendee attendee = attendees.get(0);
                if (attendee.getOrder() != null) {
                    searchChanged(attendee.getOrder().getOrderId());
                } else {
                    printBadgeWindow.getParentView().refresh();
                }
            } else {
                printBadgeWindow.getParentView().refresh();
            }
            printBadgeWindow.close();
        }
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (attendeeList.size() > 0) {
            log.info("{} reprinting badges due to error for {}", view.getCurrentUsername(), attendeeList);
            printBadges(printBadgeWindow.getParentView(), attendeeList);
        } else {
            view.notify("No attendees selected");
        }
    }

    public void searchChanged(String searchString) {
        if (searchString != null) {
            view.navigateTo(AttendeeSearchView.VIEW_NAME + "/" + searchString.trim());
        }
    }

    private Boolean validateBeforeCheckIn(AttendeeDetailWindow window, Attendee attendee) {
        try {
            attendeeValidator.validate(attendee);
        } catch (ValidationException e) {
            view.notifyError(e.getMessage());
            return false;
        }
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

    public void checkInAttendee(AttendeeDetailWindow window, Attendee attendee) {
        log.info("{} checked in preregistered attendee {}", window.getParentView().getCurrentUser(), attendee);
        if (attendee != null) {
            if (validateBeforeCheckIn(window, attendee)) {
                attendee.setParentFormReceived(window.parentalConsentFormReceived());
                attendee.addHistoryEntry(window.getParentView().getCurrentUser(), "Attendee Checked In");
                attendee.setCheckedIn(true);
                attendeeRepository.save(attendee);
                List<Attendee> attendeeList = new ArrayList<>();
                attendeeList.add(attendee);
                window.close();
                showAttendeeBadgeWindow(window.getParentView(), attendeeList, false);
            }
        }
    }

    public void searchFor(String searchString) {
        if (searchString != null && !searchString.trim().isEmpty()) {
            searchString = searchString.trim();
            long start = System.currentTimeMillis();
            List<Attendee> attendees = attendeeSearchService.search(searchString);
            long finish = System.currentTimeMillis();
            log.info("{} searched Attendees for \"{}\" and got {} results in {} ms",
                    view.getCurrentUsername(), searchString, attendees.size(), finish-start);
            view.afterSuccessfulFetch(attendees);
            if (attendees.size() == 0) {
                view.notify("No matching attendees found");
            }
        }
    }

    @Override
    public void overrideEditLogin(OverrideRequiredForEditWindow window, String username, String password, AttendeeDetailWindow attendeeDetailWindow) {
        User overrideUser = userRepository.findOneByUsernameIgnoreCase(username);
        if (overrideUser != null && overrideUser.checkPassword(password) && overrideUser.hasRight("attendee_edit")) {
            log.info("{} got edit override from {} to edit {}",
                    view.getCurrentUsername(), overrideUser, attendeeDetailWindow.getAttendee());
            window.close();
            attendeeDetailWindow.enableEditing(overrideUser);
        } else {
            view.notify("Bad username or password");
        }
    }

    @Override
    public void overrideEditCancel(OverrideRequiredForEditWindow window) { window.close(); }


    public void addNote(AttendeeDetailWindow attendeeDetailWindow, String message) {
        // Handle adding a note. Make sure the note gets saveed to the database even if the view
        // is closed without saving the attendee. (For example, if the user has rights to add notes but not edit
        // attendees.
        Attendee attendee = attendeeDetailWindow.getAttendee();
        log.info("{} added note \"{}\" to {}",
                view.getCurrentUsername(),
                message.replaceAll("([\\r\\n])+", " "),
                attendee);
        AttendeeHistory ah = new AttendeeHistory(view.getCurrentUser(), attendee, message);
        attendeeHistoryRepository.save(ah);
        attendeeDetailWindow.showHistory(attendeeHistoryRepository.findByAttendee(attendee));
    }

    public void showAttendeeList(AttendeeSearchByBadgeView view, Integer badgeId) {
        if (badgeId != null) {
            Badge badge = badgeRepository.findOne(badgeId);
            if (badge == null) {
                log.error("{} viewed attendees for badge id {} but it was not found",
                        view.getCurrentUsername(), badgeId);
                view.notifyError("Badge id " + badgeId.toString() + " not found");
                view.navigateTo(AttendeeSearchByBadgeView.VIEW_NAME);
            } else {
                showAttendeeList(view, badge);
            }
        }

    }

    public void showAttendeeList(AttendeeSearchByBadgeView view, Badge badge) {
        log.info("{} viewed attendees with badge {}", view.getCurrentUsername(), badge);
        if (badge == null) {
            view.afterAttendeeFetch(new ArrayList<>());
        }
        List<Attendee> attendees = attendeeRepository.findByBadgeType(badge);
        view.afterAttendeeFetch(attendees);
    }


    public void showBadgeTypes(AttendeeSearchByBadgeView view) {
        view.afterBadgeTypeFetch(badgeRepository.findByVisibleTrue());
    }
}
