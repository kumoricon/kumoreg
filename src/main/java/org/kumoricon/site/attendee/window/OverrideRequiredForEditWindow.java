package org.kumoricon.site.attendee.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeeDetailView;
import org.kumoricon.site.attendee.OverrideEditHandler;
import org.kumoricon.site.attendee.search.AttendeeDetailWindow;


public class OverrideRequiredForEditWindow extends Window {

    Label requiredRightLabel = new Label("Override Required");
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");

    Button override = new Button("Override");
    Button cancel = new Button("Cancel");

    private OverrideEditHandler handler;
    private AttendeeDetailWindow parentWindow;
    private BaseView parentView;

    public OverrideRequiredForEditWindow(OverrideEditHandler handler, String requiredRight,
                                         AttendeeDetailView parentView) {
        super("Override For Edit Required");

        this.handler = handler;
        this.parentView = parentView;
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
                handler.overrideEditLogin(this, username.getValue(), password.getValue(), parentWindow));
        cancel.addClickListener((Button.ClickListener) clickEvent -> handler.overrideEditCancel(this));

        verticalLayout.addComponent(horizontalLayout);
        username.focus();
        setContent(verticalLayout);

        override.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        override.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }


    public OverrideRequiredForEditWindow(OverrideEditHandler handler, String requiredRight,
                                         AttendeeDetailWindow parentWindow) {
        super("Override For Edit Required");

        this.handler = handler;
        this.parentWindow = parentWindow;
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
                handler.overrideEditLogin(this, username.getValue(), password.getValue(), parentWindow));
        cancel.addClickListener((Button.ClickListener) clickEvent -> handler.overrideEditCancel(this));

        verticalLayout.addComponent(horizontalLayout);
        username.focus();
        setContent(verticalLayout);

        override.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        override.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }


    public OverrideEditHandler getHandler() { return handler; }
    public void setHandler(OverrideEditHandler handler) { this.handler = handler; }
}
