package org.kumoricon.view.attendee;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.attendee.AttendeeDetailPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = AttendeeDetailView.VIEW_NAME)
public class AttendeeDetailView extends BaseView implements View {
    public static final String VIEW_NAME = "attendee";
    public static final String REQUIRED_RIGHT = "attendee_search";

    private AttendeeDetailForm detailForm = new AttendeeDetailForm();
    private Button btnSave;
    private Button btnCancel;
    private Button btnSaveAndReprint;

    @Autowired
    private AttendeeDetailPresenter handler;

    @PostConstruct
    public void init() {
        handler.setView(this);
        addComponent(detailForm);
        addComponent(buildSaveCancel());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.equals("")) {
            handler.showAttendee((Integer.parseInt(viewChangeEvent.getParameters())));
        }

        // Set editable fields based on current user's rights
        if (currentUserHasRight("attendee_edit")) {
            detailForm.setEditableFields(AttendeeDetailForm.EditableFields.ALL);
            if (currentUserHasRight("attendee_override_price")) {
                detailForm.setManualPriceEnabled(true);
            } else {
                detailForm.setManualPriceEnabled(false);
            }
        } else if (currentUserHasRight("attendee_edit_notes")) {
            detailForm.setEditableFields(AttendeeDetailForm.EditableFields.NOTES);
        } else {
            detailForm.setEditableFields(AttendeeDetailForm.EditableFields.NONE);
        }

        if (currentUserHasRight("attendee_edit") || currentUserHasRight("attendee_edit_notes")) {
            // Should you be able to edit an attendee with override?
            btnSave.setEnabled(true);
            if (currentUserHasRight("reprint_badge") || currentUserHasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        } else {
            btnSave.setEnabled(false);
            if (currentUserHasRight("reprint_badge_with_override")) {
                btnSaveAndReprint.setEnabled(true);
            } else {
                btnSaveAndReprint.setEnabled(false);
            }
        }

    }

    private HorizontalLayout buildSaveCancel() {
        HorizontalLayout h = new HorizontalLayout();
        h.setMargin(true);
        h.setSpacing(true);
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        if (currentUserHasRight("reprint_badge")) {
            btnSaveAndReprint = new Button("Save and Reprint Badge");
        } else {
            btnSaveAndReprint = new Button("Reprint Badge (Override)");
        }

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                detailForm.commit();
                handler.saveAttendee();
            } catch (FieldGroup.CommitException e) {
                Notification.show(e.getMessage());
            }
        });
        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelAttendee());
        btnSaveAndReprint.addClickListener((Button.ClickListener) clickEvent -> handler.saveAttendeeAndReprintBadge(null));
        h.addComponent(btnSave);
        h.addComponent(btnSaveAndReprint);
        h.addComponent(btnCancel);
        return h;
    }

    public void setHandler(AttendeeDetailPresenter handler) { this.handler = handler; }

    public Layout getDetailForm() { return detailForm; }
    public Attendee getAttendee() { return detailForm.getAttendee(); }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
