package org.kumoricon.site.attendee;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.search.AttendeeSearchPresenter;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

public abstract class CheckInView extends BaseView implements View {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "pre_reg_check_in";

    private TextArea attendeeInfo = new TextArea("Attendee");

    private Button btnCheckIn = new Button("Continue");
    private Button btnCancel = new Button("Cancel");
    private CheckBox informationVerified = new CheckBox("Information Verified");
    private CheckBox parentalConsentFormReceived = new CheckBox("Parental Consent Form Received");

    protected Attendee attendee;
    protected Integer orderId;
    protected AttendeeSearchPresenter handler;

    public CheckInView(AttendeeSearchPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        attendeeInfo.setEnabled(false);
        attendeeInfo.setWidth("500px");
        attendeeInfo.setHeight("300px");
        addComponents(attendeeInfo, informationVerified, parentalConsentFormReceived, buildButtons());

        informationVerified.addValueChangeListener(e -> enableButtons());
        parentalConsentFormReceived.addValueChangeListener(e -> enableButtons());
    }

    private void enableButtons() {
        if (informationVerified.getValue()) {
            if (attendee.isMinor()) {
                if (parentalConsentFormReceived.getValue()) {
                    btnCheckIn.setEnabled(true);
                }
            } else {
                btnCheckIn.setEnabled(true);
            }
        } else {
            btnCheckIn.setEnabled(false);
        }
    }

    protected VerticalLayout buildButtons() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));

        btnCheckIn.addClickListener((Button.ClickListener) clickEvent -> btnCheckInClicked());
        btnCancel.addClickListener(c -> close());

        btnCheckIn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnCheckIn.addStyleName(ValoTheme.BUTTON_PRIMARY);

        btnCheckIn.setEnabled(false);

        buttons.addComponents(btnCheckIn, btnCancel);
        return buttons;
    }

    protected void btnCheckInClicked() {
        handler.checkInAttendee(this, attendee);
        close();
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    public void showAttendee(Attendee attendee) {
        if (!attendee.getCheckedIn()) {
            this.attendee = attendee;
            attendeeInfo.setValue(dumpAttendeeToString(attendee));

            parentalConsentFormReceived.setEnabled(attendee.isMinor());
        } else {
            notify("Error: Attendee already checked in");
            close();
        }
    }


    protected String dumpAttendeeToString(Attendee attendee) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Name: %s %s\n", attendee.getFirstName(), attendee.getLastName()));
        if (!attendee.getNameIsLegalName()) {
            sb.append(String.format("Legal Name: %s %s\n\n", attendee.getLegalFirstName(), attendee.getLegalLastName()));
        }
        sb.append(String.format("Birthdate: %s (%s years old)\n", attendee.getBirthDate().format(DateTimeFormatter.ofPattern("M/d/Y")), attendee.getAge()));
        sb.append(String.format("Emergency Contact: \n\t%s \n\t%s\n", attendee.getEmergencyContactFullName(), attendee.getEmergencyContactPhone()));
        if (attendee.isMinor()) {
            sb.append(String.format("Parent Contact: \n\t%s \n\t%s\n", attendee.getParentFullName(), attendee.getParentPhone()));
        }

        return sb.toString();
    }

    public boolean parentalConsentFormReceived() {
        return parentalConsentFormReceived.getValue();
    }

    public boolean informationVerified() {
        return informationVerified.getValue();
    }
}
