package org.kumoricon.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;

import javax.annotation.PostConstruct;

@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends BaseView implements View {
    public static final String VIEW_NAME = "";
    public static final String REQUIRED_RIGHT = null;

    @PostConstruct
    void init() {
        addComponent(new Label("This is the default view"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}