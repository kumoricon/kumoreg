package org.kumoricon.site.attendee.reg;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.site.attendee.AddNoteHandler;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.AddNoteWindow;
import org.kumoricon.site.attendee.window.ViewNoteWindow;

public class AttendeeWindow extends Window implements DetailFormHandler, AddNoteHandler {

    AttendeeDetailForm attendeeDetailForm = new AttendeeDetailForm(this);
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete");
    Button addNote = new Button("Add Note");
    OrderView parentView;

    private OrderPresenter handler;

    public AttendeeWindow(OrderView parentView, OrderPresenter orderPresenter) {
        super("Attendee");
        this.parentView = parentView;
        this.handler = orderPresenter;
        setIcon(FontAwesome.USER);
        center();
        setModal(true);
        setWidth(1100, Unit.PIXELS);
        setHeight(800, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.addComponent(attendeeDetailForm);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(save);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(delete);
        horizontalLayout.addComponent(addNote);

        save.setTabIndex(20);
        cancel.setTabIndex(21);
        delete.setTabIndex(22);
        addNote.setTabIndex(23);

        save.addClickListener((Button.ClickListener) clickEvent -> {
            Attendee attendee = attendeeDetailForm.getAttendee();
            try {
                handler.validate(attendee);
                handler.addAttendeeToOrder(parentView, attendee);
                close();
            } catch (ValueException e) {
                parentView.notifyError(e.getMessage());
            }
        });

        cancel.addClickListener((Button.ClickListener) clickEvent -> {
            close();
        });

        delete.addClickListener((Button.ClickListener) clickEvent -> {
            handler.removeAttendeeFromOrder(parentView, attendeeDetailForm.getAttendee());
            close();
        });

        addNote.addClickListener((Button.ClickListener) clickEvent -> showAddNoteWindow());

        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);

        attendeeDetailForm.selectFirstName();

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public Attendee getAttendee() {
        return attendeeDetailForm.getAttendee();
    }


    public OrderPresenter getHandler() { return handler; }
    public void setHandler(OrderPresenter handler) { this.handler = handler; }

    public AttendeeDetailForm getDetailForm() {
        return attendeeDetailForm;
    }

    @Override
    public void showAttendeeHistory(AttendeeHistory attendeeHistory) {
        ViewNoteWindow window = new ViewNoteWindow(attendeeHistory);
        parentView.showWindow(window);
    }

    private void showAddNoteWindow() {
        AddNoteWindow window = new AddNoteWindow(this);
        parentView.showWindow(window);
    }

    @Override
    public void addNote(AddNoteWindow window, String message) {
        window.close();
        Attendee attendee = attendeeDetailForm.getAttendee();
        attendee.addHistoryEntry(parentView.getCurrentUser(), message);
        attendeeDetailForm.show(attendee);
    }

    @Override
    public void addNoteCancel(AddNoteWindow window) {
        window.close();
    }
}
