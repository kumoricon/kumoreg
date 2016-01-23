package org.kumoricon.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.FormLayout;
import org.kumoricon.presenter.LogoutPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = LogoutView.VIEW_NAME)
public class LogoutView extends FormLayout implements View {
    public static final String VIEW_NAME = "logout";

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

}
