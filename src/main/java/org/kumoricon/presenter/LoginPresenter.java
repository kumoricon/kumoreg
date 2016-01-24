package org.kumoricon.presenter;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.HomeView;
import org.kumoricon.view.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


@Controller
@Scope("request")
public class LoginPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    private LoginView loginView;

    public LoginPresenter() {
    }


    public LoginView getLoginView() { return loginView; }
    public void setLoginView(LoginView loginView) { this.loginView = loginView; }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    public RoleRepository getRoleRepository() { return roleRepository; }
    public void setRoleRepository(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

    public void login(String username, String password) {
        if (username == null || password == null) { return; }

        User user = userRepository.findOneByUsernameIgnoreCase(username);
        if (user != null && user.checkPassword(password) && user.getEnabled()) {
            KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
            ui.setLoggedInUser(user);
            ui.buildMenu();
            ui.getNavigator().navigateTo(HomeView.VIEW_NAME);
        } else {
            loginView.loginFailed();
        }
    }
}
