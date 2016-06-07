package org.kumoricon.site.attendee.search;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.user.User;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.AddNoteWindow;
import org.kumoricon.site.attendee.window.ViewNoteWindow;

import java.util.List;

public class AttendeeDetailWindow extends Window implements DetailFormHandler {

    private AttendeeDetailForm form;
    private Button btnSave;
    private Button btnCancel;
    private Button btnSaveAndReprint;
    private Button btnAddNote;
    private Button btnEdit;

    private AttendeeSearchPresenter handler;
    private BaseView parentView;

    public AttendeeDetailWindow(BaseView parentView, AttendeeSearchPresenter handler) {
        super("Attendee Detail");
        this.handler = handler;
        this.parentView = parentView;
        setIcon(FontAwesome.USER);
        center();
        setModal(true);
        setWidth(1100, Unit.PIXELS);
        setHeight("98%");

        VerticalLayout verticalLayout = new VerticalLayout();
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
        setEditableFields(getParentView().getCurrentUser());
    }

    public void setAvailableBadges(List<Badge> availableBadges) {
        form.setAvailableBadges(availableBadges);
    }

    private HorizontalLayout buildSaveCancel() {
        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(new MarginInfo(false, true, false, true));
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        btnEdit = new Button("Edit (Override)");
        btnEdit.addClickListener((Button.ClickListener) clickEvent -> {
           handler.overrideEdit(this);
        });
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
        h.addComponent(btnSave);
        h.addComponent(btnEdit);
        h.addComponent(btnSaveAndReprint);
        h.addComponent(btnAddNote);
        h.addComponent(btnCancel);
        return h;
    }

    private void showAddNoteWindow() {
        AddNoteWindow window = new AddNoteWindow(handler, this);
        parentView.showWindow(window);
    }


    public AttendeeSearchPresenter getHandler() { return handler; }
    public void setHandler(AttendeeSearchPresenter handler) { this.handler = handler; }

    public BaseView getParentView() { return parentView; }
    public void setParentView(BaseView parentView) { this.parentView = parentView; }

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
        } else if (user.hasRight("attendee_edit_notes")) {
            form.setEditableFields(AttendeeDetailForm.EditableFields.NOTES);
            btnAddNote.setEnabled(true);
        } else {
            form.setEditableFields(AttendeeDetailForm.EditableFields.NONE);
            btnAddNote.setEnabled(false);
        }

        if (user.hasRight("attendee_edit") || user.hasRight("attendee_edit_notes")) {
            btnSave.setEnabled(true);
            if (user.hasRight("reprint_badge") || user.hasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        } else {
            btnSave.setEnabled(false);
            if (user.hasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        }

        if (user.hasRight("attendee_edit_with_override") && !user.hasRight("attendee_edit")) {
            btnEdit.setEnabled(true);
            btnEdit.setVisible(true);
        } else {
            btnEdit.setEnabled(false);
            btnEdit.setVisible(false);
        }
    }

    public void showHistory(List<AttendeeHistory> histories) {
        form.showHistory(histories);
    }

    @Override
    public void showAttendeeHistory(AttendeeHistory attendeeHistory) {
        ViewNoteWindow window = new ViewNoteWindow(attendeeHistory);
        parentView.showWindow(window);
    }
}
