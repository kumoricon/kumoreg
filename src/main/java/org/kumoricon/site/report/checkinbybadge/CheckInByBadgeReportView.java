package org.kumoricon.site.report.checkinbybadge;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = CheckInByBadgeReportView.VIEW_NAME)
public class CheckInByBadgeReportView extends BaseView implements View {
    public static final String VIEW_NAME = "checkInByBadgeReport";
    public static final String REQUIRED_RIGHT = "view_check_in_by_badge_report";

    @Autowired
    private CheckInByBadgeReportPresenter handler;

    private Label badgeTypeLabel = new Label("Badge Type:" );
    private ComboBox badgeType = new ComboBox();
    private Button refresh = new Button("Refresh");
    private Grid dataGrid = new Grid("Attendees");

    @PostConstruct
    public void init() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        badgeType.setPageLength(15);
        badgeType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badgeType.setItemCaptionPropertyId("name");
        badgeType.setTextInputAllowed(false);
        badgeType.setNullSelectionAllowed(false);
        badgeType.setNewItemsAllowed(false);
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
        dataGrid.setEditorEnabled(false);
        dataGrid.setSelectionMode(Grid.SelectionMode.NONE);
        dataGrid.setWidth(1100, Unit.PIXELS);
        dataGrid.setHeightMode(HeightMode.ROW);
        setExpandRatio(dataGrid, .9f);
    }

    public void afterAttendeeFetch(List<Attendee> attendees) {
        if (attendees.size() < 1) {
            dataGrid.setHeightByRows(1);
        } else if (attendees.size() < 20) {
            dataGrid.setHeightByRows(attendees.size());
        } else {
            dataGrid.setHeightByRows(20);
        }
        dataGrid.setContainerDataSource(new BeanItemContainer<>(Attendee.class, attendees));
    }

    public void afterBadgeTypeFetch(List<Badge> badges) {
        badgeType.setContainerDataSource(new BeanItemContainer<>(Badge.class, badges));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
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

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
