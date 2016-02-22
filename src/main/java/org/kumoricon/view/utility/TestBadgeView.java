package org.kumoricon.view.utility;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.utility.TestBadgePresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseView implements View {
    public static final String VIEW_NAME = "testbadge";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private TestBadgePresenter handler;

    @PostConstruct
    public void init() {
        handler.setView(this);

        Button display = new Button("Display");
        addComponent(display);

        display.addClickListener((Button.ClickListener) clickEvent -> handler.showAttendeeBadgeWindow(new ArrayList<Attendee>()));
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}