package org.kumoricon.site.user;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.user.User;
import org.kumoricon.site.attendee.FieldFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public class UserEditWindow extends Window {

    private TextField username = FieldFactory.createTextField("Username");
    private TextField firstName = FieldFactory.createNameField("First Name");
    private TextField lastName = FieldFactory.createNameField("Last Name");
    private TextField badgePrefix = FieldFactory.createTextField("Badge Prefix");
    private NativeSelect role = new NativeSelect("Role");
    private TextField phone = FieldFactory.createTextField("Phone");

    private final BeanFieldGroup<User> userBeanFieldGroup = new BeanFieldGroup<>(User.class);

    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");
    private Button btnResetPassword = new Button("Reset Password");

    private UserPresenter handler;
    private UserView parentView;

    public UserEditWindow(UserView parentView, UserPresenter userPresenter, List<Role> roleList) {
        super("Edit User");
        this.handler = userPresenter;
        this.parentView = parentView;
        setIcon(FontAwesome.USER);
        center();
        setModal(true);
        setResizable(false);

        role.setContainerDataSource(new BeanItemContainer<>(Role.class, roleList));
        role.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        role.setItemCaptionPropertyId("name");

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);

        userBeanFieldGroup.bind(firstName, "firstName");
        firstName.focus();
        firstName.selectAll();
        firstName.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> updateUsername());
        form.addComponent(firstName);

        userBeanFieldGroup.bind(lastName, "lastName");
        lastName.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> updateUsername());
        form.addComponent(lastName);

        userBeanFieldGroup.bind(role, "role");
        role.setNullSelectionAllowed(false);
        form.addComponent(role);

        userBeanFieldGroup.bind(username, "username");
        form.addComponent(username);

        userBeanFieldGroup.bind(badgePrefix, "badgePrefix");
        badgePrefix.setWidth("6em");
        form.addComponent(badgePrefix);

        userBeanFieldGroup.bind(phone, "phone");
        form.addComponent(phone);
        form.addComponent(userBeanFieldGroup.buildAndBind("Enabled", "enabled"));

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                userBeanFieldGroup.commit();
                handler.saveUser(this, userBeanFieldGroup.getItemDataSource().getBean());
            } catch (DataIntegrityViolationException e) {
                Notification.show("Error saving user: Constraint violation. Duplicate username or badge prefix?");
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelUser(this));

        form.addComponent(buttons);

        form.addComponent(btnResetPassword);
        btnResetPassword.addClickListener((Button.ClickListener) clickEvent -> handler.resetPassword(this, getUser()));
        setContent(form);
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
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

    public User getUser() {
        BeanItem<User> userBean = userBeanFieldGroup.getItemDataSource();
        return userBean.getBean();
    }

    public void showUser(User user) {
        userBeanFieldGroup.setItemDataSource(user);
    }

    public UserPresenter getHandler() { return handler; }
    public void setHandler(UserPresenter handler) { this.handler = handler; }

    public UserView getParentView() { return parentView; }
}
