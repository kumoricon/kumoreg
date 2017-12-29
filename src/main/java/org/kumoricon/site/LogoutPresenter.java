package org.kumoricon.site;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class LogoutPresenter {
    private static final Logger log = LoggerFactory.getLogger(LogoutPresenter.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    SessionService sessionService;

    public LogoutPresenter() {
    }

    /**
     * Checks if the given user's current session has orders worth more than $0.
     * @param user User
     * @return True if session has orders worth more than $0.
     */
    public Boolean tillReportNotPrinted(User user) {
        if (user == null) {
            log.error("tillReportNotPrinted() called with null User");
            return false;
        }

        if (sessionService.userHasOpenSession(user)) {
            Session currentSession = sessionService.getCurrentSessionForUser(user);
            BigDecimal total = sessionService.getTotalForSession(currentSession);
            if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            } else {
                log.info("{} is logging out but needs to print the till report", user);
                return true;
            }
        } else {
            return false;
        }
    }

    public void logout(LogoutView view) {
        log.info("{} logged out via logout button", view.getCurrentUsername());
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        ui.setLoggedInUser(null);
        ui.buildMenu();
        ui.getNavigator().navigateTo(LoginView.VIEW_NAME);
        view.notify("Logged out");
    }
}
