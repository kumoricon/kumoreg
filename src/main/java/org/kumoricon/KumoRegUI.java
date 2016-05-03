package org.kumoricon;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.model.user.User;
import org.kumoricon.site.ErrorView;
import org.kumoricon.site.LoginView;
import org.kumoricon.site.SiteLogo;
import org.kumoricon.site.SiteMenu;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("valo")
@SpringUI
public class KumoRegUI extends UI {
    @Autowired
    private SpringViewProvider viewProvider;

    private SiteMenu menu;

    @Autowired
    private SiteLogo logo;

    private VerticalLayout leftPanel;

    public User getLoggedInUser(){
        return (User)getSession().getAttribute("user");
    }

    public void setLoggedInUser(User user){
        getSession().setAttribute("user", user);
        logo.setUser(user);
    }

    @Override
    protected void init(VaadinRequest request) {
        final HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull();
        root.setMargin(false);
        root.setSpacing(false);
        setContent(root);

        // Left hand logo and menu
        leftPanel = new VerticalLayout();
        leftPanel.setWidth(200, Unit.PIXELS);
        leftPanel.addComponent(logo);
        if (getLoggedInUser() != null) { buildMenu(); }
        root.addComponent(leftPanel);

//        final CssLayout navigationBar = new CssLayout();
//        navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
//        navigationBar.addComponent(createNavigationButton("Home",
//                HomeView.VIEW_NAME));
//        navigationBar.addComponent(createNavigationButton("UI Scoped View",
//                UIScopedView.VIEW_NAME));
//        navigationBar.addComponent(createNavigationButton("View Scoped View",
//                ViewScopedView.VIEW_NAME));
//        root.addComponent(navigationBar);

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        root.addComponent(viewContainer);
        root.setExpandRatio(viewContainer, 1.0f);
        Navigator navigator = new Navigator(this, viewContainer);
        navigator.setErrorView(new ErrorView());

        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                User currentUser = ((KumoRegUI)KumoRegUI.getCurrent()).getLoggedInUser();
                if (currentUser == null && !(event.getNewView() instanceof LoginView)) {
                    event.getNavigator().navigateTo(LoginView.VIEW_NAME);
                    return false;
//                } else if (!currentUser.hasRight("test")) {
//                    Notification.show("Permission denied", Notification.Type.ERROR_MESSAGE);
//                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {}
        });

        navigator.addProvider(viewProvider);
    }

    public void buildMenu() {
        if (leftPanel.getComponentCount() == 1) {
            menu = new SiteMenu(getLoggedInUser());
            leftPanel.addComponent(menu);
        }
    }

    public void removeMenu() {
        leftPanel.removeComponent(menu);
    }

}