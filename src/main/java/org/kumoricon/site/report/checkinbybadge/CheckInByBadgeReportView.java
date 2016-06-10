package org.kumoricon.site.report.checkinbybadge;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = CheckInByBadgeReportView.VIEW_NAME)
public class CheckInByBadgeReportView extends BaseView implements View {
    public static final String VIEW_NAME = "checkInByBadgeReport";
    public static final String REQUIRED_RIGHT = "view_check_in_by_badge_report";

    @Autowired
    private CheckInByBadgeReportPresenter handler;

    private Label badgeTypeLbl = new Label("Badge Type: ");
    private ComboBox badgeType = new ComboBox();
    private Button refresh = new Button("Refresh");
    private Grid dataGrid = new Grid("Attendees");

    @PostConstruct
    public void init() {
        HorizontalLayout h = new HorizontalLayout();
        h.setSpacing(true);
        badgeType.setPageLength(15);
        badgeType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badgeType.setItemCaptionPropertyId("name");

        h.addComponent(badgeTypeLbl);
        h.addComponent(badgeType);
        h.addComponent(refresh);
        addComponent(h);
        badgeType.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (event.getProperty() != null) {
                badgeSelected((Badge) event.getProperty().getValue());
            }
        });
        refresh.addClickListener((Button.ClickListener) clickEvent ->
                badgeSelected((Badge) badgeType.getValue()));

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

    private void badgeSelected(Badge badge) {
        handler.showAttendeeList(this, badge);
    }

    public void afterAttendeeFetch(List<Attendee> attendees) {
        dataGrid.setContainerDataSource(new BeanItemContainer<>(Attendee.class, attendees));
        if (attendees.size() > 0) {
            if (attendees.size() < 20) {
                dataGrid.setHeightByRows(attendees.size());
            } else {
                dataGrid.setHeightByRows(20);
            }
        }
    }

    public void afterBadgeTypeFetch(List<Badge> badges) {
        badgeType.setContainerDataSource(new BeanItemContainer<>(Badge.class, badges));
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
