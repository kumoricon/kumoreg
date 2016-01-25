package org.kumoricon.view.attendee;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.attendee.PreRegSearchPresenter;

public class BadgeWarningWindow extends Window {
    Label lblMessage = new Label("");
    Button btnContinue = new Button("Continue");
    Button btnAbort = new Button("Abort");
    private PreRegSearchPresenter handler;

    public BadgeWarningWindow(PreRegSearchPresenter preRegCheckInPresenter, Attendee attendee) {
        super("Warning");
        this.handler = preRegCheckInPresenter;
        setIcon(FontAwesome.WARNING);
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
        horizontalLayout.addComponent(btnContinue);
        btnContinue.focus();
        horizontalLayout.addComponent(btnAbort);

        btnContinue.addClickListener((Button.ClickListener) clickEvent -> handler.continueCheckIn(attendee));
        btnAbort.addClickListener((Button.ClickListener) clickEvent -> handler.abortCheckIn());
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);
    }


    public PreRegSearchPresenter getHandler() { return handler; }
    public void setHandler(PreRegSearchPresenter handler) { this.handler = handler; }
}
