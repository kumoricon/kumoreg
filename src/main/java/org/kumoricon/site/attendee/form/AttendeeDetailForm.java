package org.kumoricon.site.attendee.form;

import com.vaadin.ui.*;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.ServiceException;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.v7.ui.*;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;

import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.fieldconverter.DateToLocalDateConverter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static org.kumoricon.site.attendee.FieldFactory.*;


public class AttendeeDetailForm extends GridLayout {
    private TextField firstName = createNameField("First Name*", 1);
    private TextField lastName = createNameField("Last Name*", 2);
    private TextField legalFirstName = createNameField("Legal First Name", 3);
    private TextField legalLastName = createNameField("Legal Last Name", 4);
    private TextField fanName = createTextField("Fan Name", 5);
    private TextField badgeNumber = createTextField("Badge Number", 6);
    private TextField phoneNumber = createPhoneNumberField("Phone* (phone OR e-mail required)", 7);
    private DateField birthDate = createDateField("", 8);
    private TextField email = createTextField("Email*", 9);
    private TextField zip = createTextField("Zip", 10);
    private Label age = new Label("");
    private TextField emergencyContactFullName = createNameField("Emergency Contact Name*", 11);
    private TextField emergencyContactPhone = createPhoneNumberField("Emergency Contact Phone*", 12);
    private TextField parentFullName = createNameField("Parent Name", 13);
    private TextField parentPhone = createPhoneNumberField("Parent Phone", 14);
    private CheckBox parentIsEmergencyContact = createCheckBox("Parent is Emergency Contact", 15);
    private CheckBox parentFormReceived = createCheckBox("Parental Consent Form Received", 16);
    private NativeSelect badge = createNativeSelect("Pass Type*", 17);
    private TextField paidAmount = createTextField("Manual Price", 18);
    private CheckBox compedBadge = createCheckBox("Comped Badge", 19);
    private CheckBox checkedIn = createCheckBox("Attendee Checked In", 20);
    private BeanItem<Attendee> attendeeBean;

    private VerticalLayout historyLayout = new VerticalLayout();
    private FieldGroup fieldGroup;
    private DetailFormHandler handler;

    public void setParentFormReceivedVisible(boolean visible) {
        parentFormReceived.setVisible(visible);
    }

    public enum EditableFields {ALL, NONE}

