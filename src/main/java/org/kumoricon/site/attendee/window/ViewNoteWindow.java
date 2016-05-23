package org.kumoricon.site.attendee.window;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.AttendeeHistory;


public class ViewNoteWindow extends Window {

    private TextField timeStamp = new TextField("Timestamp");
    private TextField user = new TextField("User");
    private TextArea note = new TextArea("Note");

    private Button close = new Button("Close");

    public ViewNoteWindow(AttendeeHistory attendeeHistory) {
        super("View Note");

        setIcon(FontAwesome.PENCIL);
        setModal(true);
        setClosable(true);
        center();
        setWidth(500, Unit.PIXELS);

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(timeStamp);
        verticalLayout.addComponent(user);
        verticalLayout.addComponent(note);
        timeStamp.setEnabled(false);
        user.setEnabled(false);
        note.setSizeFull();
        note.setEnabled(false);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(close);

        close.addClickListener((Button.ClickListener) clickEvent -> close());

        verticalLayout.addComponent(horizontalLayout);
        note.focus();
        setContent(verticalLayout);

        timeStamp.setValue(attendeeHistory.getTimestamp().toString());
        user.setValue(String.format("%s (%s %s)",
                attendeeHistory.getUser().getUsername(),
                attendeeHistory.getUser().getFirstName(),
                attendeeHistory.getUser().getLastName()));
        note.setValue(attendeeHistory.getMessage());
    }
}
