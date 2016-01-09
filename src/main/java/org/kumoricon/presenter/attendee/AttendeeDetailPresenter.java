package org.kumoricon.presenter.attendee;

import com.vaadin.ui.Notification;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.attendee.AttendeeDetailForm;
import org.kumoricon.view.attendee.AttendeeDetailView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendeeDetailPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private AttendeeDetailView view;

    public AttendeeDetailPresenter() {
    }

    public void showAttendee(int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            AttendeeDetailForm form = (AttendeeDetailForm) view.getDetailForm();
            form.setAvailableBadges(badgeRepository.findAll());
            form.show(attendee);
        } else {
            Notification.show("Error: attendee " + id + " not found.");
        }
    }

    public void saveAttendee() {
        Attendee attendee = view.getAttendee();
        attendeeRepository.save(attendee);
        Notification.show(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public void saveAttendeeAndRepreintBadge() {
        saveAttendee();
        // print badge
    }

    public void cancelAttendee() {
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public AttendeeDetailView getView() { return view; }
    public void setView(AttendeeDetailView view) { this.view = view; }
}