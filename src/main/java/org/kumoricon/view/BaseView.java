package org.kumoricon.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.user.User;

import javax.annotation.PostConstruct;

public class BaseView extends VerticalLayout implements View {

    protected void checkPermissions() {
        // Check user permission
        if (getRequiredRight() != null) {
            KumoRegUI ui = (KumoRegUI) getUI();
            User user = ui.getLoggedInUser();
            if (user != null && !user.hasRight(getRequiredRight())) {
                ui.getNavigator().navigateTo("/");
                Notification.show("Permission denied. Right " + getRequiredRight() + " is required.");
            }
        }

    }

    protected String getRequiredRight() {
        // In the default case, assume that the user does not have rights to this page. This should be
        // overridden in the individual views. "No Rights" shouldn't match any existing right.
        return "No Rights";
    }

    @PostConstruct
    protected void initLayout() {
        // For every [child] view, do this:
        setSpacing(true);
        setMargin(true);
    }

    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        // If you override the enter method, make sure to call super.enter(viewChangeEvent)
        checkPermissions();
    }
}
