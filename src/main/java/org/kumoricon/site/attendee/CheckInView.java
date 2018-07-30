package org.kumoricon.site.attendee;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.attendee.search.AttendeeSearchPresenter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class CheckInView extends BaseGridView implements View {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "pre_reg_check_in";

    private TextArea attendeeInfo = new TextArea("Attendee");

    private Button btnCheckIn = new Button("Continue");
    private Button btnEdit = new Button("Edit");
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
        setRows(4);
        setColumns(5);
        setRowExpandRatio(3, 10.0f);
        setColumnExpandRatio(0, 10.0f);
        setColumnExpandRatio(3, 2.0f);
        setColumnExpandRatio(4, 10.0f);

        attendeeInfo.setEnabled(false);
        attendeeInfo.setWidth("500px");
        attendeeInfo.setHeight("300px");

        addComponent(attendeeInfo, 1, 0, 1, 2);
        addComponent(informationVerified, 2, 0);
        addComponent(parentalConsentFormReceived, 2, 1);

        btnCheckIn.addClickListener((Button.ClickListener) clickEvent -> btnCheckInClicked());
        btnEdit.addClickListener((Button.ClickListener) clickEvent -> btnEditClicked());
        btnCancel.addClickListener(c -> close());

        btnCheckIn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnCheckIn.addStyleName(ValoTheme.BUTTON_PRIMARY);

        btnCheckIn.setEnabled(false);

        addComponent(btnCheckIn, 3, 0);
        addComponent(btnEdit, 3, 1);
        addComponent(btnCancel, 3, 2);

        informationVerified.addValueChangeListener(e -> enableButtons());
        parentalConsentFormReceived.addValueChangeListener(e -> enableButtons());
    }

    private void enableButtons() {
        if (attendeeIsIncomplete(attendee)) {
            btnCheckIn.setEnabled(false);
            return;
        }

        if (informationVerified.getValue()) {
            if (attendee.isMinor()) {
                if (parentalConsentFormReceived.getValue()) {
                    btnCheckIn.setEnabled(true);
                } else {
                    btnCheckIn.setEnabled(false);
                }
            } else {
                btnCheckIn.setEnabled(true);
            }
        } else {
            btnCheckIn.setEnabled(false);
        }
    }

    protected void btnCheckInClicked() {
        handler.checkInAttendee(this, attendee);
        close();
    }

    protected void btnEditClicked() {
        throw new RuntimeException("Override btnEditClicked() in view");
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
        sb.append(String.format("Birthdate: %s (%s years old)\n", attendee.getBirthDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")), attendee.getAge()));
        sb.append(String.format("Emergency Contact: \n\t%s \n\t%s\n", attendee.getEmergencyContactFullName(), attendee.getEmergencyContactPhone()));
        if (attendee.isMinor()) {
            sb.append(String.format("Parent Contact: \n\t%s \n\t%s\n", attendee.getParentFullName(), attendee.getParentPhone()));
        }

        if (attendeeIsIncomplete(attendee)) {
            sb.append("\n** MISSING EMERGENCY CONTACT OR BIRTHDATE **");
        }

        return sb.toString();
    }

    private static boolean attendeeIsIncomplete(Attendee attendee) {
        // Make sure emergency contact and birthdate exist. TODO: handle missing information more
        // gracefully. Right now 1/1/1900 is kind of a "magic number" in that it was included
        // in the import data as a default.
        if (attendee.getEmergencyContactFullName().trim().isEmpty() ||
                attendee.getEmergencyContactPhone().trim().isEmpty() ||
                attendee.getBirthDate().equals(LocalDate.of(1900, 1, 1))) {
            return true;
        } else {
            return false;
        }

    }

    public boolean parentalConsentFormReceived() {
        return parentalConsentFormReceived.getValue();
    }

    public boolean informationVerified() {
        return informationVerified.getValue();
    }
}
