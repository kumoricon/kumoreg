package org.kumoricon.view.attendee;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.attendee.SearchPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = SearchView.VIEW_NAME)
public class SearchView extends BaseView implements View{
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "attendee_search";

    @Autowired
    private SearchPresenter handler;

    private TextField txtSearch = new TextField("Last Name or Badge Number");
    private Button btnSearch = new Button("Search");
    private Table tblResult;
    private BeanItemContainer<Attendee> attendeeBeanList;

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();

        attendeeBeanList = new BeanItemContainer<Attendee>(Attendee.class, new ArrayList<Attendee>());

        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.setSpacing(false);
        txtSearch.setSizeFull();
        txtSearch.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> search());
        txtSearch.setImmediate(true);
        txtSearch.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);
        f.addComponent(txtSearch);

        btnSearch = new Button("Search");
        tblResult = new Table();
        tblResult.setSizeFull();
        tblResult.setContainerDataSource(attendeeBeanList);
        tblResult.setVisibleColumns(new String[] { "firstName", "lastName", "badgeName", "badgeNumber", "age", "zip", "checkedIn"});
        tblResult.setColumnHeaders("First Name", "Last Name", "Badge Name", "Badge Number", "Age", "Zip", "Checked In");

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
        txtSearch.focus();

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            txtSearch.clear();
            tblResult.clear();
        } else {
            String searchString = viewChangeEvent.getParameters();
            tblResult.clear();
            txtSearch.setValue(searchString);
            handler.searchFor(searchString);
        }
    }

    public void afterSuccessfulFetch(List<Attendee> attendees) {
        attendeeBeanList.removeAllItems();
        attendeeBeanList.addAll(attendees);
    }

    private void search() {
        handler.searchChanged(txtSearch.getValue());
        handler.searchFor(txtSearch.getValue());
    }


    public BeanItemContainer<Attendee> getAttendeeBeanList() {
        return attendeeBeanList;
    }

    public void setHandler(SearchPresenter presenter) {
        this.handler = presenter;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
