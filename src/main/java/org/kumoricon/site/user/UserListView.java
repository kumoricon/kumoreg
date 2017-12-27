package org.kumoricon.site.user;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.user.User;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.fieldconverter.RoleToStringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = UserListView.VIEW_NAME)
public class UserListView extends BaseView implements View {
    public static final String VIEW_NAME = "users";
    public static final String REQUIRED_RIGHT = "manage_staff";

    private final UserPresenter handler;
    private final Table userList = new Table("Users");
    private final Button btnAddNew = new Button("Add New");

    @Autowired
    public UserListView(UserPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setMargin(true);
        setSpacing(true);
        userList.setNullSelectionAllowed(false);
        userList.setImmediate(true);
        userList.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        userList.setItemCaptionPropertyId("username");

        addComponent(btnAddNew);
        addComponent(userList);

        userList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
                    User u = (User)valueChangeEvent.getProperty().getValue();
                    navigateTo(UserEditView.VIEW_NAME + "/" + u.getId());
                });

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            navigateTo(UserEditView.VIEW_NAME);
        });

        handler.showUserList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
    }

    public void afterSuccessfulFetch(List<User> users) {
        Object[] sortBy = {userList.getSortContainerPropertyId()};
        boolean[] sortOrder = {userList.isSortAscending()};
        userList.setContainerDataSource(new BeanItemContainer<>(User.class, users));
        userList.setVisibleColumns("username", "firstName", "lastName", "role", "badgePrefix", "lastBadgeNumberCreated", "enabled");
        userList.setColumnHeaders("Username", "First Name", "Last Name", "Role", "Badge Prefix", "Last Badge Number", "Enabled");
        userList.setConverter("role", new RoleToStringConverter());
        userList.sort(sortBy, sortOrder);
        userList.setPageLength(users.size());
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

}
