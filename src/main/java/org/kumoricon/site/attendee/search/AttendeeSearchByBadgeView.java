package org.kumoricon.site.attendee.search;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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
    private Grid dataGrid = new Grid("Attendees");

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        badgeType.setPageLength(15);
        badgeType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badgeType.setItemCaptionPropertyId("name");
        badgeType.setTextInputAllowed(false);
        badgeType.setNullSelectionAllowed(false);
        badgeType.setNewItemsAllowed(false);
        badgeType.setWidth("400px");
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
        refresh.addClickListener((Button.ClickListener) clickEvent ->
                handler.showAttendeeList(this, (Badge) badgeType.getValue()));
        header.addComponent(badgeTypeLabel);
        header.setComponentAlignment(badgeTypeLabel, Alignment.MIDDLE_LEFT);
        header.addComponent(badgeType);
        header.addComponent(refresh);

        addComponent(header);
        addComponent(dataGrid);

        handler.showBadgeTypes(this);
        dataGrid.setColumns(new String[] {"lastName", "firstName", "badgeName", "badgeNumber",
                "checkedIn", "checkInTime"});
        dataGrid.getColumn("lastName").setMinimumWidth(250);
        dataGrid.getColumn("firstName").setMinimumWidth(250);
        dataGrid.getColumn("badgeName").setMinimumWidth(250);
        dataGrid.getColumn("badgeName").setExpandRatio(1);
        dataGrid.getColumn("badgeNumber").setMinimumWidth(150);
        dataGrid.getColumn("checkedIn").setMinimumWidth(100);
        dataGrid.getColumn("checkedIn").setWidth(100);
        dataGrid.getColumn("checkInTime").setMinimumWidth(300);
        dataGrid.getColumn("checkInTime").setExpandRatio(1);
        dataGrid.setEditorEnabled(false);
        dataGrid.setSelectionMode(Grid.SelectionMode.NONE);
        dataGrid.addStyleName("kumoHeaderOnlyHandPointer");
        dataGrid.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent ->
                handler.showAttendee((Integer) itemClickEvent.getItem().getItemProperty("id").getValue()));
        dataGrid.setSizeFull();
        setExpandRatio(dataGrid, 1.0f);
    }

    public void afterAttendeeFetch(List<Attendee> attendees) {
        dataGrid.setContainerDataSource(new BeanItemContainer<>(Attendee.class, attendees));
        dataGrid.recalculateColumnWidths();
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
            } catch (NumberFormatException e) {
                // Garbage input in the URL - treat it as null
            }
            Badge currentValue = (Badge) badgeType.getValue();

            // If the badge selection box isn't already set to the parameter in the URL, change it.
            // This will fire the value change listener again
            boolean selectionChanged = false;
            if (currentValue == null || !parameter.equals(currentValue.getId())) {
                for (Object item : badgeType.getItemIds()) {
                    Badge badge = (Badge) item;
                    if (parameter.equals(badge.getId())) {
                        badgeType.select(badge);
                        selectionChanged = true;
                        break;
                    }
                }
            }
            if (!selectionChanged) {
                // Selection wasn't changed, so load attendees for this badge type
                handler.showAttendeeList(this, parameter);
            }
        } else {
            badgeType.select(null);
            afterAttendeeFetch(new ArrayList<>());
        }
    }

    @Override
    public void refresh() {
        handler.showAttendeeList(this, (Badge) badgeType.getValue());
    }


    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }


}
