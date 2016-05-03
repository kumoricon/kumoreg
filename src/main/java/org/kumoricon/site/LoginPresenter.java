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

    public void login(LoginView view, String username, String password) {
        if (username == null || password == null) { return; }

        User user = userRepository.findOneByUsernameIgnoreCase(username);
        if (user != null && user.checkPassword(password) && user.getEnabled()) {
            KumoRegUI ui = (KumoRegUI)view.getUI();
            ui.setLoggedInUser(user);
            ui.buildMenu();
            view.navigateTo(HomeView.VIEW_NAME);
            log.info("{} logged in from {}", user, view.getCurrentClientIPAddress());
        } else {
            view.loginFailed();
        }
    }
}
