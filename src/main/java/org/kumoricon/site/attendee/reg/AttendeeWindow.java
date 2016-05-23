package org.kumoricon.site.attendee.reg;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeCheckinDetailForm;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.attendee.window.ViewNoteWindow;


public class AttendeeWindow extends Window implements DetailFormHandler{

    AttendeeDetailForm attendeeDetailForm = new AttendeeCheckinDetailForm(this);
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete");
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

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(attendeeDetailForm);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(save);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(delete);

        save.setTabIndex(20);
        cancel.setTabIndex(21);

        save.addClickListener((Button.ClickListener) clickEvent -> {
            Attendee attendee = attendeeDetailForm.getAttendee();
            try {
                attendee.validate();
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

        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);

        attendeeDetailForm.selectFirstName();
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
}
