package org.kumoricon.presenter.user;

import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.user.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class UserPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    private UserView userView;

    public UserPresenter() {
    }


    public void userSelected(User user) {
        if (user != null) {
            userView.navigateTo(userView.VIEW_NAME + "/" + user.getId().toString());
            userView.setRoleList(getAvailableRoles());
            userView.showUser(user);
        }
    }

    public void userSelected(Integer id) {
        if (id != null) {
            User user = userRepository.findOne(id);
            userSelected(user);
        }
    }

    public void addNewUser() {
        userView.clearUserForm();
        userView.showUserForm();
        userView.navigateTo(userView.VIEW_NAME);
        User user = new User();
        user.setPassword(user.DEFAULT_PASSWORD);
        userView.setRoleList(roleRepository.findAll());
        userView.showUser(user);
    }

    public void cancelUser() {
        userView.navigateTo(userView.VIEW_NAME);
        userView.clearUserForm();
        userView.hideUserForm();
        userView.clearSelection();
    }

    public void saveUser() {
        User user = userView.getUser();

        userRepository.save(user);
        userView.navigateTo(userView.VIEW_NAME);
        showUserList();
    }

    public void showUserList() {
        List<User> users = userRepository.findAll();
        userView.afterSuccessfulFetch(users);
    }

    public void navigateToUser(String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            User user = userRepository.findOne(id);
            userView.setRoleList(roleRepository.findAll());
            userView.selectUser(user);
        }
    }

    public void resetPassword() {
        User user = userView.getUser();
        user.setPassword("password");
        try {
            userRepository.save(user);
            userView.notify("Password reset for " + user.getUsername());
            cancelUser();
        } catch (Exception e) {
            userView.notifyError(e.getMessage());
        }
    }

    public List<Role> getAvailableRoles() { return roleRepository.findAll(); }

    public UserView getUserView() { return userView; }
    public void setUserView(UserView userView) { this.userView = userView; }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    public RoleRepository getRoleRepository() { return roleRepository; }
    public void setRoleRepository(RoleRepository roleRepository) { this.roleRepository = roleRepository; }
}
