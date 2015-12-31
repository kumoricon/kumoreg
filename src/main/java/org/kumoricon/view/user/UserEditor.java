package org.kumoricon.view.user;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import org.kumoricon.model.user.User;

@SpringComponent
@ViewScope
public class UserEditor extends FormLayout {

    private User user;

    TextField userName = new TextField("Username");
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    CheckBox resetPassword = new CheckBox("Reset Password");

    final BeanFieldGroup<User> binder = new BeanFieldGroup<User>(User.class);

    public UserEditor() {
        userName.setNullRepresentation("");
        firstName.setNullRepresentation("");
        lastName.setNullRepresentation("");

        binder.setItemDataSource(user);
        binder.bind(userName, "userName");
        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");

        binder.setBuffered(false);

        addComponents(userName, firstName, lastName, resetPassword);
        setSpacing(true);
    }

    public final void editUser(User u) {
        final boolean persisted = u.getId() != null;
        this.user = u;
        BeanFieldGroup.bindFieldsUnbuffered(user, this);
        resetPassword.setValue(false);
        userName.selectAll();
    }

    public boolean isValid() {
        try {
            binder.commit();
        } catch(Exception e) {
            Notification.show(e.getMessage());
        }
        return binder.isValid();
    }

    public final User getUser() {
        return user;
    }
}