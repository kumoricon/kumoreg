package org.kumoricon.site.attendee;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.search.AttendeeSearchPresenter;
import org.kumoricon.site.attendee.search.AttendeeSearchView;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.site.attendee.window.ViewNoteWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@ViewScope
public abstract class AttendeeDetailView extends BaseView implements View, AttendeePrintView, DetailFormHandler {

    protected Integer attendeeId;

    protected AttendeeDetailForm form = new AttendeeDetailForm(this);
    protected Button btnSave;
    protected Button btnCancel;
    protected Button btnCheckIn;
    protected Button btnSaveAndReprint;
    protected Button btnPrePrintBadge;
    protected Button btnAddNote;
    protected Button btnEdit;
    protected PopupView checkInPopup;

    @Autowired
    protected AttendeeSearchPresenter handler;


    @PostConstruct
    public void init() {
        setSizeFull();

        VerticalLayout buttons = buildSaveCancel();
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);

        setButtonVisibility();

        form.setAllFieldsButCheckInDisabled();
        addComponents(form, buttons);

    }

    protected void setButtonVisibility() {
        btnSave.setVisible(currentUserHasRight("attendee_edit"));
        btnEdit.setVisible(currentUserHasRight("attendee_edit_with_override") && !currentUserHasRight("attendee_edit"));
        btnCheckIn.setVisible(currentUserHasRight("pre_reg_check_in"));
        btnPrePrintBadge.setVisible(currentUserHasRight("pre_print_badges"));
        btnSaveAndReprint.setVisible(currentUserHasRight("reprint_badge") || currentUserHasRight("reprint_badge_with_override"));
        btnAddNote.setVisible(currentUserHasRight("attendee_add_note"));
    }


    @Override
    public void refresh() {
        handler.showAttendee(this, attendeeId);
    }

    public void showAttendee(Attendee attendee, List<Badge> all) {
        form.setAvailableBadges(all);
        form.show(attendee);
    }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow printBadgeWindow = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(printBadgeWindow);
    }

    public void showOverrideRequiredWindow(AttendeeSearchPresenter presenter, List<Attendee> attendeeList)
    {
        OverrideRequiredWindow overrideRequiredWindow = new OverrideRequiredWindow(presenter, "reprint_badge", attendeeList);
        showWindow(overrideRequiredWindow);
    }

    public void showOverrideEditWindow(AttendeeSearchPresenter presenter, AttendeeDetailView attendeeDetailWindow) {
        OverrideRequiredForEditWindow window = new OverrideRequiredForEditWindow(presenter, "attendee_edit", attendeeDetailWindow);
        showWindow(window);
    }

    @Override
    public void showAttendeeHistory(AttendeeHistory attendeeHistory) {
        ViewNoteWindow window = new ViewNoteWindow(attendeeHistory);
        showWindow(window);
    }


    protected VerticalLayout buildSaveCancel() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        btnEdit = new Button("Edit (Override)");
        btnEdit.addClickListener((Button.ClickListener) clickEvent -> showOverrideEditWindow(handler, this));

        btnCheckIn = new Button("Check In");
        btnCheckIn.addClickListener((Button.ClickListener) clickEvent -> showCheckInWindow());
        btnCheckIn.setVisible(currentUserHasRight("pre_reg_check_in"));

        btnAddNote = new Button("Add Note");
        btnAddNote.addClickListener((Button.ClickListener) clickEvent -> showAddNoteWindow());

        if (currentUserHasRight("reprint_badge")) {
            btnSaveAndReprint = new Button("Save and Reprint Badge");
        } else {
            btnSaveAndReprint = new Button("Reprint Badge (Override)");
        }

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                form.commit();
                handler.saveAttendee(this, form.getAttendee());
            } catch (FieldGroup.CommitException e) {
                notifyError(e.getMessage());
            }
        });
        btnCancel.addClickListener((Button.ClickListener) clickEvent -> close());
        btnSaveAndReprint.addClickListener((Button.ClickListener) clickEvent ->
                handler.saveAttendeeAndReprintBadge(this, form.getAttendee(), null));
        checkInPopup = buildCheckInPopupView();

        btnPrePrintBadge = new Button("Pre-Print Badge");
        btnPrePrintBadge.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                form.commit();
                handler.saveAttendeeAndPrePrintBadge(this, form.getAttendee());
            } catch (FieldGroup.CommitException e) {
                notifyError(e.getMessage());
            }
        });

        buttons.addComponent(btnSave);
        buttons.addComponent(btnEdit);
        buttons.addComponent(btnSaveAndReprint);
        buttons.addComponent(btnAddNote);
        buttons.addComponent(btnCheckIn);
        buttons.addComponent(checkInPopup);
        buttons.addComponent(btnPrePrintBadge);
        buttons.addComponent(btnCancel);
        return buttons;
    }


    protected PopupView buildCheckInPopupView() {
        com.vaadin.v7.ui.CheckBox attendeeInformationVerified = new com.vaadin.v7.ui.CheckBox("Information Verified");
        com.vaadin.v7.ui.CheckBox parentalConsentFormReceived = new com.vaadin.v7.ui.CheckBox("Parental Consent Form Received");
        Button btnInfoReceived = new Button("Save");
        btnInfoReceived.setEnabled(false);
        com.vaadin.v7.ui.VerticalLayout layout = new com.vaadin.v7.ui.VerticalLayout();
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


    private void showCheckInWindow() {

    }

    protected void showAddNoteWindow() {

    }

    private boolean validateCheckInFields() {

        return false;
    }

    private void attendeeInformationVerified() {

    }

}
