package org.kumoricon.presenter.attendee;

import com.vaadin.navigator.Navigator;
import org.kumoricon.KumoRegUI;
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
            Navigator navigator = KumoRegUI.getCurrent().getNavigator();
            navigator.navigateTo(view.VIEW_NAME + "/" + searchString.trim());
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
            if (!attendee.getBadge().getWarningMessage().trim().equals("")) {
                warningWindow = new BadgeWarningWindow(this, attendee);
                KumoRegUI.getCurrent().addWindow(warningWindow);
            } else {
                continueCheckIn(attendee);
            }
        }
    }

    public PreRegSearchView getView() { return view; }
    public void setView(PreRegSearchView view) { this.view = view; }

    public void continueCheckIn(Attendee attendee) {
        if (warningWindow != null) { warningWindow.close(); }
        Navigator navigator = KumoRegUI.getCurrent().getNavigator();
        navigator.navigateTo(PreRegCheckInView.VIEW_NAME + "/" + attendee.getId().toString());
    }

    public void abortCheckIn() {
        if (warningWindow != null) { warningWindow.close(); }
    }
}