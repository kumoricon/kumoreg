package org.kumoricon.presenter;

import org.kumoricon.KumoRegUI;
import org.kumoricon.view.LoginView;
import org.kumoricon.view.LogoutView;
import org.springframework.stereotype.Controller;

@Controller
public class LogoutPresenter {
    public LogoutPresenter() {
    }

    public void logout(LogoutView view) {
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        ui.setLoggedInUser(null);
        ui.removeMenu();
        ui.getNavigator().navigateTo(LoginView.VIEW_NAME);
        view.notify("Logged out");
    }
}
