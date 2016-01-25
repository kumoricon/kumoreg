package org.kumoricon.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import org.kumoricon.presenter.LogoutPresenter;
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
        handler.setView(this);
        handler.logout();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
