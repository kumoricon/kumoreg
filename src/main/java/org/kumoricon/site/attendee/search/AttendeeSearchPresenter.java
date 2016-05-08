package org.kumoricon.site.attendee.search;

import com.vaadin.ui.Window;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.site.attendee.*;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.site.BaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class AttendeeSearchPresenter implements PrintBadgeHandler, OverrideHandler, OverrideEditHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgePrintService badgePrintService;

    private static final Logger log = LoggerFactory.getLogger(AttendeeSearchPresenter.class);

    private AttendeeSearchView view;
    private OverrideRequiredWindow overrideRequiredWindow;

    public AttendeeSearchPresenter() {
    }

    public void showAttendee(int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            log.info("{} displayed Attendee {}", view.getCurrentUser(), attendee);
            view.showAttendee(attendee, badgeRepository.findAll());
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUser(), id);
            view.notify("Error: attendee " + id + " not found.");
        }
    }

    public void saveAttendee(AttendeeDetailWindow window, Attendee attendee) {
        BaseView view = window.getParentView();
        try {
            attendee.validate();
            attendee = attendeeRepository.save(attendee);
            view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
            log.info("{} saved {}", view.getCurrentUser(), attendee);
            window.close();
            view.refresh();
        } catch (ValueException e) {
            log.error("{} tried to save {} and got error {}",
                    window.getCurrentUser(), attendee, e.getMessage());
            view.notifyError(e.getMessage());
        }
    }

    public void saveAttendeeAndReprintBadge(Window window, Attendee attendee, User overrideUser) {
        // If overrideUser is null, get the current user from the view
        try {
            attendee.validate();
            attendee = attendeeRepository.save(attendee);
            // Todo: Will have to refactor this
            // Todo: log.info("{} saved {}", window.getCurrentUser(), attendee);
        } catch (ValueException e) {
            view.notifyError(e.getMessage());
//          Todo:  log.error("{} tried to save {} and got error {}",
//                    window.getCurrentUser(), attendee, e.getMessage());
            return;
        }

        window.close();
        List<Attendee> attendeeList = new ArrayList<>();
        attendeeList.add(attendee);
        if (overrideUser == null) {
            if (view.currentUserHasRight("reprint_badge")) {
                log.info("{} reprinting badge(s) for {}", view.getCurrentUser(), attendee);
                showAttendeeBadgeWindow(view, attendeeList);
            } else {
                overrideRequiredWindow = new OverrideRequiredWindow(this, "reprint_badge", attendeeList);
                view.showWindow(overrideRequiredWindow);
            }
        } else {
            if (overrideUser.hasRight("reprint_badge")) {
                log.info("{} reprinting badge(s) for {} with override from {}",
                        view.getCurrentUser(), attendee, overrideUser);
                showAttendeeBadgeWindow(view, attendeeList);
            } else {
                view.notifyError("Override user does not have the required right");
                log.error("{} requested an override but {} did not have the reprint_badge right",
                        view.getCurrentUser(), overrideUser);
                List<Object> target = new ArrayList<>();
                target.add(attendee);
                overrideRequiredWindow = new OverrideRequiredWindow(this, "reprint_badge", attendeeList);
                view.showWindow(overrideRequiredWindow);
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
            log.info("{} got reprint badges override from {}", view.getCurrentUser(), overrideUser);
            saveAttendeeAndReprintBadge(window, (Attendee)targets.get(0), overrideUser);
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

    public void searchChanged(String searchString) {
        if (searchString != null) {
            view.navigateTo(AttendeeSearchView.VIEW_NAME + "/" + searchString.trim());
        }
    }

    public void searchFor(String searchString) {
        if (searchString != null && !searchString.trim().isEmpty()) {
            searchString = searchString.trim();
            List<Attendee> attendees = attendeeRepository.findByLastNameOrBadgeNumber(searchString);
            log.info("{} searched Attendees for \"{}\" and got {} results", view.getCurrentUser(), searchString, attendees.size());
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
                    view.getCurrentUser(), overrideUser, attendeeDetailWindow.getAttendee());
            window.close();
            attendeeDetailWindow.enableEditing(overrideUser);
        } else {
            view.notify("Bad username or password");
        }
    }

    @Override
    public void overrideEditCancel(OverrideRequiredForEditWindow window) { window.close(); }

    public void overrideEdit(AttendeeDetailWindow attendeeDetailWindow) {
        OverrideRequiredForEditWindow window = new OverrideRequiredForEditWindow(this, "attendee_edit", attendeeDetailWindow);
        view.showWindow(window);
    }
}