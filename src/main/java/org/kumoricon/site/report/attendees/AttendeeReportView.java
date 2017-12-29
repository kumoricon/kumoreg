package org.kumoricon.site.report.attendees;

import com.vaadin.navigator.View;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Label;
import org.kumoricon.site.report.ReportView;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = AttendeeReportView.VIEW_NAME)
public class AttendeeReportView extends BaseView implements View, ReportView {
    public static final String VIEW_NAME = "attendeeReport";
    public static final String REQUIRED_RIGHT = "view_attendance_report";
    private final AttendeeReportPresenter handler;

    private final Button refresh = new Button("Refresh");
    private final Label data = new Label();

    @Autowired
    public AttendeeReportView(AttendeeReportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.fetchReportData(this));
        addComponent(data);
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
