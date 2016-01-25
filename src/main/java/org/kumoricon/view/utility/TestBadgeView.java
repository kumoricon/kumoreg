package org.kumoricon.view.utility;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import org.kumoricon.view.BaseView;

import javax.annotation.PostConstruct;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseView implements View {
    public static final String VIEW_NAME = "testbadge";
    public static final String REQUIRED_RIGHT = null;

    @PostConstruct
    public void init() {
        addComponent(new Label("Print Test Badge"));
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}