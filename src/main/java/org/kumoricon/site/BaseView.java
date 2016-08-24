package org.kumoricon.site;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;

public class BaseView extends VerticalLayout implements View {

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
        ui.getNavigator().navigateTo(path);
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
     * Refresh any data in the current view. Override this!
     */
    public void refresh() {
        throw new NotImplementedException();
    }

    public void showWindow(Window window) {
        getUI().addWindow(window);
    }

    public void notify(String message) { Notification.show(message); }
    public void notifyError(String message) { Notification.show(message, Notification.Type.WARNING_MESSAGE); }

    @PostConstruct
    protected void initLayout() {
        // For every [child] view, do this:
        setSpacing(true);
        setMargin(true);
    }

    public String getCurrentClientIPAddress() {
        return getUI().getCurrent().getPage().getWebBrowser().getAddress();
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
