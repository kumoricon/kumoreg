package org.kumoricon.site.user;

import com.vaadin.ui.Button;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import org.kumoricon.model.user.User;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = UserListView.VIEW_NAME)
public class UserListView extends BaseView implements View {
    public static final String VIEW_NAME = "users";
    public static final String REQUIRED_RIGHT = "manage_staff";

    private final UserPresenter handler;
    private final Grid<User> userList = new Grid<>();
    private final Button btnAddNew = new Button("Add New");

    @Autowired
    public UserListView(UserPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        userList.setWidth("800px");
        userList.setSelectionMode(Grid.SelectionMode.NONE);

        userList.addColumn(User::getUsername).setCaption("Username");
        userList.addColumn(User::getFirstName).setCaption("First Name");
        userList.addColumn(User::getLastName).setCaption("Last Name");
        userList.addColumn(user -> user.getRole().getName()).setCaption("Role");
        userList.addColumn(User::getBadgePrefix).setCaption("Badge Prefix");
        userList.addColumn(User::getLastBadgeNumberCreated).setCaption("Last Badge Number");
        userList.addColumn(User::getEnabled).setCaption("Enabled");

        userList.addItemClickListener(clickEvent -> {
            User u = clickEvent.getItem();
            navigateTo(UserEditView.VIEW_NAME + "/" + u.getId());
        });

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> navigateTo(UserEditView.VIEW_NAME));

        addComponents(userList, btnAddNew);
        handler.showUserList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
    }

    public void afterSuccessfulFetch(List<User> users) {
        userList.setItems(users);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

}
