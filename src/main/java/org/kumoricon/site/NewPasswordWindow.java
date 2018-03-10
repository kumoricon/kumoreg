package org.kumoricon.site;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.attendee.FieldFactory8;


public class NewPasswordWindow extends Window {

    private PasswordField password = FieldFactory8.createPasswordField("New Password", 1);
    private PasswordField verifyPassword = FieldFactory8.createPasswordField("Verity Password", 2);
    private Button save = new Button("Save");

    private LoginPresenter handler;
    private LoginView parentView;

    public NewPasswordWindow(LoginView parentView, LoginPresenter loginPresenter) {
        super("Set Password");
        this.handler = loginPresenter;
        this.parentView = parentView;
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
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public LoginPresenter getHandler() { return handler; }
    public void setHandler(LoginPresenter handler) { this.handler = handler; }

}
