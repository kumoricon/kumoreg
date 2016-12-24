package org.kumoricon.site.attendee.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.attendee.AddNoteHandler;


public class AddNoteWindow extends Window {

    private TextArea note = new TextArea("Note");

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");

    private AddNoteHandler handler;

    public AddNoteWindow(AddNoteHandler handler) {
        super("Add Note");
        this.handler = handler;
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

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public AddNoteHandler getHandler() { return handler; }
    public void setHandler(AddNoteHandler handler) { this.handler = handler; }
}
