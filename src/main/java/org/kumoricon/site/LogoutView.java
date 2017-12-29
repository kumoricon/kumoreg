package org.kumoricon.site;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.utility.closeouttill.CloseOutTillView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = LogoutView.VIEW_NAME)
public class LogoutView extends BaseView implements View {
    public static final String VIEW_NAME = "logout";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private LogoutPresenter handler;

    private Label spacer = new Label(" ");
    private Label warning = new Label("Warning: Till has not been closed out");
    private Button goToTillReport = new Button("Close out till");
    private Button logout = new Button("Logout anyway");


    @PostConstruct
    void init() {
        addComponent(spacer);
        spacer.setHeight("5em");
        addComponent(warning);
        warning.setSizeUndefined();
//        setComponentAlignment(warning, Alignment.MIDDLE_CENTER);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);

        horizontalLayout.addComponent(goToTillReport);
        goToTillReport.focus();
        goToTillReport.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        goToTillReport.addStyleName(ValoTheme.BUTTON_PRIMARY);
        goToTillReport.addClickListener((Button.ClickListener) clickEvent -> navigateTo(CloseOutTillView.VIEW_NAME));
        horizontalLayout.addComponent(logout);
        logout.addClickListener((Button.ClickListener) clickEvent -> handler.logout(this));
        addComponent(horizontalLayout);
//        setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (handler.tillReportNotPrinted(getCurrentUser())) {
            warning.setVisible(true);
        } else {
            handler.logout(this);
        }
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
