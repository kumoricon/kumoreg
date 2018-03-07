package org.kumoricon.site;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.BaseGridView;
import org.kumoricon.site.utility.closeouttill.CloseOutTillView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = LogoutView.VIEW_NAME)
public class LogoutView extends BaseGridView implements View {
    public static final String VIEW_NAME = "logout";
    public static final String REQUIRED_RIGHT = null;

    private final LogoutPresenter handler;

    private Label warning = new Label("Warning: Till has not been closed out");
    private Button goToTillReport = new Button("Close out till");
    private Button logout = new Button("Logout anyway");

    @Autowired
    public LogoutView(LogoutPresenter handler) {
        this.handler = handler;
    }


    @PostConstruct
    void init() {
        setColumns(3);
        setRows(2);
        addComponent(warning, 0, 0, 2, 0);
        warning.setSizeUndefined();

        addComponent(goToTillReport, 0, 1);
        goToTillReport.focus();
        goToTillReport.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        goToTillReport.addStyleName(ValoTheme.BUTTON_PRIMARY);
        goToTillReport.addClickListener((Button.ClickListener) clickEvent -> navigateTo(CloseOutTillView.VIEW_NAME));

        addComponent(logout, 2, 1);
        logout.addClickListener((Button.ClickListener) clickEvent -> handler.logout(this));
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
