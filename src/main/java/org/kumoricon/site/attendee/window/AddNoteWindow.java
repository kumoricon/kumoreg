package org.kumoricon.site.attendee.window;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.site.attendee.AddNoteHandler;
import org.kumoricon.site.attendee.search.AttendeeDetailWindow;


public class AddNoteWindow extends Window {

    private TextArea note = new TextArea("Note");

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");

    private AddNoteHandler handler;
    private AttendeeDetailWindow parentWindow;

    public AddNoteWindow(AddNoteHandler handler, AttendeeDetailWindow parentWindow) {
        super("Add Note");

        this.handler = handler;
        this.parentWindow = parentWindow;
        setIcon(FontAwesome.PENCIL);
        setModal(true);
        setClosable(true);
        center();
        setWidth(500, Unit.PIXELS);

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(note);
        note.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(save);
        horizontalLayout.addComponent(cancel);

        save.addClickListener((Button.ClickListener) clickEvent ->
                handler.addNote(this, note.getValue()));
        cancel.addClickListener((Button.ClickListener) clickEvent -> handler.addNoteCancel(this));

        verticalLayout.addComponent(horizontalLayout);
        note.focus();
        setContent(verticalLayout);
    }

    public AttendeeDetailWindow getParentWindow() { return parentWindow; }

    public AddNoteHandler getHandler() { return handler; }
    public void setHandler(AddNoteHandler handler) { this.handler = handler; }
}
