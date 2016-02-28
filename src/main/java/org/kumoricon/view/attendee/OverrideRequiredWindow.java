package org.kumoricon.view.attendee;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.presenter.attendee.OverrideHandler;

public class OverrideRequiredWindow extends Window {

    Label requiredRightLabel = new Label("Override Required");
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");

    Button override = new Button("Override");
    Button cancel = new Button("Cancel");

    private OverrideHandler handler;

    public OverrideRequiredWindow(OverrideHandler handler, String requiredRight) {
        super("Override Required");

        this.handler = handler;
        setIcon(FontAwesome.LOCK);
        setModal(true);
        setClosable(true);
        center();
        setWidth(500, Unit.PIXELS);

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        requiredRightLabel.setValue("Override required: " + requiredRight);
        verticalLayout.addComponent(requiredRightLabel);
        verticalLayout.addComponent(username);
        verticalLayout.addComponent(password);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(override);
        horizontalLayout.addComponent(cancel);

        override.addClickListener((Button.ClickListener) clickEvent ->
                handler.overrideLogin(username.getValue(), password.getValue()));
        cancel.addClickListener((Button.ClickListener) clickEvent -> handler.overrideCancel());

        verticalLayout.addComponent(horizontalLayout);
        username.focus();
        setContent(verticalLayout);
    }


    public OverrideHandler getHandler() { return handler; }
    public void setHandler(OverrideHandler handler) { this.handler = handler; }
}
