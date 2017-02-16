package org.kumoricon.site.attendee.search;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.user.User;
import org.kumoricon.site.attendee.AddNoteHandler;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.CheckInConfirmationHandler;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.AddNoteWindow;
import org.kumoricon.site.attendee.window.ViewNoteWindow;

import java.util.List;
import java.util.Set;

public class AttendeeDetailWindow extends Window implements DetailFormHandler, CheckInConfirmationHandler, AddNoteHandler {

    private AttendeeDetailForm form;
    private Button btnSave;
    private Button btnCancel;
    private Button btnCheckIn;
    private Button btnSaveAndReprint;
    private Button btnAddNote;
    private Button btnEdit;
    private PopupView checkInPopup;
    private CheckBox attendeeInformationVerified;
    private CheckBox parentalConsentFormReceived;
    private Button btnInfoReceived;
    private HorizontalLayout buttonBar = new HorizontalLayout();

    private AttendeeSearchPresenter handler;
    private AttendeePrintView parentView;

    public AttendeeDetailWindow(AttendeePrintView parentView, AttendeeSearchPresenter handler) {
        super("Attendee Detail");
        this.handler = handler;
        this.parentView = parentView;
        setIcon(FontAwesome.USER);
        center();
        setModal(true);
        setWidth(1100, Unit.PIXELS);
        setHeight(800, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(true);
        form = new AttendeeDetailForm(this);
        form.setAllFieldsButCheckInDisabled();
        verticalLayout.addComponent(form);
        verticalLayout.addComponent(buildSaveCancel());
        setContent(verticalLayout);

        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public Attendee getAttendee() {return form.getAttendee(); }

    public void showAttendee(Attendee attendee) {
        form.show(attendee);

        form.setAllFieldsButCheckInDisabled();
        btnCheckIn.setEnabled(!attendee.getCheckedIn());
        setEditableFields(getParentView().getCurrentUser());
    }

    public void setAvailableBadges(List<Badge> availableBadges) {
        form.setAvailableBadges(availableBadges);
    }

    private HorizontalLayout buildSaveCancel() {
        buttonBar.setSpacing(true);
        buttonBar.setMargin(new MarginInfo(false, true, false, true));
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        btnEdit = new Button("Edit (Override)");
        btnEdit.addClickListener((Button.ClickListener) clickEvent -> {
           handler.overrideEdit(this);
        });

        btnCheckIn = new Button("Check In");
        btnCheckIn.addClickListener((Button.ClickListener) clickEvent -> showCheckInWindow());
        btnCheckIn.setVisible(parentView.currentUserHasRight("pre_reg_check_in"));

        btnAddNote = new Button("Add Note");
        btnAddNote.addClickListener((Button.ClickListener) clickEvent -> showAddNoteWindow());

        if (parentView.currentUserHasRight("reprint_badge")) {
            btnSaveAndReprint = new Button("Save and Reprint Badge");
        } else {
            btnSaveAndReprint = new Button("Reprint Badge (Override)");
        }

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                form.commit();
                handler.saveAttendee(this, form.getAttendee());
            } catch (FieldGroup.CommitException e) {
                parentView.notifyError(e.getMessage());
            }
        });
        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelAttendee(this));
        btnSaveAndReprint.addClickListener((Button.ClickListener) clickEvent ->
                handler.saveAttendeeAndReprintBadge(this, form.getAttendee(), null));
        checkInPopup = buildCheckInPopupView();

        buttonBar.addComponent(btnSave);
        buttonBar.addComponent(btnEdit);
        buttonBar.addComponent(btnSaveAndReprint);
        buttonBar.addComponent(btnAddNote);
        buttonBar.addComponent(btnCheckIn);
        buttonBar.addComponent(checkInPopup);
        buttonBar.addComponent(btnCancel);

        return buttonBar;
    }

    private void showAddNoteWindow() {
        AddNoteWindow window = new AddNoteWindow(this);
        parentView.showWindow(window);
    }


    public AttendeeSearchPresenter getHandler() { return handler; }
    public void setHandler(AttendeeSearchPresenter handler) { this.handler = handler; }

    public AttendeePrintView getParentView() { return parentView; }
    public void setParentView(AttendeePrintView parentView) { this.parentView = parentView; }

    public User getCurrentUser() {
        if (parentView != null) {
            return parentView.getCurrentUser();
        } else {
            return null;
        }
    }

    public void enableEditing(User overrideUser) {
        setEditableFields(overrideUser);
    }

    private void setEditableFields(User user) {
        // Set editable fields based on current user's rights
        if (user.hasRight("attendee_edit")) {
            form.setEditableFields(AttendeeDetailForm.EditableFields.ALL);
            form.setMinorFieldsEnabled(form.getAttendee().isMinor());

            if (user.hasRight("attendee_override_price")) {
                form.setManualPriceEnabled(true);
            } else {
                form.setManualPriceEnabled(false);
            }
        } else {
            form.setEditableFields(AttendeeDetailForm.EditableFields.NONE);
        }

        btnAddNote.setEnabled(user.hasRight("attendee_add_note"));

        // save and reprint badge only if the attendee is already checked in
        if (form.getAttendee() != null && form.getAttendee().getCheckedIn()) {
            btnSaveAndReprint.setVisible(true);
            if (user.hasRight("reprint_badge") || user.hasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        } else {
            btnSaveAndReprint.setVisible(false);
        }

        if (user.hasRight("attendee_edit")) {
            btnSave.setEnabled(true);
            btnSave.setVisible(true);
        } else {
            btnSave.setEnabled(false);
            btnSave.setVisible(false);
        }

        if (user.hasRight("attendee_edit_with_override") && !user.hasRight("attendee_edit")) {
            btnEdit.setEnabled(true);
            btnEdit.setVisible(true);
        } else {
            btnEdit.setEnabled(false);
            btnEdit.setVisible(false);
        }

        if (user.hasRight("pre_reg_check_in") && form.getAttendee() != null && !form.getAttendee().getCheckedIn()) {
            btnCheckIn.setVisible(true);
            btnCheckIn.setEnabled(true);
        } else {
            btnCheckIn.setVisible(false);
            btnCheckIn.setEnabled(false);
        }
    }

    public void showHistory(Set<AttendeeHistory> histories) {
        form.showHistory(histories);
    }

    private void showCheckInWindow() {
        Attendee attendee = form.getAttendee();
        if (attendee != null) {
            parentalConsentFormReceived.setVisible(attendee.isMinor());
            checkInPopup.setPopupVisible(true);
        }
    }

    private PopupView buildCheckInPopupView() {
        attendeeInformationVerified = new CheckBox("Information Verified");
        parentalConsentFormReceived = new CheckBox("Parental Consent Form Received");
        btnInfoReceived = new Button("Save");
        btnInfoReceived.setEnabled(false);
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(attendeeInformationVerified);
        layout.addComponent(parentalConsentFormReceived);
        layout.addComponent(btnInfoReceived);

        attendeeInformationVerified.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                btnInfoReceived.setEnabled(validateCheckInFields()));
        parentalConsentFormReceived.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                btnInfoReceived.setEnabled(validateCheckInFields()));
        btnInfoReceived.addClickListener((Button.ClickListener) clickEvent -> attendeeInformationVerified());

        return new PopupView(null, layout);
    }

    private boolean validateCheckInFields() {
        if (form.getAttendee().isMinor()) {
            return attendeeInformationVerified.getValue() && parentalConsentFormReceived.getValue();
        } else {
            return attendeeInformationVerified.getValue();
        }
    }

    @Override
    public void showAttendeeHistory(AttendeeHistory attendeeHistory) {
        Window window = new ViewNoteWindow(attendeeHistory);
        parentView.showWindow(window);
    }

    @Override
    public void attendeeInformationVerified() {
        Attendee attendee = form.getAttendee();
        handler.checkInAttendee(this, attendee);
    }

    public boolean parentalConsentFormReceived() {
        return parentalConsentFormReceived.getValue();
    }


    public boolean informationVerified() {
        return attendeeInformationVerified.getValue();
    }

    @Override
    public void addNote(AddNoteWindow window, String message) {
        handler.addNote(this, message);
        window.close();
    }

    @Override
    public void addNoteCancel(AddNoteWindow window) {
        window.close();
    }
}
