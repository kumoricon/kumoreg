package org.kumoricon;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.component.SiteLogo;
import org.kumoricon.component.SiteMenu;
import org.kumoricon.view.ErrorView;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("valo")
@SpringUI
public class KumoRegUI extends UI {
    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private SiteMenu menu;

    @Autowired
    private SiteLogo logo;

    @Override
    protected void init(VaadinRequest request) {
        final HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull();
        root.setMargin(false);
        root.setSpacing(false);
        setContent(root);

        // Left hand logo and menu
        final VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setWidth(200, Unit.PIXELS);
        leftPanel.addComponent(logo);
        leftPanel.addComponent(menu);
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
        navigator.addProvider(viewProvider);
        navigator.navigateTo("");

    }

}