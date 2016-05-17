package org.kumoricon.site;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static org.kumoricon.site.attendee.FieldFactory.createTextField;

@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends BaseView implements View {
    public static final String VIEW_NAME = "login";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private LoginPresenter handler;

    private TextField usernameField = createTextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private Button loginButton = new Button("Login");

    @PostConstruct
    void init() {
        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(usernameField);
        formLayout.addComponent(passwordField);
        formLayout.addComponent(loginButton);
        addComponent(formLayout);

        loginButton.setClickShortcut( ShortcutAction.KeyCode.ENTER ) ;
        loginButton.addClickListener((Button.ClickListener) clickEvent -> {
            if (usernameField.isEmpty()) {
                Notification.show("Username is required");
                usernameField.focus();
            } else if (passwordField.isEmpty()) {
                Notification.show("Password is required");
                passwordField.focus();
            } else {
                handler.login(this, usernameField.getValue(), passwordField.getValue());
            }
        });
        usernameField.focus();
    }

    public void showNewPasswordWindow() {
        NewPasswordWindow window = new NewPasswordWindow(this, handler);
        showWindow(window);
    }

    public void loginFailed() {
        Notification.show("Error: Login failed", Notification.Type.HUMANIZED_MESSAGE);
        passwordField.selectAll();
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
