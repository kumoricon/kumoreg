package org.kumoricon.site;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.PostConstruct;

public class BaseView extends CssLayout implements View {

    private static final Logger log = LoggerFactory.getLogger(BaseView.class);

    protected void checkPermissions() {
        // Check user permission
        if (getRequiredRight() != null) {
            User user = getCurrentUser();
            if (user != null && !user.hasRight(getRequiredRight())) {
                log.error("{} access denied because they did not have right {}", user, getRequiredRight());
                navigateTo("/");
                Notification.show("Permission denied. Right " + getRequiredRight() + " is required.");
            }
        }
    }

    public void navigateTo(String path) {
        KumoRegUI ui = (KumoRegUI) getUI();
        if (ui != null) {
            ui.getNavigator().navigateTo(path);
        } else {
            log.warn("Tried to get UI and it was null");
        }
    }

    protected String getRequiredRight() {
        // In the default case, assume that the user does not have rights to this page. This should be
        // overridden in the individual views. "No Rights" shouldn't match any existing right.
        return "No Rights";
    }

    public Boolean currentUserHasRight(String right) {
        if (right != null) {
            KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
            if (ui == null) { return false; }
            User user = ui.getLoggedInUser();
            if (user != null) {
                return user.hasRight(right);
            }
        }
        return false;
    }

    public User getCurrentUser() {
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        if (ui == null) { return null; }
        User user = ui.getLoggedInUser();
        if (user != null) {
            return user;
        }
        return null;
    }

    /**
     * Returns string representing the current user, or their IP address if not logged in
     * @return User
     */
    public String getCurrentUsername() {
        User user = getCurrentUser();
        if (user != null) {
            return user.toString();
        } else {
            return String.format("[%s]", getCurrentClientIPAddress());
        }
    }

    /**
     * "Close" the current view, returning to whatever the parent is. For example, if the view
     * open is "/order/1234/123", then calling close() should navigate to "/order/1234"
     */
    public void close() {
        navigateTo("/");
    }

    /**
     * Refresh any data in the current view. Override this!
     */
    public void refresh() {
        throw new RuntimeException("Not implemented!");
    }

    public void showWindow(Window window) {
        getUI().addWindow(window);
    }

    public void notify(String message) { Notification.show(message); }
    public void notifyError(String message) { Notification.show(message, Notification.Type.WARNING_MESSAGE); }

    @PostConstruct
    protected void initLayout() {
        // For every [child] view, do this:
//        setSpacing(true);
//        setMargin(true);
        addStyleName("kumoBaseView");
    }

    public String getCurrentClientIPAddress() {
        return UI.getCurrent().getPage().getWebBrowser().getAddress();
    }


    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        // If you override the enter method, make sure to call super.enter(viewChangeEvent)
        checkPermissions();
    }

    public void setLoggedInUser(User user) {
        KumoRegUI ui = (KumoRegUI)KumoRegUI.getCurrent();
        if (ui != null) {
            ui.setLoggedInUser(user);
        }
    }
}
