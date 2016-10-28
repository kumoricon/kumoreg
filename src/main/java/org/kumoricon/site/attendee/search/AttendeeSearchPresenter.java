package org.kumoricon.site.attendee.search;

import com.vaadin.ui.Window;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.service.validate.AttendeeValidator;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.*;
import org.kumoricon.site.attendee.window.AddNoteWindow;
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
public class AttendeeSearchPresenter implements PrintBadgeHandler, OverrideHandler, OverrideEditHandler, AddNoteHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private AttendeeHistoryRepository attendeeHistoryRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgePrintService badgePrintService;

    @Autowired
    private AttendeeValidator attendeeValidator;

    private static final Logger log = LoggerFactory.getLogger(AttendeeSearchPresenter.class);

    private AttendeeSearchView view;

    public AttendeeSearchPresenter() {
    }

    public void showAttendee(int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            log.info("{} displayed Attendee {}", view.getCurrentUsername(), attendee);
            view.showAttendee(attendee, badgeRepository.findAll());
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUsername(), id);
            view.notify("Error: attendee " + id + " not found.");
        }
    }

    public void saveAttendee(AttendeeDetailWindow window, Attendee attendee) {
        BaseView view = window.getParentView();
        try {
            attendeeValidator.validate(attendee);
            attendee = attendeeRepository.save(attendee);
            view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
            log.info("{} saved {}", view.getCurrentUsername(), attendee);
            window.close();
            view.refresh();
        } catch (ValueException e) {
            log.error("{} tried to save {} and got error {}",
                    window.getCurrentUser(), attendee, e.getMessage());
            view.notifyError(e.getMessage());
        }
    }

    public void saveAttendeeAndReprintBadge(Window window, Attendee attendee, User overrideUser) {
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
        } catch (ValueException e) {
            view.notifyError(e.getMessage());
            log.error("{} tried to save {} and got error {}", view.getCurrentUsername(), attendee, e.getMessage());
            return;
        }

        window.close();
        List<Attendee> attendeeList = new ArrayList<>();
        attendeeList.add(attendee);
        // If no override user, check permissions on the current user

        if (overrideUser == null) {
            if (view.currentUserHasRight("reprint_badge")) {
                log.info("{} reprinting badge(s) for {}", view.getCurrentUsername(), attendee);
                showAttendeeBadgeWindow(view, attendeeList);
            } else {
                view.showOverrideRequiredWindow(this, attendeeList);
            }
        } else {
            if (overrideUser.hasRight("reprint_badge")) {
                log.info("{} reprinting badge(s) for {} with override from {}",
                        view.getCurrentUsername(), attendee, overrideUser);
                showAttendeeBadgeWindow(view, attendeeList);
            } else {
                view.notifyError("Override user does not have the required right");
                log.error("{} requested an override to reprint a badge for {} but {} did not have the reprint_badge right",
                        view.getCurrentUsername(), attendee, overrideUser);
                view.showOverrideRequiredWindow(this, attendeeList);
            }
        }
    }

    public void cancelAttendee(AttendeeDetailWindow window) {
        window.close();
    }

    public AttendeeSearchView getView() { return view; }
    public void setView(AttendeeSearchView view) { this.view = view; }

    @Override
    public void overrideLogin(OverrideRequiredWindow window, String username, String password, List<Attendee> targets) {
        User overrideUser = userRepository.findOneByUsernameIgnoreCase(username);
        if (overrideUser != null && overrideUser.checkPassword(password) && overrideUser.hasRight("reprint_badge")) {
            log.info("{} got reprint badges override from {}", view.getCurrentUsername(), overrideUser);
            saveAttendeeAndReprintBadge(window, targets.get(0), overrideUser);
        } else {
            view.notify("Bad username or password");
        }
    }

    @Override
    public void overrideCancel(OverrideRequiredWindow window) {
        window.close();
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        if (attendeeList != null) {
            view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
            view.showPrintBadgeWindow(attendeeList);
        }
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
            log.info("{} reprinting badges due to error for {}", view.getCurrentUsername(), attendeeList);
            view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
        } else {
            view.notify("No attendees selected");
        }
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees, printBadgeWindow.getParentView().getCurrentClientIPAddress());
    }

    public void searchChanged(String searchString) {
        if (searchString != null) {
            view.navigateTo(AttendeeSearchView.VIEW_NAME + "/" + searchString.trim());
        }
    }

    public void searchFor(String searchString) {
        if (searchString != null && !searchString.trim().isEmpty()) {
            searchString = searchString.trim();
            List<Attendee> attendees = attendeeRepository.findByLastNameOrBadgeNumber(searchString);
            log.info("{} searched Attendees for \"{}\" and got {} results", view.getCurrentUsername(), searchString, attendees.size());
            if (attendees.size() > 0) {
                view.afterSuccessfulFetch(attendees);
            } else {
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

    public void overrideEdit(AttendeeDetailWindow attendeeDetailWindow) {
        view.showOverrideEditWindow(this, attendeeDetailWindow);
    }

    @Override
    public void addNote(AddNoteWindow window, String message) {
        log.info("{} added note \"{}\" to {}",
                view.getCurrentUsername(),
                message.replaceAll("(\\r|\\n)+", " "),
                window.getParentWindow().getAttendee());
        AttendeeDetailWindow parentWindow = window.getParentWindow();
        AttendeeHistory ah = new AttendeeHistory(view.getCurrentUser(), parentWindow.getAttendee(), message);
        attendeeHistoryRepository.save(ah);
        parentWindow.showHistory(attendeeHistoryRepository.findByAttendee(parentWindow.getAttendee()));
        window.close();
    }

    @Override
    public void addNoteCancel(AddNoteWindow window) {
        window.close();
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