    public AttendeeDetailForm(DetailFormHandler handler) {
        this.handler = handler;
        birthDate.setConverter(new DateToLocalDateConverter());
        setColumns(3);
        setRows(10);
        setMargin(new MarginInfo(false, true, true, true));
        setSpacing(true);
        setWidth("80%");
        setColumnExpandRatio(0, 0);
        setColumnExpandRatio(1, 0);
        setColumnExpandRatio(2, 1);

        fieldGroup = new BeanFieldGroup<>(Attendee.class);
        fieldGroup.setFieldFactory(new CustomFieldGroupFieldFactory());
        fieldGroup.bindMemberFields(this);

        birthDate.setDateFormat("MM/dd/yyyy");
        birthDate.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            Integer currentAge = getAgeFromDate(birthDate.getValue());
                if (currentAge != null) {
                    age.setValue(String.format("(%s years old)", currentAge));
                    setMinorFieldsEnabled(currentAge < 18);
                    try {
                        if (!badge.isEmpty()) {
                            Badge thisBadge = (Badge) badge.getConvertedValue();
                            paidAmount.setValue(thisBadge.getCostForAge(Long.valueOf(getAgeFromDate(birthDate.getValue()))).toString());
                        }
                    } catch(ServiceException e) {
                        Notification.show(e.getMessage());
                    }
                } else {
                    age.setValue("");
                    paidAmount.setValue(null);
                }
        });

        badge.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            // Field is read only when values are being added
            if (!badge.isReadOnly() && !badge.isEmpty() && !birthDate.isEmpty()) {
                Badge thisBadge = (Badge) badge.getConvertedValue();
                // Don't change price automatically if attendee is already checked in
                if (!checkedIn.getValue()) {
                    try {
                        paidAmount.setValue(thisBadge.getCostForAge(Long.valueOf(getAgeFromDate(birthDate.getValue()))).toString());
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

        parentIsEmergencyContact.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            if (parentIsEmergencyContact.getValue() && parentFullName.isEnabled()) {
                parentFullName.setValue(emergencyContactFullName.getValue());
                parentPhone.setValue(emergencyContactPhone.getValue());
            } else {
                parentFullName.setValue(null);
                parentPhone.setValue(null);
                parentFullName.focus();
            }
        });

        addComponent(parentFullName, 1, 5);
        addComponent(parentPhone, 1, 6);
        addComponent(parentIsEmergencyContact, 1, 7);

        addComponent(badge, 0, 8);
        badge.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badge.setItemCaptionPropertyId("name");
        badge.setNullSelectionAllowed(false);
        HorizontalLayout manualPrice = new HorizontalLayout();
        manualPrice.addComponent(paidAmount);
        manualPrice.addComponent(compedBadge);
        addComponent(manualPrice, 1, 8);

        compedBadge.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            if (Boolean.TRUE.equals(valueChangeEvent.getProperty().getValue())) {
                paidAmount.setValue("0");
            } else {
                try {
                    if (!badge.isEmpty()) {
                        Badge thisBadge = (Badge) badge.getConvertedValue();
                        paidAmount.setValue(thisBadge.getCostForAge(Long.valueOf(getAgeFromDate(birthDate.getValue()))).toString());
                    }
                } catch (ServiceException e) {
                    Notification.show(e.getMessage());
                }
            }
        });

        addComponent(parentFormReceived, 0, 9);
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

    public void show(Attendee attendee) {
        attendeeBean = new BeanItem<>(attendee);
        fieldGroup.setItemDataSource(attendeeBean);
        if (attendee.getPaidAmount() == null) {
            try {
                paidAmount.setValue(attendee.getBadge().getCostForAge(attendee.getAge()).toString());
            } catch(ServiceException e) {
                Notification.show(e.getMessage());
            }
        }
        badge.select(attendee.getBadge());
        setMinorFieldsEnabled(attendee.isMinor());
        checkedIn.setVisible(attendee.getCheckedIn()); // Hide checked in checkbox if attendee is not checked in
        parentFormReceived.setVisible(attendee.getCheckedIn()); // Hide parentFormReceived if attendee is not checked in
        badgeNumber.setEnabled(false);
        showHistory(attendee.getHistory());
        firstName.focus();
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
            f.setValidationVisible(isEnabled);
        }
    }

    public void setManualPriceEnabled(boolean enabled) {
        paidAmount.setEnabled(enabled);
        paidAmount.setValidationVisible(enabled);
        compedBadge.setEnabled(enabled);
        compedBadge.setValidationVisible(enabled);
    }

    public Attendee getAttendee() {
        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException e) {
            System.out.println(e);
            StringBuilder sb = new StringBuilder();
            for (Field f : e.getInvalidFields().keySet()) {
                sb.append(e.getInvalidFields().get(f).getMessage());
                sb.append("\n");
            }
            throw new RuntimeException(sb.toString());
        }
        return attendeeBean.getBean();
    }

    public void setAllFieldsButCheckInDisabled() {
        setEditableFields(EditableFields.NONE);
        parentFormReceived.setEnabled(true);
        parentFormReceived.setValidationVisible(true);
    }

    private static Integer getAgeFromDate(Date birthDate) {
        if (birthDate != null) {
            LocalDate bd = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Integer age = Period.between(bd, LocalDate.now()).getYears();
            if (age < 0) { age = 0; }
            return age;
        } else {
            return null;
        }
    }

    public void commit() throws FieldGroup.CommitException {
        fieldGroup.commit();
    }

    public void setAvailableBadges(List<Badge> availableBadges) {
        badge.setContainerDataSource(new BeanItemContainer<>(Badge.class, availableBadges));
    }

    public void selectFirstName() {
        firstName.selectAll();
    }

    /**
     * Enable or disable the fields specified by the EditableFields enum. Visible validation is also
     * disabled on disabled fields - if a user can't edit a field, they can't fix validation errors
     * @param fields EditableFields ENUM
     */
    public void setEditableFields(EditableFields fields) {
        switch (fields) {
            case ALL:
                for (Field field : fieldGroup.getFields()) {
                    AbstractField af = (AbstractField)field;
                    af.setEnabled(true);
                    af.setValidationVisible(true);
                }
                badgeNumber.setEnabled(false);
                break;
            case NONE:
                for (Field field : fieldGroup.getFields()) {
                    AbstractField af = (AbstractField)field;
                    af.setEnabled(false);
                    af.setValidationVisible(false);
                }
        }
    }

    static class CustomFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {
        @Override
        public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
            if (LocalDate.class.isAssignableFrom(dataType)) {
                T field;
                DateField dateField = new DateField();
                dateField.setConverter(new DateToLocalDateConverter());
                field = (T) dateField;
                return field;
            }
            return super.createField(dataType, fieldType);
        }

    }


}
