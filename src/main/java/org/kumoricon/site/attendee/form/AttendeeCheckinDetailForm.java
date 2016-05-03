package org.kumoricon.site.attendee.form;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;

public class AttendeeCheckinDetailForm extends AttendeeDetailForm {
    // Subclass the attendee detail form and hide the "parental consent form received" checkbox
    // so that it can be displayed down near the bottom of the form instead
    public AttendeeCheckinDetailForm() {
        super();
    }

    @Override
    protected FormLayout buildAttendeeLeft() {
        FormLayout f = new FormLayout();
        f.setMargin(false);

        f.addComponent(firstName);
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
        f.addComponent(h);
        f.addComponent(zip);

        return f;
    }


    @Override
    protected FormLayout buildCheckedIn() {
        FormLayout f = new FormLayout();
        // Don't show the checked in checkbox on the check in form
        return f;
    }
}
