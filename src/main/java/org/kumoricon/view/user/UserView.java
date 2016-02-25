package org.kumoricon.view.user;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.user.User;
import org.kumoricon.presenter.user.UserPresenter;
import org.kumoricon.util.FieldFactory;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = UserView.VIEW_NAME)
public class UserView extends BaseView implements View {
    public static final String VIEW_NAME = "users";
    public static final String REQUIRED_RIGHT = "manage_staff";

    @Autowired
    private UserPresenter handler;

    private TextField username = FieldFactory.createTextField("Username");
    private TextField firstName = FieldFactory.createTextField("First Name");
    private TextField lastName = FieldFactory.createTextField("Last Name");
    private NativeSelect role = new NativeSelect("Role");
    private TextField phone = FieldFactory.createTextField("Phone");
    private CheckBox enabled = new CheckBox("Enabled");

    private Button btnAddNew = new Button("Add");
    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");
    private Button btnResetPassword = new Button("Reset Password");
    private ListSelect userList = new ListSelect("Users");

    private BeanFieldGroup<User> userBeanFieldGroup = new BeanFieldGroup<>(User.class);

    private Layout leftPanel;
    private Layout rightPanel;

    @PostConstruct
    public void init() {
        handler.setUserView(this);

        leftPanel = buildLeftPanel();
        rightPanel = buildRightPanel();
        addComponent(leftPanel);
        addComponent(rightPanel);

        handler.showUserList();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            hideUserForm();
            clearUserForm();
        } else {
            handler.navigateToUser(viewChangeEvent.getParameters());
        }
    }

    public void setHandler(UserPresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<User> users) {
        userList.setContainerDataSource(new BeanItemContainer<User>(User.class, users));
    }

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        userList.setCaption("Users");
        userList.setNullSelectionAllowed(false);
        userList.setWidth(500, Unit.PIXELS);
        userList.setImmediate(true);
        leftPanel.addComponent(userList);
        leftPanel.addComponent(btnAddNew);

        userList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                handler.userSelected((User)valueChangeEvent.getProperty().getValue()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            userList.select(null);
            handler.addNewUser();
        });

        return leftPanel;
    }

    private FormLayout buildRightPanel() {

        FormLayout form = new FormLayout();
        form.setVisible(false);
        form.setMargin(true);
        form.setSpacing(true);

        userBeanFieldGroup.bind(username, "username");
        form.addComponent(username);

        userBeanFieldGroup.bind(firstName, "firstName");
        form.addComponent(firstName);

        userBeanFieldGroup.bind(lastName, "lastName");
        form.addComponent(lastName);

        userBeanFieldGroup.bind(phone, "phone");
        form.addComponent(phone);
        role.setNullSelectionAllowed(false);
        userBeanFieldGroup.bind(role, "role");
        form.addComponent(role);
        form.addComponent(userBeanFieldGroup.buildAndBind("Enabled", "enabled"));

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                userBeanFieldGroup.commit();
                handler.saveUser();
            } catch (DataIntegrityViolationException e) {
                Notification.show("Error saving user: Constraint violation. Duplicate username?");
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelUser());

        form.addComponent(buttons);

        form.addComponent(btnResetPassword);
        btnResetPassword.addClickListener((Button.ClickListener) clickEvent -> handler.resetPassword());


        return form;
    }

    public void clearUserForm() {
        username.clear();
        enabled.clear();
    }

    public void showUser(User user) {
        clearUserForm();
        showUserForm();
        if (user.getPassword() == null) {
            btnResetPassword.setEnabled(false);
        } else {
            btnResetPassword.setEnabled(true);
        }
        userBeanFieldGroup.setItemDataSource(user);
        username.selectAll();
    }

    public void setRoleList(List<Role> roleList) {
        role.clear();
        BeanItemContainer<Role> roleBeanItemContainer = new BeanItemContainer<>(Role.class, roleList);
        role.setContainerDataSource(roleBeanItemContainer);
    }

    public void hideUserForm() {
        rightPanel.setVisible(false);
    }
    public void showUserForm() { rightPanel.setVisible(true);}

    public void selectUser(User user) {
        userList.select(user);

    }

    public void clearSelection() {
        userList.select(null);
    }

    public User getUser() {
        BeanItem<User> userBean = userBeanFieldGroup.getItemDataSource();
        User user = userBean.getBean();

        return user;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

}
