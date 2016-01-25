package org.kumoricon.view.report;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import org.kumoricon.model.report.ReportLine;
import org.kumoricon.presenter.report.AttendeeReportPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = AttendeeReportView.VIEW_NAME)
public class AttendeeReportView extends BaseView implements View {
    public static final String VIEW_NAME = "attendeeReport";
    public static final String REQUIRED_RIGHT = "view_staff_report";
    @Autowired
    private AttendeeReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private Grid dataGrid = new Grid("Attendee Statistics");

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();

        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.showReport());
        addComponent(dataGrid);
        dataGrid.setEditorEnabled(false);
        dataGrid.setSelectionMode(Grid.SelectionMode.NONE);
        handler.showReport();
        for (Grid.Column c : dataGrid.getColumns()) {
            c.setSortable(false);
        }
        setExpandRatio(dataGrid, 1f);
        dataGrid.setSizeFull();
        dataGrid.setWidthUndefined();

    }

    public void afterSuccessfulFetch(List<ReportLine> data) {
        dataGrid.setContainerDataSource(new BeanItemContainer<>(ReportLine.class, data));
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
