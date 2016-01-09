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
import org.kumoricon.presenter.attendee.PreRegSearchPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = PreRegSearchView.VIEW_NAME)
public class PreRegSearchView extends VerticalLayout implements View{
    public static final String VIEW_NAME = "preregSearch";

    @Autowired
    private PreRegSearchPresenter handler;

    private TextField txtSearch = new TextField("Last Name");
    private Button btnSearch = new Button("Search");
    private Table tblResult;
    private BeanItemContainer<Attendee> attendeeBeanList;

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSpacing(true);
        setMargin(true);

        attendeeBeanList = new BeanItemContainer<Attendee>(Attendee.class, new ArrayList<Attendee>());

        FormLayout f = new FormLayout();
        f.setMargin(false);
        f.setSpacing(false);
        txtSearch.setSizeFull();
        txtSearch.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                search();
            }
        });
        txtSearch.setImmediate(true);
        txtSearch.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);
        f.addComponent(txtSearch);

        btnSearch = new Button("Search");
        tblResult = new Table();
        tblResult.setSizeFull();
        tblResult.setContainerDataSource(attendeeBeanList);
        tblResult.setVisibleColumns(new String[] { "firstName", "lastName", "badgeName", "age", "zip" });
        tblResult.setColumnHeaders("First Name", "Last Name", "Badge Name", "Age", "Zip");

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
        txtSearch.focus();

    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
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

    public void setHandler(PreRegSearchPresenter presenter) {
        this.handler = presenter;
    }

    public String getViewName() {
        return VIEW_NAME;
    }
}
