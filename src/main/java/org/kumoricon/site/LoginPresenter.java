package org.kumoricon.site;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(LoginPresenter.class);

    public LoginPresenter() {
    }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    public RoleRepository getRoleRepository() { return roleRepository; }
    public void setRoleRepository(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

    void login(LoginView view, String username, String password) {
        if (username == null || password == null) { return; }

        User user = userRepository.findOneByUsernameIgnoreCase(username);
        if (user == null) {
            log.error("{} tried to log in from {} but was not found in the database",
                    username, view.getCurrentClientIPAddress());
            view.loginFailed();
        } else if (!user.checkPassword(password)) {
            log.error("{} tried to log in from {} but entered a bad password", user, view.getCurrentClientIPAddress());
            view.loginFailed();
        } else if (!user.getEnabled()) {
            log.error("{} tried to log in from {} but is disabled", user, view.getCurrentClientIPAddress());
            view.loginFailed();
        } else {
            log.info("{} logged in from {}", user, view.getCurrentClientIPAddress());
            afterSuccessfulLogin(view, user);
            if (user.getResetPassword()) {
                view.showNewPasswordWindow();
            } else {
                view.navigateTo(HomeView.VIEW_NAME);
            }
        }
    }

    private static void afterSuccessfulLogin(LoginView view, User user) {
        KumoRegUI ui = (KumoRegUI)view.getUI();
        ui.setLoggedInUser(user);
        ui.buildMenu();
    }

    void setNewPassword(LoginView view, String newPassword) {
        log.info("{} set new password on login from {}",
                view.getCurrentUsername(), view.getCurrentClientIPAddress());
        User currentUser = userRepository.findOne(view.getCurrentUser().getId());
        currentUser.setPassword(newPassword);
        currentUser.setResetPassword(false);
        userRepository.save(currentUser);
        afterSuccessfulLogin(view, currentUser);
        view.navigateTo(HomeView.VIEW_NAME);
        view.notify("Password Set");
    }


}
