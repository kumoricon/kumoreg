package org.kumoricon.presenter;

import com.vaadin.ui.Notification;
import org.kumoricon.KumoRegUI;
import org.kumoricon.view.LoginView;
import org.kumoricon.view.LogoutView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


@Controller
@Scope("request")
public class LogoutPresenter {
    private LogoutView view;

    public LogoutPresenter() {
    }


    public LogoutView getView() { return view; }
    public void setView(LogoutView view) { this.view = view; }

    public void logout() {
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        ui.setLoggedInUser(null);
        ui.removeMenu();
        ui.getNavigator().navigateTo(LoginView.VIEW_NAME);
        Notification.show("Logged out");
    }
}
