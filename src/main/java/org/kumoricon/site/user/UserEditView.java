package org.kumoricon.site.user;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.v7.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.user.User;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.FieldFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = UserEditView.VIEW_NAME)
class UserEditView extends BaseView implements View {

    private final TextField username = FieldFactory.createTextField("Username");
    private final TextField firstName = FieldFactory.createNameField("First Name");
    private final TextField lastName = FieldFactory.createNameField("Last Name");
    private final TextField badgePrefix = FieldFactory.createTextField("Badge Prefix");
    private final NativeSelect role = new NativeSelect("Role");
    private final TextField phone = FieldFactory.createTextField("Phone");

    private final BeanFieldGroup<User> userBeanFieldGroup = new BeanFieldGroup<>(User.class);

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

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);
        form.setWidth("400px");

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

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> navigateTo(UserListView.VIEW_NAME));

        form.addComponent(buttons);

        form.addComponent(btnResetPassword);
        btnResetPassword.addClickListener((Button.ClickListener) clickEvent -> handler.resetPassword(this, getUser()));
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        addComponent(form);
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
        BeanItem<User> userBean = userBeanFieldGroup.getItemDataSource();
        return userBean.getBean();
    }

    void showUser(User user, List<Role> availableRoles) {
        role.setContainerDataSource(new BeanItemContainer<>(Role.class, handler.getAvailableRoles()));
        role.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        role.setItemCaptionPropertyId("name");
        userBeanFieldGroup.setItemDataSource(user);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        handler.showUser(this, parameters);
    }

}
