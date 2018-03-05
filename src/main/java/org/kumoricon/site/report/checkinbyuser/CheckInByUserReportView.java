package org.kumoricon.site.report.checkinbyuser;

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
@SpringView(name = CheckInByUserReportView.VIEW_NAME)
public class CheckInByUserReportView extends BaseGridView implements View, ReportView {
    public static final String VIEW_NAME = "checkInByUserReport";
    public static final String REQUIRED_RIGHT = "view_check_in_by_user_report";

    private final CheckInByUserReportPresenter handler;

    private final Button btnRefresh = new Button("Refresh");
    private final Label data = new Label();

    @Autowired
    public CheckInByUserReportView(CheckInByUserReportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(2);
        setRows(1);
        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(1, 1);

        btnRefresh.addClickListener((Button.ClickListener) clickEvent -> handler.fetchReportData(this));
        addComponent(data, 0, 0);
        addComponent(btnRefresh, 1, 0);
        data.setContentMode(ContentMode.HTML);
        handler.fetchReportData(this);
        data.setWidth("650px");
    }

    @Override
    public void afterSuccessfulFetch(String reportData) {
        data.setValue(reportData);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
