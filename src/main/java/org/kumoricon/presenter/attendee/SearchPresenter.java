package org.kumoricon.presenter.attendee;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.view.attendee.AttendeeDetailView;
import org.kumoricon.view.attendee.SearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private SearchView view;

    public SearchPresenter() {
    }

    public void searchChanged(String searchString) {
        Navigator navigator = KumoRegUI.getCurrent().getNavigator();
        navigator.navigateTo(view.VIEW_NAME + "/" + searchString);
    }

    public void searchFor(String searchString) {
        List<Attendee> attendees = attendeeRepository.findByLastNameOrBadgeNumber(searchString);
        view.getAttendeeBeanList().removeAllItems();
        if (attendees.size() > 0) {
            view.getAttendeeBeanList().addAll(attendees);
        } else {
            Notification.show("No matching attendees found");
        }

    }

    public void selectAttendee(Attendee attendee) {
        Navigator navigator = KumoRegUI.getCurrent().getNavigator();
        navigator.navigateTo(AttendeeDetailView.VIEW_NAME + "/" + attendee.getId().toString());
    }

    public SearchView getView() { return view; }
    public void setView(SearchView view) { this.view = view; }
}