package org.kumoricon.site.user;

import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = UserEditView.VIEW_NAME)
class UserEditView extends BaseGridView implements View {

    private final TextField username = new TextField("Username");
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField badgePrefix = new TextField("Badge Prefix");
    private final NativeSelect<Role> role = new NativeSelect<>("Role");
    private final TextField phone = new TextField("Phone");
    private final CheckBox enabled = new CheckBox("Enabled");

    private final Binder<User> binder = new Binder<>();

    private final Button btnSave = new Button("Save");
    private final Button btnCancel = new Button("Cancel");
    private final Button btnResetPassword = new Button("Reset Password");

    private final UserPresenter handler;
    public static final String VIEW_NAME = "user";
    public static final String REQUIRED_RIGHT = "manage_staff";

    @Autowired
    public UserEditView(UserPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(3);
        setRows(4);
        setRowExpandRatio(3, 10);

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);
        form.setWidth("400px");

        binder.bind(firstName, User::getFirstName, User::setFirstName);
        firstName.focus();
        firstName.selectAll();
        firstName.addValueChangeListener(valueChangeEvent -> updateUsername());
        firstName.setValueChangeMode(ValueChangeMode.BLUR);

        form.addComponent(firstName);

        binder.bind(lastName, User::getLastName, User::setLastName);
        lastName.addValueChangeListener(valueChangeEvent -> updateUsername());
        lastName.setValueChangeMode(ValueChangeMode.BLUR);
        form.addComponent(lastName);

        binder.bind(role, User::getRole, User::setRole);
        role.setEmptySelectionAllowed(false);
        form.addComponent(role);

        binder.bind(username, User::getUsername, User::setUsername);
        form.addComponent(username);

        binder.bind(badgePrefix, User::getBadgePrefix, User::setBadgePrefix);
        badgePrefix.setWidth("6em");
        form.addComponent(badgePrefix);

        binder.bind(phone, User::getPhone, User::setPhone);
        form.addComponent(phone);

        binder.bind(enabled, User::getEnabled, User::setEnabled);
        form.addComponent(enabled);


        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                handler.saveUser(this, binder.getBean());
            } catch (DataIntegrityViolationException e) {
                Notification.show("Error saving user: Constraint violation. Duplicate username or badge prefix?");
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> navigateTo(UserListView.VIEW_NAME));


        btnResetPassword.addClickListener((Button.ClickListener) clickEvent -> handler.resetPassword(this, getUser()));
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);

        addComponent(form, 1, 0, 1, 3);
        addComponent(btnSave, 2, 0);
        addComponent(btnCancel, 2, 1);
        addComponent(btnResetPassword, 2, 2);
    }

    private void updateUsername() {
        if (firstName.getValue() != null && firstName.getValue().length() > 0 &&
                lastName.getValue() != null && lastName.getValue().length() > 0) {

            // Note: this would be a good place to hook in any "automatically find the next unused
            // username and badge prefix" routine.
            String newUserName = String.format("%s%s",
                    firstName.getValue().charAt(0),lastName.getValue()).toLowerCase();
            username.setValue(newUserName);

            String newBadgePrefix = String.format("%S%S",
                    firstName.getValue().charAt(0), lastName.getValue().charAt(0));
            badgePrefix.setValue(newBadgePrefix);
        }
    }

    private User getUser() {
        return binder.getBean();
    }

    void showUser(User user, List<Role> availableRoles) {
        role.setItems(availableRoles);
        binder.setBean(user);
        role.setItemCaptionGenerator(Role::getName);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        handler.showUser(this, parameters);
    }

}
