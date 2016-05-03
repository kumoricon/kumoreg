package org.kumoricon.site;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = LogoutView.VIEW_NAME)
public class LogoutView extends BaseView implements View {
    public static final String VIEW_NAME = "logout";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private LogoutPresenter handler;

    @PostConstruct
    void init() {
        handler.logout(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
