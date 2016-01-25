package org.kumoricon.view;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.kumoricon.presenter.LoginPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static org.kumoricon.util.FieldFactory.createTextField;

@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends FormLayout implements View {
    public static final String VIEW_NAME = "login";

    @Autowired
    private LoginPresenter handler;


    private TextField usernameField = createTextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private Button loginButton = new Button("Login");


    @PostConstruct
    void init() {
        handler.setLoginView(this);
        setMargin(true);
        setSpacing(true);

        addComponent(usernameField);
        addComponent(passwordField);
        addComponent(loginButton);
        loginButton.setClickShortcut( ShortcutAction.KeyCode.ENTER ) ;
        loginButton.addClickListener((Button.ClickListener) clickEvent -> {
            if (usernameField.isEmpty()) {
                Notification.show("Username is required");
                usernameField.focus();
            } else if (passwordField.isEmpty()) {
                Notification.show("Password is required");
                passwordField.focus();
            } else {
                handler.login(usernameField.getValue(), passwordField.getValue());
            }
        });
        usernameField.focus();


    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    public void loginFailed() {
        Notification.show("Error: Login failed", Notification.Type.HUMANIZED_MESSAGE);
        passwordField.selectAll();
    }
}
