package org.kumoricon.view.attendee;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ServiceException;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.util.DateToLocalDateConverter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.kumoricon.util.FieldFactory.*;


public class AttendeeDetailForm extends GridLayout {
    protected TextField firstName = createTextField("First Name", 1);
    protected TextField lastName = createTextField("Last Name", 2);
    protected TextField badgeName = createTextField("Badge Name", 3);
    protected TextField badgeNumber = createTextField("Badge Number", 4);
    protected TextField phoneNumber = createTextField("Phone", 5);
    protected DateField birthDate = createDateField("", 6);
    protected TextField email = createTextField("Email", 7);
    protected TextField zip = createTextField("Zip", 8);
    protected Label age = new Label("");
    protected TextField emergencyContactFullName = createTextField("Full Name", 9);
    protected TextField emergencyContactPhone = createTextField("Phone", 10);
    protected TextField parentFullName = createTextField("Full Name", 11);
    protected TextField parentPhone = createTextField("Phone", 12);
    protected CheckBox parentIsEmergencyContact = createCheckBox("Parent is Emergency Contact", 13);
    protected CheckBox parentFormReceived = createCheckBox("Parental Consent Form Received", 14);
    protected NativeSelect badge = createNativeSelect("Pass Type", 15);
    protected TextField paidAmount = createTextField("Manual Price", 16);
    protected TextArea notes = createTextArea(null, 17);
    protected CheckBox checkedIn = createCheckBox("Attendee Checked In", 18);
    protected BeanItem<Attendee> attendeeBean;

    protected FieldGroup fieldGroup;

    public enum EditableFields {ALL, NOTES, NONE}

    public AttendeeDetailForm() {
        birthDate.setConverter(new DateToLocalDateConverter());
        setColumns(2);
        setRows(5);
        setMargin(true);
        setSpacing(true);
        setSizeFull();
        setColumnExpandRatio(0, 1);
        setColumnExpandRatio(1, 2);

        fieldGroup = new BeanFieldGroup<Attendee>(Attendee.class);
        fieldGroup.setFieldFactory(new CustomFieldGroupFieldFactory());
        fieldGroup.bindMemberFields(this);

        birthDate.setDateFormat("MM/dd/yyyy");
        birthDate.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            Integer currentAge = getAgeFromDate(birthDate.getValue());
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
        });


        badge.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            // Field is read only when values are being added
            if (!badge.isReadOnly() && !badge.isEmpty() && !birthDate.isEmpty()) {
                Badge thisBadge = (Badge) badge.getConvertedValue();
                try {
                    paidAmount.setValue(thisBadge.getCostForAge(Long.valueOf(getAgeFromDate(birthDate.getValue()))).toString());
                } catch(ServiceException e) {
                    Notification.show(e.getMessage());
                }
            }
        });


        addComponent(buildAttendeeLeft(), 0, 0);
        addComponent(buildAttendeeRight(), 1, 0);
        addComponent(buildEmergencyContactInfo(), 0, 1);
        addComponent(buildParentInfo(), 1, 1);
        addComponent(buildPassInfo(), 0, 2);
        addComponent(buildNotes(), 1, 2);
        addComponent(buildCheckedIn(), 0, 3);


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
        firstName.focus();
    }

    private void setMinorFieldsEnabled(boolean isEnabled) {
        parentFullName.setEnabled(isEnabled);
        parentPhone.setEnabled(isEnabled);
        parentIsEmergencyContact.setEnabled(isEnabled);
        parentFormReceived.setEnabled(isEnabled);
    }

    public void setManualPriceEnabled(boolean enabled) {
        paidAmount.setEnabled(enabled);
    }

    public Attendee getAttendee() {
        try {
            fieldGroup.commit();
            return attendeeBean.getBean();
        } catch (FieldGroup.CommitException e) {
            Notification.show(e.getMessage());
        }
        return null;
    }

    public void setAllFieldsEnabled(Boolean enabled) {
        fieldGroup.setEnabled(enabled);
    }


    public void setAllFieldsButCheckInDisabled() {
        setAllFieldsEnabled(false);
        parentFormReceived.setEnabled(true);
    }


    protected FormLayout buildAttendeeLeft() {
        FormLayout f = new FormLayout();
        f.setMargin(false);

        f.addComponent(firstName);
        f.addComponent(badgeName);
        f.addComponent(phoneNumber);
        f.addComponent(email);

        return f;
    }

    protected FormLayout buildAttendeeRight() {
        FormLayout f = new FormLayout();
        f.setMargin(false);

        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(false);
        h.setCaption("Birthdate");
        birthDate.setCaption(null);         // Can't set this when the object is created, need to remove caption
        h.addComponent(birthDate);
        h.addComponent(age);
        h.setComponentAlignment(birthDate, Alignment.MIDDLE_LEFT);
        h.setComponentAlignment(age, Alignment.MIDDLE_LEFT);
        f.addComponent(lastName);
        f.addComponent(badgeNumber);
        f.addComponent(h);
        f.addComponent(zip);

        return f;
    }

    protected FormLayout buildEmergencyContactInfo() {
        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.setCaption("Emergency Contact");

        f.addComponent(emergencyContactFullName);
        f.addComponent(emergencyContactPhone);
        return f;
    }

    protected FormLayout buildParentInfo() {
        FormLayout f = new FormLayout();
        f.setCaption("Parent Information");
        f.setMargin(false);

        HorizontalLayout checkBoxes = new HorizontalLayout();
        checkBoxes.setMargin(false);
        checkBoxes.setSpacing(true);
        checkBoxes.addComponent(parentIsEmergencyContact);
        checkBoxes.addComponent(parentFormReceived);

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

        f.addComponent(parentFullName);
        f.addComponent(parentPhone);
        f.addComponent(checkBoxes);

        return f;
    }

    protected FormLayout buildPassInfo() {
        FormLayout f = new FormLayout();
        f.setCaption("Badge Information");
        f.setMargin(false);
        f.addComponent(badge);
        badge.setNullSelectionAllowed(false);
        f.addComponent(paidAmount);
        return f;
    }

    protected FormLayout buildNotes() {
        FormLayout f = new FormLayout();
        f.setCaption("Notes");
        f.setMargin(false);
        f.setSizeFull();
        f.addComponent(notes);
        notes.setSizeFull();
        return f;
    }

    protected FormLayout buildCheckedIn() {
        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.addComponent(checkedIn);
        return f;
    }

    protected static Integer getAgeFromDate(Date birthDate) {
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
        badge.removeAllItems();
        badge.addItems(availableBadges);
    }

    public void selectFirstName() {
        firstName.selectAll();
    }

    public void setEditableFields(EditableFields fields) {
        switch (fields) {
            case ALL:
                fieldGroup.setEnabled(true);
                break;
            case NOTES:
                fieldGroup.setEnabled(false);
                notes.setEnabled(true);
                break;
            case NONE:
                fieldGroup.setEnabled(false);
        }
    }

    class CustomFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {
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
