package org.kumoricon.site.attendee.search;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.site.attendee.window.ViewNoteWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchDetailView.TEMPLATE)
public class AttendeeSearchDetailView extends BaseView implements View, AttendeePrintView, DetailFormHandler {
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "attendee_search";

    public static final String TEMPLATE = "attendeeSearch/{searchString}/{attendeeId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    private Integer attendeeId;
    private String searchString;

    private AttendeeDetailForm form = new AttendeeDetailForm(this);
    private Button btnSave;
    private Button btnCancel;
    private Button btnCheckIn;
    private Button btnSaveAndReprint;
    private Button btnAddNote;
    private Button btnEdit;
    private PopupView checkInPopup;

    @Autowired
    private AttendeeSearchPresenter handler;


    @PostConstruct
    public void init() {
        setSizeFull();

        VerticalLayout buttons = buildSaveCancel();
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);

        
        form.setAllFieldsButCheckInDisabled();
        addComponents(buttons, form);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.searchString = map.get("searchString");
        try {
            this.attendeeId = Integer.parseInt(map.get("attendeeId"));
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            return;
        }

        handler.showAttendee(this, attendeeId);
    }

    public void afterSuccessfulFetch(Attendee attendee) {
        form.show(attendee);
    }


    @Override
    public void refresh() {
        handler.showAttendee(this, attendeeId);
    }


    public void setHandler(AttendeeSearchPresenter presenter) {
        this.handler = presenter;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

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

    public void showOverrideEditWindow(AttendeeSearchPresenter presenter, AttendeeDetailWindow attendeeDetailWindow) {
        OverrideRequiredForEditWindow window = new OverrideRequiredForEditWindow(presenter, "attendee_edit", attendeeDetailWindow);
        showWindow(window);
    }

    @Override
    public void showAttendeeHistory(AttendeeHistory attendeeHistory) {
        ViewNoteWindow window = new ViewNoteWindow(attendeeHistory);
        showWindow(window);
    }


    private VerticalLayout buildSaveCancel() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setMargin(new MarginInfo(false, true, false, true));
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        btnEdit = new Button("Edit (Override)");
        btnEdit.addClickListener((Button.ClickListener) clickEvent -> {
            handler.overrideEdit(this);
        });

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
        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelAttendee(this));
        btnSaveAndReprint.addClickListener((Button.ClickListener) clickEvent ->
                handler.saveAttendeeAndReprintBadge(this, form.getAttendee(), null));
        checkInPopup = buildCheckInPopupView();

        btnPrePrintBadge = new Button("Pre-Print Badge");
        btnPrePrintBadge.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                form.commit();
                handler.saveAttendeeAndPrePrintBadge(this, form.getAttendee());
            } catch (FieldGroup.CommitException e) {
                parentView.notifyError(e.getMessage());
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


    private PopupView buildCheckInPopupView() {
        attendeeInformationVerified = new com.vaadin.v7.ui.CheckBox("Information Verified");
        parentalConsentFormReceived = new com.vaadin.v7.ui.CheckBox("Parental Consent Form Received");
        btnInfoReceived = new Button("Save");
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

}
