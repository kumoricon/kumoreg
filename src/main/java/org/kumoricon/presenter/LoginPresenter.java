package org.kumoricon.presenter;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.HomeView;
import org.kumoricon.view.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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
        } else {
            view.loginFailed();
        }
    }
}
