package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.ui.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.attendee.search.SearchPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = SearchByBadgeView.VIEW_NAME)
public class SearchByBadgeView extends BaseGridView implements View {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "attendee_search";

    private SearchPresenter handler;

    private ComboBox<Badge> badgeType = new ComboBox<>();
    private Button refresh = new Button("Refresh");
    private Grid<Attendee> attendeeTable = new Grid<>();
    private String searchString;
    private List<Badge> availableBadgeTypes;
    private Grid.Column<Attendee, String> checkInLinkColumn;


    @Autowired
    public SearchByBadgeView(SearchPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(5);
        setRows(2);
        setRowExpandRatio(1, 10);
        setWidth("100%");
        setHeight("95%");

        badgeType.setPageLength(15);
        badgeType.setTextInputAllowed(false);
        badgeType.setEmptySelectionAllowed(false);
        badgeType.setItemCaptionGenerator(Badge::getName);
        badgeType.setTextInputAllowed(true);
        badgeType.setWidth("100%");
        refresh.addClickListener((Button.ClickListener) clickEvent ->
                handler.showAttendeeList(this, badgeType.getValue()));

        addComponent(badgeType, 2, 0, 3, 0);
        addComponent(refresh, 4, 0);
        addComponent(attendeeTable, 0, 1, 4, 1);

        handler.showBadgeTypes(this);

        attendeeTable.setSelectionMode(Grid.SelectionMode.NONE);
        attendeeTable.addColumn(Attendee::getFirstName).setCaption("First Name");
        attendeeTable.addColumn(Attendee::getLastName).setCaption("Last Name");
        attendeeTable.addColumn(Attendee::getLegalFirstName).setCaption("Legal First Name");
        attendeeTable.addColumn(Attendee::getLegalLastName).setCaption("Legal Last Name");
        attendeeTable.addColumn(Attendee::getFanName).setCaption("Fan Name");
        attendeeTable.addColumn(Attendee::getBadgeNumber).setCaption("Badge Number");
        attendeeTable.addColumn(Attendee::getAge).setCaption("Age");
        attendeeTable.addColumn(Attendee::getCheckedIn).setCaption("Checked In");
        attendeeTable.addColumn(Attendee::getCheckInTime).setCaption("Check In Time");
        if (currentUserHasRight("pre_reg_check_in")) {
            checkInLinkColumn = attendeeTable.addColumn(attendee -> {
                        if (!attendee.getCheckedIn()) {
                            return "<a href='#!" + VIEW_NAME + "/" + badgeType.getValue().getId() + "/" + attendee.getId() + "/checkin'>Check In</a>";
                        } else {
                            return "";
                        }},
                    new HtmlRenderer());
        }

        attendeeTable.addStyleName("kumoHandPointer");

        attendeeTable.addItemClickListener(itemClickEvent -> {
            // Don't navigate away if a link in the table was clicked, just follow that link
            if (checkInLinkColumn != itemClickEvent.getColumn()) {
                navigateTo(AttendeeSearchByBadgeDetailView.VIEW_NAME +
                        "/" + searchString +
                        "/" + itemClickEvent.getItem().getId());
            }
        });

        attendeeTable.setWidth("100%");
        attendeeTable.setHeight("90%");
    }

    public void afterAttendeeFetch(List<Attendee> attendees) {
        attendeeTable.setItems(attendees);
    }

    public void afterBadgeTypeFetch(List<Badge> badges) {
        availableBadgeTypes = badges;
        badgeType.setItems(badges);
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
            for (Badge badge : availableBadgeTypes) {
                if (badge.getId().equals(parameter)) {
                    badgeType.setSelectedItem(badge);
                    break;
                }
            }

            handler.showAttendeeList(this, parameter);
        } else {
            badgeType.setSelectedItem(null);
        }

        // Add the valueChangeListener after loading data so it doens't get fired
        // twice
        badgeType.addValueChangeListener( event -> {
            if (event != null) {
                Badge b = event.getValue();
                if (b != null) {
                    navigateTo(VIEW_NAME + "/" + b.getId());
                } else {
                    navigateTo(VIEW_NAME);
                }
            }
        });
        badgeType.focus();
    }

    @Override
    public void refresh() {
        handler.showAttendeeList(this, badgeType.getValue());
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

}
