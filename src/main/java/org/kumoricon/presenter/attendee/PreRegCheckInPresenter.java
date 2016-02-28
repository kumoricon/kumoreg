package org.kumoricon.presenter.attendee;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.attendee.AttendeeDetailForm;
import org.kumoricon.view.attendee.PreRegCheckInView;
import org.kumoricon.view.attendee.PreRegSearchView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class PreRegCheckInPresenter implements PrintBadgeHandler {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private PreRegCheckInView view;

    private PrintBadgeWindow printBadgeWindow;
    private Attendee attendee;

    public PreRegCheckInPresenter() {
    }

    public void showAttendee(int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        AttendeeDetailForm form = view.getDetailForm();
        view.setAvailableBadges(badgeRepository.findAll());
        form.setAllFieldsButCheckInDisabled();
        view.showAttendee(attendee);
    }

    public void checkInAttendee() {
        attendee = view.getAttendee();
        List<Attendee> attendeeList = new ArrayList<>();
        attendeeList.add(attendee);
        printBadgeWindow = new PrintBadgeWindow(this, attendeeList);
        if (validateBeforeCheckIn(attendee)) {
            view.showWindow(printBadgeWindow);
        }
    }

    public void badgePrintSuccess() {
        printBadgeWindow.close();
        attendee.setParentFormReceived(view.parentalConsentFormReceived()); // This field isn't part of the main attendee
                                                                    // detail form, so it isn't bound automatically
                                                                    // but still needs to be set.
        attendee.setCheckedIn(true);
        attendeeRepository.save(attendee);
        view.notify(String.format("%s %s is checked in", attendee.getFirstName(), attendee.getLastName()));
        KumoRegUI.getCurrent().getNavigator().navigateTo(PreRegSearchView.VIEW_NAME + "/" + attendee.getOrder().getOrderId());

    }

    @Override
    public void reprintBadges(List<Attendee> attendeeList) {
        view.notify("Reprinting badge...");
    }

    public Boolean validateBeforeCheckIn(Attendee attendee) {
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

    public void cancelAttendee() {
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }


    public PreRegCheckInView getView() { return view; }
    public void setView(PreRegCheckInView view) { this.view = view; }

    @Override
    public void showAttendeeBadgeWindow(List<Attendee> attendeeList) {

    }
}