package org.kumoricon.site.attendee.search.byname;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.attendee.search.AttendeeSearchPresenter;
import org.kumoricon.site.attendee.search.SearchPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = SearchByNameView.VIEW_NAME)
public class SearchByNameView extends BaseGridView implements View {
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "attendee_search";

    private final SearchPresenter handler;

    private TextField txtSearch = new TextField();
    private Button btnSearch = new Button("Search");
    private Grid<Attendee> tblResult = new Grid<>();

    @Autowired
    public SearchByNameView(SearchPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setRows(2);
        setColumns(5);
        setHeight("90%");
        setRowExpandRatio(1, 10);

        txtSearch.setSizeFull();
        addComponent(txtSearch, 1, 0, 2, 0);

        btnSearch = new Button("Search");
        btnSearch.addClickListener((Button.ClickListener) clickEvent -> navigateTo(VIEW_NAME + "/" + txtSearch.getValue()));
        btnSearch.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        addComponent(btnSearch, 3, 0);

        tblResult.setWidth("95%");
        tblResult.setHeight("90%");

        tblResult.addColumn(Attendee::getFirstName).setCaption("First Name");
        tblResult.addColumn(Attendee::getLastName).setCaption("Last Name");
        tblResult.addColumn(Attendee::getLegalFirstName).setCaption("Legal First Name");
        tblResult.addColumn(Attendee::getLegalLastName).setCaption("Legal Last Name");
        tblResult.addColumn(Attendee::getFanName).setCaption("Fan Name");
        tblResult.addColumn(Attendee::getBadgeNumber).setCaption("Badge Number");
        tblResult.addColumn(Attendee::getAge).setCaption("Age");
        tblResult.addColumn(Attendee::getZip).setCaption("Zip");
        tblResult.addColumn(Attendee::getCheckedIn).setCaption("Checked In");

        tblResult.addStyleName("kumoHandPointer");
        tblResult.addItemClickListener(itemClickEvent -> {
            navigateTo(AttendeeSearchDetailView.VIEW_NAME +
                    "/" + txtSearch.getValue() +
                    "/" + itemClickEvent.getItem().getId());
        });

        addComponent(tblResult, 0, 1, 4, 1);
        txtSearch.focus();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            txtSearch.clear();
        } else {
            String searchString = viewChangeEvent.getParameters();
            if (txtSearch.getValue() != null && !txtSearch.getValue().equals(searchString)) {
                txtSearch.setValue(searchString);
                handler.searchFor(this, searchString);
            }
        }
    }

    public void afterSuccessfulFetch(List<Attendee> attendees) {
        tblResult.setItems(attendees);
        txtSearch.selectAll();
    }

    @Override
    public void refresh() {
        handler.searchFor(this, txtSearch.getValue());
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
