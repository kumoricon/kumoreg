package org.kumoricon.site.report.role;

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
@SpringView(name = RoleReportView.VIEW_NAME)
public class RoleReportView extends BaseGridView implements View, ReportView {
    public static final String VIEW_NAME = "roleReport";
    public static final String REQUIRED_RIGHT = "view_role_report";

    private final RoleReportPresenter handler;

    private final Button btnRefresh = new Button("Refresh");
    private final Label data = new Label();

    @Autowired
    public RoleReportView(RoleReportPresenter handler) {
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
        handler.fetchReportData(this);
        data.setWidth("1000px");
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    @Override
    public void afterSuccessfulFetch(String report) {
        data.setValue(report);
    }
}
