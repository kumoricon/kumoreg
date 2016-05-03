package org.kumoricon.site.attendee.form;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;

public class AttendeePreRegDetailForm extends AttendeeDetailForm {
    // Subclass the attendee detail form and hide the "parental consent form received" checkbox
    // so that it can be displayed down near the bottom of the form instead
    public AttendeePreRegDetailForm() {
        super();
    }

    @Override
    protected FormLayout buildParentInfo() {
        FormLayout f = new FormLayout();
        f.setCaption("Parent Information");
        f.setMargin(false);

        HorizontalLayout checkBoxes = new HorizontalLayout();
        checkBoxes.setMargin(false);
        checkBoxes.setSpacing(true);
        checkBoxes.addComponent(parentIsEmergencyContact);

        f.addComponent(parentFullName);
        f.addComponent(parentPhone);
        f.addComponent(checkBoxes);

        return f;
    }

    @Override
    protected FormLayout buildCheckedIn() {
        FormLayout f = new FormLayout();
        // Don't show the checked in checkbox on the check in form
        return f;
    }
}
