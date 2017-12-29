package org.kumoricon.site.report.role;

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
@SpringView(name = RoleReportView.VIEW_NAME)
public class RoleReportView extends BaseView implements View, ReportView {
    public static final String VIEW_NAME = "roleReport";
    public static final String REQUIRED_RIGHT = "view_role_report";

    private final RoleReportPresenter handler;

    private final Button refresh = new Button("Refresh");
    private final Label data = new Label();

    @Autowired
    public RoleReportView(RoleReportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.fetchReportData(this));
        addComponent(data);
        data.setContentMode(ContentMode.HTML);
        handler.fetchReportData(this);
        data.setWidth("900px");
    }



    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    @Override
    public void afterSuccessfulFetch(String report) {
        data.setValue(report);
    }
}
