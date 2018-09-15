package org.kumoricon.site.attendee.form;

import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import com.vaadin.server.ServiceException;
import com.vaadin.shared.ui.MarginInfo;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

import static org.kumoricon.site.attendee.FieldFactory8.*;

public class AttendeeDetailForm extends GridLayout {
    private static final DateTimeFormatter DEFAULT = DateTimeFormatter.ofPattern("MMddyyyy");
    private static final DateTimeFormatter DASHES = DateTimeFormatter.ofPattern("M-d-yyyy");
    private static final DateTimeFormatter SLASHES = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final DateTimeFormatter TWO_DIGIT_YEAR =
            new DateTimeFormatterBuilder()
                    .appendPattern("MMdd")
                    .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 4, LocalDate.now().minusYears(99))
                    .toFormatter();

    private TextField firstName = createNameField("First Name*", 1);
    private TextField lastName = createNameField("Last Name*", 2);
    private TextField legalFirstName = createNameField("Legal First Name", 3);
    private TextField legalLastName = createNameField("Legal Last Name", 4);
    private CheckBox nameIsLegalName = createCheckBox("Name is legal name", 5);
    private TextField fanName = createTextField("Fan Name", 5);
    private TextField badgeNumber = createTextField("Badge Number", 6);
    private TextField phoneNumber = createPhoneNumberField("Phone*", 7);
    private TextField birthDate = createBirthdayField("", 8);
    private TextField email = createTextField("Email*", 9);
    private TextField zip = createTextField("Zip", 10);
    private Label age = new Label("");
    private TextField emergencyContactFullName = createNameField("Emergency Contact Name*", 11);
    private TextField emergencyContactPhone = createPhoneNumberField("Emergency Contact Phone*", 12);
    private CheckBox parentIsEmergencyContact = createCheckBox("Parent is Emergency Contact", 13);
    private TextField parentFullName = createNameField("Parent Name", 14);
    private TextField parentPhone = createPhoneNumberField("Parent Phone", 15);
    private CheckBox parentFormReceived = createCheckBox("Parental Consent Form Received", 16);
    private NativeSelect<Badge> badge = createBadgeSelect("Badge Type", 17);
    private TextField paidAmount = createTextField("Manual Price", 18);
    private CheckBox compedBadge = createCheckBox("Comped Badge", 19);
    private CheckBox checkedIn = createCheckBox("Attendee Checked In", 20);

    Binder<Attendee> binder = new Binder<>();

    private VerticalLayout historyLayout = new VerticalLayout();
    private List<Badge> availableBadges;

    public void setParentFormReceivedVisible(boolean visible) {
        parentFormReceived.setVisible(visible);
    }

    public enum EditableFields {ALL, NONE}

    public AttendeeDetailForm() {
        setColumns(3);
        setRows(10);
        setMargin(new MarginInfo(false, true, true, true));
        setSpacing(true);
        setWidth("80%");
        setColumnExpandRatio(0, 0);
        setColumnExpandRatio(1, 0);
        setColumnExpandRatio(2, 1);

        binder.bind(firstName, Attendee::getFirstName, Attendee::setFirstName);
        binder.bind(lastName, Attendee::getLastName, Attendee::setLastName);
        binder.bind(legalFirstName, Attendee::getLegalFirstName, Attendee::setLegalFirstName);
        binder.bind(legalLastName, Attendee::getLegalLastName, Attendee::setLegalLastName);
        binder.bind(nameIsLegalName, Attendee::getNameIsLegalName, Attendee::setNameIsLegalName);
        binder.bind(fanName, Attendee::getFanName, Attendee::setFanName);
        binder.bind(badgeNumber, Attendee::getBadgeNumber, Attendee::setBadgeNumber);
        binder.bind(phoneNumber, Attendee::getPhoneNumber, Attendee::setPhoneNumber);
        binder.forField(birthDate)
                .withConverter(new StringToLocalDateConverter("Must enter a date"))
                .bind(Attendee::getBirthDate, Attendee::setBirthDate);
        binder.bind(email, Attendee::getEmail, Attendee::setEmail);
        binder.bind(zip, Attendee::getZip, Attendee::setZip);

        binder.bind(emergencyContactFullName, Attendee::getEmergencyContactFullName, Attendee::setEmergencyContactFullName);
        binder.bind(emergencyContactPhone, Attendee::getEmergencyContactPhone, Attendee::setEmergencyContactPhone);
        binder.bind(parentFullName, Attendee::getParentFullName, Attendee::setParentFullName);
        binder.bind(parentPhone, Attendee::getParentPhone, Attendee::setParentPhone);
        binder.bind(parentIsEmergencyContact, Attendee::getParentIsEmergencyContact, Attendee::setParentIsEmergencyContact);
        binder.bind(parentFormReceived, Attendee::getParentFormReceived, Attendee::setParentFormReceived);

        binder.bind(badge, Attendee::getBadge, Attendee::setBadge);
        binder.bind(paidAmount,
                attendee -> attendee.getPaidAmount().toString(),
                (attendee, formValue) -> attendee.setPaidAmount(new BigDecimal(formValue)));
        binder.bind(compedBadge, Attendee::getCompedBadge, Attendee::setCompedBadge);
        binder.bind(checkedIn, Attendee::getCheckedIn, Attendee::setCheckedIn);


        birthDate.addValueChangeListener(valueChangeEvent -> {
            LocalDate parsedDate = parseDate(valueChangeEvent.getValue());
            if (parsedDate != null) {
                String formattedDate = parsedDate.format(SLASHES);
                if (!formattedDate.equals(valueChangeEvent.getValue())) {
                    birthDate.setValue(parsedDate.format(SLASHES));
                    return;
                }
            }

            Integer currentAge = getAgeFromDate(parseDate(birthDate.getValue()));
                if (currentAge != null) {
                    age.setValue(String.format("(%s years old)", currentAge));
                    setMinorFieldsEnabled(currentAge < 18);
                    try {
                        if (!badge.isEmpty()) {
                            Badge thisBadge = badge.getValue();
                            LocalDate birthday = parseDate(birthDate.getValue());
                            paidAmount.setValue(thisBadge.getCostForAge(getAgeFromDate(birthday)).toString());
                        }
                    } catch(ServiceException e) {
                        Notification.show(e.getMessage());
                    }
                } else {
                    age.setValue("");
                    paidAmount.clear();
                }
        });


        badge.addValueChangeListener(valueChangeEvent -> {
            // Field is read only when values are being added
            if (!badge.isReadOnly() && !badge.isEmpty() && !birthDate.isEmpty()) {
                Badge thisBadge = badge.getValue();
                // Don't change price automatically if attendee is already checked in
                if (!checkedIn.getValue()) {
                    try {
                        LocalDate birthday = parseDate(birthDate.getValue());
                        paidAmount.setValue(thisBadge.getCostForAge(getAgeFromDate(birthday)).toString());
                    } catch(ServiceException e) {
                        Notification.show(e.getMessage());
                    }
                }
            }
        });

        addComponent(firstName, 0, 0);
        addComponent(lastName, 1, 0);
        addComponent(legalFirstName, 0, 1);
        addComponent(legalLastName, 1, 1);
        addComponent(fanName, 0, 2);
        addComponent(badgeNumber, 1, 2);
        badgeNumber.setEnabled(false);
        addComponent(phoneNumber, 0, 3);
        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(false);
        h.setCaption("Birthdate*");
        birthDate.setCaption(null);         // Can't set this when the object is created, need to remove caption
        h.addComponent(birthDate);
        h.addComponent(age);
        h.setComponentAlignment(birthDate, Alignment.MIDDLE_LEFT);
        h.setComponentAlignment(age, Alignment.MIDDLE_LEFT);

        addComponent(h, 1, 3);

        addComponent(email, 0, 4);
        addComponent(zip, 1, 4);

        addComponent(emergencyContactFullName, 0, 5);
        addComponent(emergencyContactPhone, 0, 6);
        addComponent(parentIsEmergencyContact, 0, 7);

        parentIsEmergencyContact.addValueChangeListener(valueChangeEvent -> {
            if (parentIsEmergencyContact.getValue() && parentFullName.isEnabled()) {

                // Of the parent fields and emergency fields, copy values from the field that's filled in to
                // the field that isn't. Added this because some people would fill in the parent field, then
                // click the parent is emergency contact button, which is backwards from the workflow I was
                // expecting
                if (emergencyContactFullName.getValue().trim().isEmpty() && !parentFullName.getValue().trim().isEmpty()) {
                    emergencyContactFullName.setValue(parentFullName.getValue());
                } else {
                    parentFullName.setValue(emergencyContactFullName.getValue());
                }

                if (emergencyContactPhone.getValue().trim().isEmpty() && !parentPhone.getValue().trim().isEmpty()) {
                    emergencyContactPhone.setValue(parentPhone.getValue());
                } else {
                    parentPhone.setValue(emergencyContactPhone.getValue());
                }
            } else {
                parentFullName.focus();
            }
        });

        addComponent(parentFullName, 1, 5);
        addComponent(parentPhone, 1, 6);
        addComponent(parentFormReceived, 1, 7);

        addComponent(badge, 0, 8);
        badge.setItemCaptionGenerator(Badge::getName);
        badge.setEmptySelectionAllowed(false);
        HorizontalLayout manualPrice = new HorizontalLayout();
        manualPrice.addComponent(paidAmount);
        manualPrice.addComponent(compedBadge);
        addComponent(manualPrice, 1, 8);

        compedBadge.addValueChangeListener(valueChangeEvent -> {
            if (Boolean.TRUE.equals(valueChangeEvent.getValue())) {
                paidAmount.setValue("0");
            } else {
                try {
                    if (!badge.isEmpty()) {
                        Badge thisBadge = badge.getValue();
                        LocalDate birthday = parseDate(birthDate.getValue());
                        paidAmount.setValue(thisBadge.getCostForAge(getAgeFromDate(birthday)).toString());
                    }
                } catch (ServiceException e) {
                    Notification.show(e.getMessage());
                }
            }
        });

        addComponent(checkedIn, 1, 9);


        Panel hist = new Panel("Notes/History");
        hist.setWidth("100%");
        hist.setHeight("100%");


        historyLayout.setMargin(true);
        historyLayout.setMargin(new MarginInfo(false, true, false, true));
        historyLayout.setSpacing(true);
        hist.setContent(historyLayout);
        addComponent(hist, 2, 0, 2, 6);
    }

    public void setOpitionallyRequiredFieldNames(boolean requirePhoneAndEmail) {
        if (requirePhoneAndEmail) {
            email.setCaption("Email*");
            phoneNumber.setCaption("Phone*");
        } else {
            email.setCaption("Email");
            phoneNumber.setCaption("Phone");
        }
    }

    public void hideLegalNameFields() {
        removeComponent(legalFirstName);

        addComponent(nameIsLegalName, 0, 1);
        nameIsLegalName.setVisible(false);

        nameIsLegalName.setVisible(true);
        nameIsLegalName.setValue(true);
        legalFirstName.setVisible(false);
        legalLastName.setVisible(false);
        nameIsLegalName.addValueChangeListener(e -> {
            removeComponent(nameIsLegalName);
            addComponent(legalFirstName, 0, 1);
            legalFirstName.setVisible(true);
            legalLastName.setVisible(true);
        });
    }

    public void hideFanNameField() {
        fanName.setVisible(false);
    }

    public void show(Attendee attendee) {
        binder.setBean(attendee);

        if (attendee.getPaidAmount() == null) {
            try {
                paidAmount.setValue(attendee.getBadge().getCostForAge(attendee.getAge()).toString());
            } catch(ServiceException e) {
                Notification.show(e.getMessage());
            }
        }
        if (attendee.getNameIsLegalName()) {
            hideLegalNameFields();
        }

        setMinorFieldsEnabled(attendee.isMinor());
        checkedIn.setVisible(attendee.getCheckedIn()); // Hide checked in checkbox if attendee is not checked in
        badgeNumber.setEnabled(false);
        showHistory(attendee.getHistory());
        firstName.focus();
        firstName.selectAll();

        // If the attendee doesn't have a badge type and id (hasn't been saved), and there is only
        // one available badge type, auto-select it
        if (attendee.getBadge() == null && attendee.getId() == null) {
            if (availableBadges != null && availableBadges.size() == 1) {
                badge.setSelectedItem(availableBadges.get(0));
            }
        }
    }

    public void focusFirstName() {
        firstName.selectAll();
    }

    public void showHistory(Set<AttendeeHistory> attendeeHistories) {
        historyLayout.removeAllComponents();
        Component c = null;
        if (attendeeHistories != null) {
            List<AttendeeHistory> sortedHistories = new ArrayList<>(attendeeHistories);
            sortedHistories.sort(Comparator.comparing(AttendeeHistory::getTimestamp));
            for (AttendeeHistory history : sortedHistories) {
                c = new HistoryEntryLayout(history);
                historyLayout.addComponent(c);
            }
        }
        if (c != null) {
            historyLayout.setExpandRatio(c, 1);
        }
    }

    public void setMinorFieldsEnabled(boolean isEnabled) {
        AbstractField[] fields = {parentFullName, parentPhone, parentIsEmergencyContact, parentFormReceived};
        for (AbstractField f : fields) {
            f.setEnabled(isEnabled);
        }
    }

    public void setManualPriceEnabled(boolean enabled) {
        paidAmount.setEnabled(enabled);
        compedBadge.setEnabled(enabled);
    }

    public Attendee getAttendee() {
        return binder.getBean();
    }

    public void setAllFieldsButCheckInDisabled() {
        setEditableFields(EditableFields.NONE);
        parentFormReceived.setEnabled(true);
//        parentFormReceived.setValidationVisible(true);
    }

    private static Integer getAgeFromDate(LocalDate birthDate) {
        if (birthDate != null) {
            Integer age = Period.between(birthDate, LocalDate.now(ZoneId.of("America/Los_Angeles"))).getYears();
            if (age < 0) { age = 0; }
            return age;
        } else {
            return null;
        }
    }

    private static LocalDate parseDate(String date) {
        if (date == null) return null;
        LocalDate birthday;
        try {
            birthday = LocalDate.parse(date, DEFAULT);
        } catch (DateTimeParseException ignored) {
            try {
                birthday = LocalDate.parse(date, SLASHES);
            } catch (DateTimeParseException ignored2) {
                try {
                    birthday = LocalDate.parse(date, DASHES);
                } catch (DateTimeParseException ignored3) {
                    try {
                        birthday = LocalDate.parse(date, TWO_DIGIT_YEAR);
                    } catch (DateTimeParseException ignored4) {
                        return null;
                    }
                }
            }
        }
        return birthday;

    }

    public void commit() throws Exception {
//        fieldGroup.commit();
    }

    public void setAvailableBadges(List<Badge> availableBadges) {
        this.availableBadges = availableBadges;
        badge.setItems(availableBadges);
    }

    /**
     * Enable or disable the fields specified by the EditableFields enum. Visible validation is also
     * disabled on disabled fields - if a user can't edit a field, they can't fix validation errors
     * @param fields EditableFields ENUM
     */
    public void setEditableFields(EditableFields fields) {

        List<AbstractField> allFields = Arrays.asList(firstName, lastName, legalFirstName, legalLastName,
                nameIsLegalName, fanName, badgeNumber, phoneNumber, birthDate, email, zip, emergencyContactFullName,
                emergencyContactPhone, parentFormReceived, parentFullName, parentPhone, paidAmount,
                compedBadge, checkedIn);
        switch (fields) {
            case ALL:
                for (AbstractField field : allFields) {
                    field.setEnabled(true);
//                    field.setValidationVisible(true);
                }
                badgeNumber.setEnabled(false);
                badge.setEnabled(true);
                break;
            case NONE:
                for (AbstractField field : allFields) {
                    field.setEnabled(false);
                }
                badgeNumber.setEnabled(false);
                badge.setEnabled(false);
        }
    }
}
