package org.kumoricon.site;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;
import org.kumoricon.site.attendee.FieldFactory;


public class NewPasswordWindow extends Window {

    PasswordField password = FieldFactory.createPasswordField("New Password", 1);
    PasswordField verifyPassword = FieldFactory.createPasswordField("Verity Password", 2);
    Button save = new Button("Save");

    private LoginPresenter handler;
    private LoginView parentView;

    public NewPasswordWindow(LoginView parentView, LoginPresenter loginPresenter) {
        super("Set Password");
        this.handler = loginPresenter;
        this.parentView = parentView;
        setIcon(FontAwesome.LOCK);
        setModal(true);
        center();
        setClosable(false);

        setWidth(400, Unit.PIXELS);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(true);
        formLayout.setSpacing(true);

        formLayout.addComponent(password);
        formLayout.addComponent(verifyPassword);
        formLayout.addComponent(save);

        password.focus();

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener((Button.ClickListener) clickEvent -> {
            if (password.isEmpty()) {
                parentView.notify("Password can not be empty");
                password.focus();
            } else if (password.getValue().toLowerCase().equals("password")) {
                parentView.notify("Password can't be \"password\"");
                password.selectAll();
            } else if (!password.getValue().equals(verifyPassword.getValue())) {
                parentView.notify("Password and Verification do not match");
                verifyPassword.selectAll();
            } else {
                handler.setNewPassword(this.parentView, password.getValue());
                close();
            }
        });

        setContent(formLayout);
    }

    public LoginPresenter getHandler() { return handler; }
    public void setHandler(LoginPresenter handler) { this.handler = handler; }

}
