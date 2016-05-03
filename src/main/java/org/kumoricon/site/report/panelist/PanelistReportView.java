package org.kumoricon.site.report.panelist;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = PanelistReportView.VIEW_NAME)
public class PanelistReportView extends BaseView implements View {
    public static final String VIEW_NAME = "panelistReport";
    public static final String REQUIRED_RIGHT = "view_panelist_report";

    @Autowired
    private PanelistReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private Grid dataGrid = new Grid("Panelist List");

    @PostConstruct
    public void init() {
        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.showAttendeeList(this));

        addComponent(dataGrid);
        handler.showAttendeeList(this);
        dataGrid.setColumns(new String[] {"lastName", "firstName", "badgeName", "badgeNumber",
                "checkedIn", "checkInTime"});
        dataGrid.setEditorEnabled(false);
        dataGrid.setWidth(900, Unit.PIXELS);
        dataGrid.setHeightMode(HeightMode.ROW);
        setExpandRatio(dataGrid, .9f);
    }

    public void afterSuccessfulFetch(List<Attendee> attendees) {
        dataGrid.setContainerDataSource(new BeanItemContainer<>(Attendee.class, attendees));
        if (attendees.size() > 0) { dataGrid.setHeightByRows(attendees.size()); }
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
