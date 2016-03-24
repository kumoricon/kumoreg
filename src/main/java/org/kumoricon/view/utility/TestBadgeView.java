package org.kumoricon.view.utility;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.utility.PrintBadgePresenter;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.AttendeePrintView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "testbadge";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private PrintBadgePresenter handler;

    @PostConstruct
    public void init() {
        Button display = new Button("Print Test Badges");
        addComponent(display);

        display.addClickListener((Button.ClickListener) clickEvent -> handler.showAttendeeBadgeWindow(this, new ArrayList<Attendee>()));
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow window = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(window);
    }
}