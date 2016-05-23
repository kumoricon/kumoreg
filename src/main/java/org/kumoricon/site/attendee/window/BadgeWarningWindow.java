package org.kumoricon.site.attendee.window;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.site.attendee.prereg.PreRegPresenter;
import org.kumoricon.model.attendee.Attendee;


public class BadgeWarningWindow extends Window {
    Label lblMessage = new Label("");
    Button btnAbort = new Button("Abort");
    private PreRegPresenter handler;

    public BadgeWarningWindow(PreRegPresenter preRegCheckInPresenter, Attendee attendee) {
        super("Warning");
        this.handler = preRegCheckInPresenter;
        setIcon(FontAwesome.WARNING);
        setModal(true);
        center();

        String warning = attendee.getFirstName() + " " + attendee.getLastName() + ": " +
                attendee.getBadge().getWarningMessage();

        lblMessage.setValue(warning);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(lblMessage);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(btnAbort);
        btnAbort.focus();
        btnAbort.addClickListener((Button.ClickListener) clickEvent -> handler.abortCheckIn());
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);
    }


    public PreRegPresenter getHandler() { return handler; }
    public void setHandler(PreRegPresenter handler) { this.handler = handler; }
}