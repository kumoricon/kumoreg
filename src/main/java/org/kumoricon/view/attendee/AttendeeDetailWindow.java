package org.kumoricon.view.attendee;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.presenter.attendee.AttendeeDetailPresenter;

import java.util.List;

public class AttendeeDetailWindow extends Window {

    private AttendeeDetailForm form;
    private CheckBox informationVerified = new CheckBox("Information Verified");
    private CheckBox consentFormReceived = new CheckBox("Parental Consent Form Received");
    private Button btnSave;
    private Button btnCancel;
    private Button btnSaveAndReprint;

    private AttendeeDetailPresenter handler;
    private PreRegView parentView;

    public AttendeeDetailWindow(PreRegView parentView, AttendeeDetailPresenter handler) {
        super("Attendee Detail");
        this.handler = handler;
        this.parentView = parentView;
        setIcon(FontAwesome.USER);
        center();
        setModal(true);
        setWidth(950, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        form = new AttendeePreRegDetailForm();
        form.setAllFieldsButCheckInDisabled();
        verticalLayout.addComponent(form);
        verticalLayout.addComponent(buildVerifiedCheckboxes());
        verticalLayout.addComponent(buildSaveCancel());
        setContent(verticalLayout);
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
        // Set editable fields based on current user's rights
        if (parentView.currentUserHasRight("attendee_edit")) {
            form.setEditableFields(AttendeeDetailForm.EditableFields.ALL);
            form.setMinorFieldsEnabled(form.getAttendee().isMinor());

            if (parentView.currentUserHasRight("attendee_override_price")) {
                form.setManualPriceEnabled(true);
            } else {
                form.setManualPriceEnabled(false);
            }
        } else if (parentView.currentUserHasRight("attendee_edit_notes")) {
            form.setEditableFields(AttendeeDetailForm.EditableFields.NOTES);
        } else {
            form.setEditableFields(AttendeeDetailForm.EditableFields.NONE);
        }

        if (parentView.currentUserHasRight("attendee_edit") || parentView.currentUserHasRight("attendee_edit_notes")) {
            // Should you be able to edit an attendee with override?
            btnSave.setEnabled(true);
            if (parentView.currentUserHasRight("reprint_badge") || parentView.currentUserHasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        } else {
            btnSave.setEnabled(false);
            if (parentView.currentUserHasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        }
    }

    public void setAvailableBadges(List<Badge> availableBadges) {
        form.setAvailableBadges(availableBadges);
    }


    private HorizontalLayout buildVerifiedCheckboxes() {
        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(false);
        h.addComponent(informationVerified);
        h.addComponent(consentFormReceived);
        return h;
    }

    private HorizontalLayout buildSaveCancel() {
        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(true);
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        if (parentView.currentUserHasRight("reprint_badge")) {
            btnSaveAndReprint = new Button("Save and Reprint Badge");
        } else {
            btnSaveAndReprint = new Button("Reprint Badge (Override)");
        }

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                form.commit();
//                handler.saveAttendee(this, form.getAttendee());
            } catch (FieldGroup.CommitException e) {
                parentView.notifyError(e.getMessage());
            }
        });
//        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelAttendee(this));
//        btnSaveAndReprint.addClickListener((Button.ClickListener) clickEvent ->
//                handler.saveAttendeeAndReprintBadge(this, form.getAttendee(), null));
        h.addComponent(btnSave);
        h.addComponent(btnSaveAndReprint);
        h.addComponent(btnCancel);
        return h;
    }


    public AttendeeDetailPresenter getHandler() { return handler; }
    public void setHandler(AttendeeDetailPresenter handler) { this.handler = handler; }

    public PreRegView getParentView() { return parentView; }
    public void setParentView(PreRegView parentView) { this.parentView = parentView; }
}
