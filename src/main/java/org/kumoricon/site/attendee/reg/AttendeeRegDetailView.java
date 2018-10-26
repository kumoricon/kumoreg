package org.kumoricon.site.attendee.reg;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.attendee.AttendeeDetailView;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.window.WarningWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

@ViewScope
@SpringView(name = AttendeeRegDetailView.TEMPLATE)
public class AttendeeRegDetailView extends AttendeeDetailView implements View, DetailFormHandler {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    public static final String TEMPLATE = "order/{orderId}/{attendeeId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected Integer orderId;

    private Button btnDelete;

    private OrderPresenter orderPresenter;

    @Autowired
    public AttendeeRegDetailView(OrderPresenter orderPresenter) {
        this.orderPresenter = orderPresenter;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        try {
            this.orderId = Integer.parseInt((map.get("orderId")));
        } catch (NumberFormatException ex) {
            notifyError("Bad order id: must be an integer");
            navigateTo("/");
        }

        if (map.get("attendeeId") != null && map.get("attendeeId").toLowerCase().equals("new")) {
            orderPresenter.addNewAttendee(this, this.orderId);
        } else {
            try {
                this.attendeeId = Integer.parseInt(map.get("attendeeId"));
            } catch (NumberFormatException ex) {
                notifyError("Bad attendee id: must be an integer");
                close();
            }

            handler.showAttendee(this, attendeeId);
        }
    }

    @Override
    public void showAttendee(Attendee attendee, List<Badge> all) {
        // Make sure this attendee is actually in the the order specified in the URL
        if (!orderId.equals(attendee.getOrder().getId())) {
            notifyError(String.format("Error: Attendee %s is not in order %s", attendee.getId(), orderId));
            close();
        }
        form.setAvailableBadges(all);
        form.show(attendee);
        if (!currentUserHasRight("fan_name_at_con_checkin")) form.hideFanNameField();
        form.setManualPriceEnabled(currentUserHasRight("attendee_override_price"));
        form.focusFirstName();

        if (attendee.getId() == null) { // Attendee hasn't been saved to database yet
            btnAddNote.setVisible(false);
            btnDelete.setVisible(false);
        }
    }


    @Override
    protected VerticalLayout buildSaveCancel() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");

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
                orderPresenter.saveAttendee(this, form.getAttendee());
                close();
            } catch (Exception e) {
                notifyError(e.getMessage());
            }
        });

        btnDelete = new Button("Delete");
        btnDelete.addStyleName(ValoTheme.BUTTON_DANGER);
        btnDelete.addClickListener((Button.ClickListener) clickEvent -> {
           orderPresenter.removeAttendeeFromOrder(this, form.getAttendee());
           navigateTo(VIEW_NAME + "/" + orderId);
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> close());

        buttons.addComponents(btnSave, btnAddNote, btnDelete, btnCancel);
        return buttons;
    }

    @Override
    protected void reprintClicked() {
        // Not used during at-con registration
    }

    @Override
    protected void setButtonVisibility() {
        btnSave.setVisible(currentUserHasRight("at_con_registration"));
        btnAddNote.setVisible(currentUserHasRight("attendee_add_note"));
    }

    @Override
    protected void showAddNoteWindow() {
        Attendee attendee = form.getAttendee();
        if (handler.attendeeHasChanged(attendee)) {
            try {
                attendee = handler.saveAttendee(this, attendee);
                navigateTo(VIEW_NAME + "/" + attendee.getOrder().getId() + "/" + attendee.getId() + "/note/new");
            } catch (ValidationException ex) {
                notifyError(ex.getMessage());
            }
        } else {
            if (attendee != null && attendee.getOrder() != null) {
                navigateTo(VIEW_NAME + "/" + attendee.getOrder().getId() + "/" + attendee.getId() + "/note/new");
            }
        }
    }

    /**
     * Message shown to users who do not have the right allowing them to check in someone on the blacklist
     */
    public void showBlacklistWarningWindow() {
        WarningWindow window = new WarningWindow("Please send this person to the manager's booth right away");
        showWindow(window);
    }

    /**
     * Message shown to users who have the right allowing them to check in someone on the blacklist
     */
    public void showBlacklistConfirmationWindow() {
        WarningWindow window = new WarningWindow("This person matches a name on the attendee blacklist");
        showWindow(window);
    }


    @Override
    public void close() {
        navigateTo(OrderView.VIEW_NAME + "/" + orderId);
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
