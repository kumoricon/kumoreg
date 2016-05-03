package org.kumoricon.site.user;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.user.User;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = UserView.VIEW_NAME)
public class UserView extends BaseView implements View {
    public static final String VIEW_NAME = "users";
    public static final String REQUIRED_RIGHT = "manage_staff";

    @Autowired
    private UserPresenter handler;
    private ListSelect userList = new ListSelect("Users");
    private Button btnAddNew = new Button("Add New");
    private Layout leftPanel;

    @PostConstruct
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        userList.setCaption("Users");
        userList.setNullSelectionAllowed(false);
        userList.setWidth(500, Unit.PIXELS);
        userList.setImmediate(true);
        userList.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        userList.setItemCaptionPropertyId("username");
        layout.addComponent(userList);
        layout.addComponent(btnAddNew);

        userList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                handler.userSelected(this, (User)valueChangeEvent.getProperty().getValue()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            userList.select(null);
            handler.addNewUser(this);
        });

        addComponent(layout);
        handler.showUserList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.equals("")) {
            handler.navigateToUser(this, viewChangeEvent.getParameters());
        }
    }

    public void setHandler(UserPresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<User> users) {
        userList.setContainerDataSource(new BeanItemContainer<User>(User.class, users));
    }

    public void showUser(User user, List<Role> roles) {
        UserEditWindow window = new UserEditWindow(this, handler, roles);
        window.showUser(user);
        showWindow(window);
    }

    public void selectUser(User user) {
        userList.select(user);
    }

    public void clearSelection() {
        userList.select(null);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

}
