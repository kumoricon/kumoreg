package org.kumoricon.site.attendee.prereg;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.form.AttendeePreRegDetailForm;
import org.kumoricon.site.attendee.window.ViewNoteWindow;

import java.util.List;

public class PreRegCheckInWindow extends Window implements DetailFormHandler {

    private AttendeeDetailForm form;
    private CheckBox informationVerified = new CheckBox("Information Verified");
    private CheckBox consentFormReceived = new CheckBox("Parental Consent Form Received");
    private Button btnCheckIn = new Button("Check In");
    private Button btnCancel = new Button("Cancel");

    private PreRegPresenter handler;
    private PreRegView parentView;

    public PreRegCheckInWindow(PreRegView parentView, PreRegPresenter preRegPresenter) {
        super("Preregistered Attendee Check In");
        this.handler = preRegPresenter;
        this.parentView = parentView;
        setIcon(FontAwesome.USER);
        center();
        setModal(true);
        setWidth(1100, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        form = new AttendeePreRegDetailForm(this);
        form.setAllFieldsButCheckInDisabled();
        verticalLayout.addComponent(form);
        verticalLayout.addComponent(buildVerifiedCheckboxes());
        verticalLayout.addComponent(buildSaveCancel());
        setContent(verticalLayout);

        informationVerified.focus();
        btnCheckIn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnCheckIn.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public Boolean informationVerified() {
        if (informationVerified.getValue() == null) {
            return false;
        } else {
            return informationVerified.getValue();
        }
    }

    public Boolean parentalConsentFormReceived() {
        if (consentFormReceived.getValue() == null) {
            return false;
        } else {
            return consentFormReceived.getValue();
        }
    }

    public Attendee getAttendee() {return form.getAttendee(); }

    public void showAttendee(Attendee attendee) {
        form.show(attendee);

        informationVerified.setValue(false);
        consentFormReceived.setValue(attendee.getParentFormReceived());
        form.setAllFieldsButCheckInDisabled();
        if (attendee.isMinor() && !attendee.getCheckedIn()) {
            consentFormReceived.setEnabled(true);
        } else {
            consentFormReceived.setEnabled(false);
        }
        if (attendee.getCheckedIn()) {
            btnCheckIn.setEnabled(false);
            informationVerified.setEnabled(false);
            btnCheckIn.setCaption("Already Checked In");
        } else {
            btnCheckIn.setEnabled(true);
            informationVerified.setEnabled(true);
            btnCheckIn.setCaption("Check In");
        }
    }

    public void setAvailableBadges(List<Badge> availableBadges) {
        form.setAvailableBadges(availableBadges);
    }


    private HorizontalLayout buildVerifiedCheckboxes() {
        HorizontalLayout h = new HorizontalLayout();
        h.setMargin(false);
        h.setSpacing(true);
        h.addComponent(informationVerified);
        h.addComponent(consentFormReceived);
        return h;
    }

    private HorizontalLayout buildSaveCancel() {
        HorizontalLayout h = new HorizontalLayout();
        h.setMargin(false);
        h.setSpacing(true);

        btnCheckIn.addClickListener((Button.ClickListener) clickEvent -> handler.checkInAttendee(this, getAttendee()));
        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelAttendee(this));
        h.addComponent(btnCheckIn);
        h.addComponent(btnCancel);
        return h;
    }


    public PreRegPresenter getHandler() { return handler; }
    public void setHandler(PreRegPresenter handler) { this.handler = handler; }

    public PreRegView getParentView() { return parentView; }
    public void setParentView(PreRegView parentView) { this.parentView = parentView; }

    @Override
    public void showAttendeeHistory(AttendeeHistory attendeeHistory) {
        ViewNoteWindow window = new ViewNoteWindow(attendeeHistory);
        parentView.showWindow(window);
    }
}
