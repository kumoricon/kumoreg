package org.kumoricon.site;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.v7.ui.Label;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ErrorView.VIEW_NAME)
public class ErrorView extends BaseView implements View {
    public static final String VIEW_NAME = "error";
    public static final String REQUIRED_RIGHT = null;

    @PostConstruct
    void init() {
        addComponent(new Label("Error: View not found"));
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}