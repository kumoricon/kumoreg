package org.kumoricon.view.attendee;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.presenter.attendee.PreRegPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = PreRegView.VIEW_NAME)
public class PreRegView extends BaseView implements View, AttendeePrintView{
    public static final String VIEW_NAME = "preReg";
    public static final String REQUIRED_RIGHT = "pre_reg_check_in";

    @Autowired
    private PreRegPresenter handler;

    private TextField txtSearch = new TextField("Last Name or Order ID");
    private Button btnSearch = new Button("Search");
    private Table tblResult;
    private BeanItemContainer<Attendee> attendeeBeanList;

    private PreRegCheckInWindow preRegCheckInWindow;

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();
        attendeeBeanList = new BeanItemContainer<Attendee>(Attendee.class, new ArrayList<Attendee>());

        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.setSpacing(false);
        txtSearch.setSizeFull();
        f.addComponent(txtSearch);

        btnSearch = new Button("Search");
        tblResult = new Table();
        tblResult.setContainerDataSource(attendeeBeanList);
        tblResult.setVisibleColumns(new String[] { "firstName", "lastName", "badgeName", "age", "zip", "checkedIn" });
        tblResult.setColumnHeaders("First Name", "Last Name", "Badge Name", "Age", "Zip", "Checked In");

        tblResult.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent ->
                handler.selectAttendee((Attendee)itemClickEvent.getItemId()));

        btnSearch.addClickListener((Button.ClickListener) clickEvent -> search());

        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(false);
        h.addComponent(f);
        h.addComponent(btnSearch);
        h.setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);

        addComponent(h);
        addComponent(tblResult);
        setExpandRatio(tblResult, 1.0f);
        tblResult.setSizeFull();
        txtSearch.focus();
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.replace("/search", "").equals("")) {
            txtSearch.clear();
            tblResult.clear();
        } else if (parameters.matches("^search/.*")) {
            String searchString = viewChangeEvent.getParameters().replace("search/", "");
            tblResult.clear();
            if (preRegCheckInWindow != null) {
                preRegCheckInWindow.close();
            }
            txtSearch.setValue(searchString);
            handler.searchFor(searchString);
        } else {
            handler.showAttendee(this, Integer.parseInt(parameters));
        }
    }

    public void afterSuccessfulFetch(List<Attendee> attendees) {
        attendeeBeanList.removeAllItems();
        attendeeBeanList.addAll(attendees);
    }

    private void search() {
        handler.searchChanged(txtSearch.getValue());
    }


    public void showAttendee(Attendee attendee, List<Badge> badgeList) {
        preRegCheckInWindow = new PreRegCheckInWindow(this, handler);
        preRegCheckInWindow.setAvailableBadges(badgeList);
        preRegCheckInWindow.showAttendee(attendee);
        showWindow(preRegCheckInWindow);
    }

    public BeanItemContainer<Attendee> getAttendeeBeanList() {
        return attendeeBeanList;
    }

    public void setHandler(PreRegPresenter presenter) {
        this.handler = presenter;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow printBadgeWindow = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(printBadgeWindow);
    }
}
