package org.kumoricon.site.attendee.search.byname;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.v7.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.search.AttendeeSearchPresenter;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = AttendeeSearchView.VIEW_NAME)
public class AttendeeSearchView extends BaseView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "attendee_search";

    private final AttendeeSearchPresenter handler;

    private TextField txtSearch = new TextField("Search");
    private Button btnSearch = new Button("Search");
    private Table tblResult;
    private BeanItemContainer<Attendee> attendeeBeanList;

    @Autowired
    public AttendeeSearchView(AttendeeSearchPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        handler.setView(this);
        setWidth("100%");
        setHeight("95%");

        attendeeBeanList = new BeanItemContainer<>(Attendee.class, new ArrayList<>());

        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.setSpacing(false);
        txtSearch.setSizeFull();
        txtSearch.setImmediate(true);
        txtSearch.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);
        f.addComponent(txtSearch);

        btnSearch = new Button("Search");
        tblResult = new Table();
        tblResult.setWidth("95%");
        tblResult.setHeight("90%");
        tblResult.setContainerDataSource(attendeeBeanList);
        tblResult.setVisibleColumns("firstName", "lastName", "legalFirstName", "legalLastName", "fanName",
                "badgeNumber", "age", "zip", "checkedIn");
        tblResult.setColumnHeaders("First Name", "Last Name", "Legal First Name", "Legal Last Name", "Fan Name",
                "Badge Number", "Age", "Zip", "Checked In");
        tblResult.addStyleName("kumoHandPointer");
        tblResult.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent -> {
            navigateTo(AttendeeSearchDetailView.VIEW_NAME +
                    "/" + txtSearch.getValue() +
                    "/" + itemClickEvent.getItem().getItemProperty("id").getValue());
        });

        btnSearch.addClickListener((Button.ClickListener) clickEvent -> search());

        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        h.setMargin(false);
        h.addComponent(f);
        h.addComponent(btnSearch);
        h.setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);

        addComponent(h);
        addComponent(tblResult);
        txtSearch.focus();

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            txtSearch.clear();
            attendeeBeanList.removeAllItems();
        } else {
            String searchString = viewChangeEvent.getParameters();
            tblResult.clear();
            if (txtSearch.getValue() != null && !txtSearch.getValue().equals(searchString)) {
                txtSearch.setValue(searchString);
            }
            handler.searchFor(searchString);
        }
        txtSearch.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> search());
    }

    public void afterSuccessfulFetch(List<Attendee> attendees) {
        Object[] sortBy = {tblResult.getSortContainerPropertyId()};
        boolean[] sortOrder = {tblResult.isSortAscending()};

        attendeeBeanList.removeAllItems();
        attendeeBeanList.addAll(attendees);
        tblResult.sort(sortBy, sortOrder);
        txtSearch.selectAll();
    }

    private void search() {
        handler.searchChanged(txtSearch.getValue());
    }

    @Override
    public void refresh() {
        handler.searchFor(txtSearch.getValue());
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow printBadgeWindow = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(printBadgeWindow);
    }
}