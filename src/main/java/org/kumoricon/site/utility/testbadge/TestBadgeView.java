package org.kumoricon.site.utility.testbadge;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "testbadge";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private TestBadgePresenter handler;

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