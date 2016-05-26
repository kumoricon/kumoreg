package org.kumoricon.site.attendee.form;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ServiceException;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.fieldconverter.DateToLocalDateConverter;
import org.kumoricon.site.fieldconverter.UserToStringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.kumoricon.site.attendee.FieldFactory.*;


public class AttendeeDetailForm extends GridLayout {
    protected TextField firstName = createTextField("First Name", 1);
    protected TextField lastName = createTextField("Last Name", 2);
    protected TextField badgeName = createTextField("Badge Name", 3);
    protected TextField badgeNumber = createTextField("Badge Number", 4);
    protected TextField phoneNumber = createPhoneNumberField("Phone", 5);
    protected DateField birthDate = createDateField("", 6);
    protected TextField email = createTextField("Email", 7);
    protected TextField zip = createTextField("Zip", 8);
    protected Label age = new Label("");
    protected TextField emergencyContactFullName = createTextField("Full Name", 9);
    protected TextField emergencyContactPhone = createPhoneNumberField("Phone", 10);
    protected TextField parentFullName = createTextField("Full Name", 11);
    protected TextField parentPhone = createPhoneNumberField("Phone", 12);
    protected CheckBox parentIsEmergencyContact = createCheckBox("Parent is Emergency Contact", 13);
    protected CheckBox parentFormReceived = createCheckBox("Parental Consent Form Received", 14);
    protected NativeSelect badge = createNativeSelect("Pass Type", 15);
    protected TextField paidAmount = createTextField("Manual Price", 16);
    protected TextArea notes = createTextArea(null, 17);
    protected CheckBox checkedIn = createCheckBox("Attendee Checked In", 18);
    protected BeanItem<Attendee> attendeeBean;
    protected Table history = new Table();
    protected FieldGroup fieldGroup;
    private DetailFormHandler handler;

    public enum EditableFields {ALL, NOTES, NONE}

    public AttendeeDetailForm(DetailFormHandler handler) {
        this.handler = handler;
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
        addComponent(buildNotes(), 1, 2, 1, 3);
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
        showHistory(attendee.getHistory());
        firstName.focus();
    }

    public void showHistory(List<AttendeeHistory> attendeeHistories) {
        if (attendeeHistories != null) {
            BeanItemContainer<AttendeeHistory> historyItems = new BeanItemContainer<AttendeeHistory>(AttendeeHistory.class);
            history.setContainerDataSource(historyItems);
            history.setVisibleColumns("timestamp", "user", "message");
            history.setColumnHeaders("Time", "User", "Message");
            history.setConverter("user", new UserToStringConverter());
            history.setConverter("timestamp", new StringToDateConverter(){
                @Override
                public DateFormat getFormat(Locale locale){
                    return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                }
            });
            historyItems.addAll(attendeeHistories);
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
    }

    public Attendee getAttendee() {
        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException e) {
            System.out.println(e);
        }
        return attendeeBean.getBean();
    }

    public void setAllFieldsButCheckInDisabled() {
        setEditableFields(EditableFields.NONE);
        parentFormReceived.setEnabled(true);
        parentFormReceived.setValidationVisible(true);
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
        badge.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badge.setItemCaptionPropertyId("name");
        badge.setNullSelectionAllowed(false);
        f.addComponent(paidAmount);
        return f;
    }

    protected FormLayout buildNotes() {
        FormLayout f = new FormLayout();
        f.setCaption("History");
        f.setMargin(false);
        f.setSizeFull();
        f.addComponent(history);
        history.setSizeFull();
        history.setEditable(false);
        history.setNullSelectionAllowed(true);
        history.setPageLength(5);
        history.setColumnExpandRatio("message", 1.0f);
        history.addItemClickListener((ItemClickEvent.ItemClickListener) event -> {
            handler.showAttendeeHistory((AttendeeHistory) event.getItemId());
        });
        return f;
    }

    protected FormLayout buildCheckedIn() {
        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.addComponent(parentFormReceived);
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
        badge.setContainerDataSource(new BeanItemContainer<Badge>(Badge.class, availableBadges));
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
                break;
            case NOTES:
                for (Field field : fieldGroup.getFields()) {
                    AbstractField af = (AbstractField)field;
                    af.setEnabled(false);
                    af.setValidationVisible(false);
                }
                notes.setEnabled(true);
                notes.setValidationVisible(true);
                break;
            case NONE:
                for (Field field : fieldGroup.getFields()) {
                    AbstractField af = (AbstractField)field;
                    af.setEnabled(false);
                    af.setValidationVisible(false);
                }
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
