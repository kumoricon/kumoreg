package org.kumoricon.site.report.attendees;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.BaseGridView;
import org.kumoricon.site.report.ReportView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = AttendeeReportView.VIEW_NAME)
public class AttendeeReportView extends BaseGridView implements View, ReportView {
    public static final String VIEW_NAME = "attendeeReport";
    public static final String REQUIRED_RIGHT = "view_attendance_report";
    private final AttendeeReportPresenter handler;

    private final Button btnRefresh = new Button("Refresh");
    private final Label data = new Label();

    @Autowired
    public AttendeeReportView(AttendeeReportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(4);
        setRows(1);
        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(3, 10);
        btnRefresh.addClickListener((Button.ClickListener) clickEvent -> handler.fetchReportData(this));
        addComponent(data, 1, 0);
        addComponent(btnRefresh, 2, 0);
        data.setContentMode(ContentMode.HTML);
        data.setWidth("800px");
        handler.fetchReportData(this);
    }

    @Override
    public void afterSuccessfulFetch(String data) {
        this.data.setValue(data);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
