package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.converter.StringToDateConverter;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.v7.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.search.AttendeeSearchPresenter;
import org.kumoricon.site.attendee.search.byname.AttendeeSearchView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ViewScope
@SpringView(name = AttendeeSearchByBadgeView.VIEW_NAME)
public class AttendeeSearchByBadgeView extends AttendeeSearchView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "attendee_search";

    @Autowired
    private AttendeeSearchPresenter handler;

    private Label badgeTypeLabel = new Label("Badge Type:" );
    private ComboBox badgeType = new ComboBox();
    private Button refresh = new Button("Refresh");
    private BeanItemContainer<Attendee> attendeeContainer = new BeanItemContainer<>(Attendee.class);
    private Table attendeeTable = new Table(null);
    private String searchString;

    public AttendeeSearchByBadgeView(AttendeeSearchPresenter handler) {
        super(handler);
    }

    @PostConstruct
    public void init() {
        handler.setView(this);
        setWidth("100%");
        setHeight("95%");
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        badgeType.setPageLength(15);
        badgeType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badgeType.setItemCaptionPropertyId("name");
        badgeType.setTextInputAllowed(false);
        badgeType.setNullSelectionAllowed(false);
        badgeType.setNewItemsAllowed(false);
        badgeType.setWidth("400px");
        refresh.addClickListener((Button.ClickListener) clickEvent ->
                handler.showAttendeeList(this, (Badge) badgeType.getValue()));
        header.addComponent(badgeTypeLabel);
        header.setComponentAlignment(badgeTypeLabel, Alignment.MIDDLE_LEFT);
        header.addComponent(badgeType);
        header.addComponent(refresh);

        addComponent(header);
        addComponent(attendeeTable);
        handler.showBadgeTypes(this);
        attendeeTable.setContainerDataSource(attendeeContainer);
        attendeeTable.setVisibleColumns("lastName", "firstName", "fanName", "badgeNumber", "checkedIn", "checkInTime");
        attendeeTable.setColumnHeaders("Last Name", "First Name", "Fan Name", "Badge Number", "Checked In", "Check In Time");
        attendeeTable.setConverter("checkInTime", new StringToDateConverter(){
            @Override
            public DateFormat getFormat(Locale locale){
                return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            }
        });
        attendeeTable.addStyleName("kumoHandPointer");

        attendeeTable.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent -> {
            navigateTo(AttendeeSearchByBadgeDetailView.VIEW_NAME +
                    "/" + searchString +
                    "/" + itemClickEvent.getItem().getItemProperty("id").getValue());
                });

        attendeeTable.setWidth("100%");
        attendeeTable.setHeight("90%");
    }

    public void afterAttendeeFetch(List<Attendee> attendees) {
        Object[] sortBy = {attendeeTable.getSortContainerPropertyId()};
        boolean[] sortOrder = {attendeeTable.isSortAscending()};

        attendeeContainer.removeAllItems();
        attendeeContainer.addAll(attendees);
        attendeeTable.sort(sortBy, sortOrder);
    }

    public void afterBadgeTypeFetch(List<Badge> badges) {
        badgeType.setContainerDataSource(new BeanItemContainer<>(Badge.class, badges));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        checkPermissions();
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            Integer parameter = null;
            try {
                parameter = Integer.parseInt(parameters);
                searchString = parameters;
            } catch (NumberFormatException e) {
                // Garbage input in the URL - treat it as null
            }

            // Set the badgeType selection based on the ID in the URL
            for (Object item : badgeType.getItemIds()) {
                Badge badge = (Badge) item;
                if (parameter.equals(badge.getId())) {
                    badgeType.select(badge);
                    break;
                }
            }

            handler.showAttendeeList(this, parameter);
        } else {
            badgeType.select(null);
            afterAttendeeFetch(new ArrayList<>());
        }

        // Add the valueChangeListener after loading data so it doens't get fired
        // twice
        badgeType.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (event.getProperty() != null) {
                Badge b = (Badge) event.getProperty().getValue();
                if (b != null) {
                    navigateTo(VIEW_NAME + "/" + b.getId());
                } else {
                    navigateTo(VIEW_NAME);
                }
            }
        });

    }

    @Override
    public void refresh() {
        handler.showAttendeeList(this, (Badge) badgeType.getValue());
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

}
