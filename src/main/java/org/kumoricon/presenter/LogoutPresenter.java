package org.kumoricon.presenter;

import org.kumoricon.KumoRegUI;
import org.kumoricon.view.LoginView;
import org.kumoricon.view.LogoutView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class LogoutPresenter {
    private static final Logger log = LoggerFactory.getLogger(LogoutPresenter.class);

    public LogoutPresenter() {
    }

    public void logout(LogoutView view) {
        log.info("{} logged out via logout button", view.getCurrentUser());
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        ui.setLoggedInUser(null);
        ui.removeMenu();
        ui.getNavigator().navigateTo(LoginView.VIEW_NAME);
        view.notify("Logged out");
    }
}
