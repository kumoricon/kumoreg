package org.kumoricon.site.attendee.search;

import org.kumoricon.model.attendee.*;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.validate.AttendeeValidator;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.*;
import org.kumoricon.site.attendee.search.byname.AttendeeSearchDetailView;
import org.kumoricon.site.attendee.search.byname.SearchByNameView;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class AttendeeSearchPresenter extends BadgePrintingPresenter implements OverrideHandler, OverrideEditHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendeeValidator attendeeValidator;

    private static final Logger log = LoggerFactory.getLogger(AttendeeSearchPresenter.class);

    private SearchByNameView view;

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

    public void showAttendee(CheckInView cView, Integer id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            cView.showAttendee(attendee);
            log.info("{} displayed Attendee {}", cView.getCurrentUsername(), attendee);
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUsername(), id);
            cView.notify("Error: attendee " + id + " not found.");
        }
    }

    public Attendee saveAttendee(AttendeeDetailView view, Attendee attendee) throws ValidationException {
            attendeeValidator.validate(attendee);
            attendee = attendeeRepository.save(attendee);
            view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
            log.info("{} saved {}", view.getCurrentUsername(), attendee);
            return attendee;
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
        } catch (ValidationException e) {
            view.notifyError(e.getMessage());
            log.error("{} tried to save {} and got error {}", view.getCurrentUsername(), attendee, e.getMessage());
        }
// Handle this by navigating to one of the RePrint views
//        List<Attendee> attendeeList = new ArrayList<>();
//        attendeeList.add(attendee);
//        // If no override user, check permissions on the current user
//
//        if (overrideUser == null) {
//            if (view.currentUserHasRight("reprint_badge")) {
//                log.info("{} reprinting badge(s) for {}", view.getCurrentUsername(), attendee);
//                showAttendeeBadgeWindow(view, attendeeList, false);
//            } else {
//                view.showOverrideRequiredWindow(this, attendeeList);
//            }
//        } else {
//            if (overrideUser.hasRight("reprint_badge")) {
//                log.info("{} reprinting badge(s) for {} with override from {}",
//                        view.getCurrentUsername(), attendee, overrideUser);
//                showAttendeeBadgeWindow(view, attendeeList, false);
//            } else {
//                view.notifyError("Override user does not have the required right");
//                log.error("{} requested an override to reprint a badge for {} but {} did not have the reprint_badge right",
//                        view.getCurrentUsername(), attendee, overrideUser);
//                view.showOverrideRequiredWindow(this, attendeeList);
//            }
//        }
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

    public SearchByNameView getView() { return view; }
    public void setView(SearchByNameView view) { this.view = view; }

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
//            view.showPrintBadgeWindow(attendeeList);
        }
    }

    private Boolean validateBeforeCheckIn(CheckInView view, Attendee attendee) {
        attendee.setParentFormReceived(view.parentalConsentFormReceived());
        try {
            attendeeValidator.validate(attendee);
        } catch (ValidationException e) {
            view.notifyError(e.getMessage());
            return false;
        }
        if (attendee.isMinor()) {
            if (!view.parentalConsentFormReceived()) {
                view.notify("Error: Parental consent form has not been received");
                return false;
            }
        }
        if (!view.informationVerified()) {
            view.notify("Error: Information not verified");
            return false;
        }
        return true;
    }

    public void checkInAttendee(CheckInView view, Attendee attendee) {
        log.info("{} checked in preregistered attendee {}", view.getCurrentUser(), attendee);
        if (attendee != null) {
            if (validateBeforeCheckIn(view, attendee)) {
                attendee.setParentFormReceived(view.parentalConsentFormReceived());
                attendee.addHistoryEntry(view.getCurrentUser(), "Attendee Checked In");
                attendee.setCheckedIn(true);
                attendeeRepository.save(attendee);
            }
        }
    }

    @Override
    public void overrideEditLogin(OverrideRequiredForEditWindow window, String username, String password, AttendeeDetailView view) {
        User overrideUser = userRepository.findOneByUsernameIgnoreCase(username);
        if (overrideUser != null && overrideUser.checkPassword(password)) {
            if (overrideUser.hasRight("attendee_edit")) {
                window.close();
                view.enableEditFields(overrideUser);
            } else {
                view.notifyError(overrideUser.getUsername() + "does not have the right \"attendee_edit\"");
            }
        } else {
            view.notifyError("Bad username or ones/584password");
        }
    }

    @Override
    public void overrideEditCancel(OverrideRequiredForEditWindow window) {
        window.close();
    }

    public boolean attendeeHasChanged(Attendee attendee) {
        Attendee existing = attendeeRepository.findOne(attendee.getId());
        return !existing.fieldsSameAs(attendee);
    }
}
