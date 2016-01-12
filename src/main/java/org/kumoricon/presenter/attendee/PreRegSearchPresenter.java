package org.kumoricon.presenter.attendee;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
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

    public PreRegSearchPresenter() {
    }

    public void searchChanged(String searchString) {
        Navigator navigator = KumoRegUI.getCurrent().getNavigator();
        navigator.navigateTo(view.getViewName() + "/" + searchString);
    }

    public void searchFor(String searchString) {
        if (searchString != null) {
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
                Notification.show("No matching attendees found");
            }
        }
    }

    public void selectAttendee(Attendee attendee) {
        Navigator navigator = KumoRegUI.getCurrent().getNavigator();
        navigator.navigateTo(PreRegCheckInView.VIEW_NAME + "/" + attendee.getId().toString());
    }

    public PreRegSearchView getView() { return view; }
    public void setView(PreRegSearchView view) { this.view = view; }
}