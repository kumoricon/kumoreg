package org.kumoricon.presenter.attendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.view.attendee.BadgeWarningWindow;
import org.kumoricon.view.attendee.PreRegCheckInView;
import org.kumoricon.view.attendee.PreRegSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("request")
public class PreRegSearchPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private PreRegSearchView view;
    private BadgeWarningWindow warningWindow;

    public PreRegSearchPresenter() {
    }

    public void searchChanged(String searchString) {
        if (searchString != null) {
            view.navigateTo(view.VIEW_NAME + "/" + searchString.trim());
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

    public PreRegSearchView getView() { return view; }
    public void setView(PreRegSearchView view) { this.view = view; }

    public void continueCheckIn(Attendee attendee) {
        if (warningWindow != null) { warningWindow.close(); }
        view.navigateTo(PreRegCheckInView.VIEW_NAME + "/" + attendee.getId().toString());
    }

    public void abortCheckIn() {
        if (warningWindow != null) { warningWindow.close(); }
    }
}