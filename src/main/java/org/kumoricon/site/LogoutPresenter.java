package org.kumoricon.site;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LogoutPresenter {
    private static final Logger log = LoggerFactory.getLogger(LogoutPresenter.class);

    @Autowired
    OrderRepository orderRepository;

    public LogoutPresenter() {
    }

    /**
     * Checks if the given user's current session has orders worth more than $0.
     * @param user User
     * @return True if session has orders worth more than $0.
     */
    public Boolean tillReortNotPrinted(User user) {
        if (user == null) {
            log.error("tillReortNotPrinted() called with null User");
            return false;
        }

        Float total = orderRepository.getSessionOrderTotal(user.getId(), user.getSessionNumber());
        if (total == null || total.compareTo(0.0f) <= 0) {
            return false;
        } else {
            log.info("{} is logging out but needs to print the till report", user);
            return true;
        }
    }

    public void logout(LogoutView view) {
        log.info("{} logged out via logout button", view.getCurrentUsername());
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        ui.setLoggedInUser(null);
        ui.removeMenu();
        ui.getNavigator().navigateTo(LoginView.VIEW_NAME);
        view.notify("Logged out");
    }
}
