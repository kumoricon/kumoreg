package org.kumoricon.view.report;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.presenter.report.RoleReportPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = RoleReportView.VIEW_NAME)
public class RoleReportView extends BaseView implements View, ReportView {
    public static final String VIEW_NAME = "roleReport";
    public static final String REQUIRED_RIGHT = "view_role_report";

    @Autowired
    private RoleReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private Label data = new Label();

    @PostConstruct
    public void init() {
        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.fetchReportData(this));
        addComponent(data);
        data.setContentMode(ContentMode.HTML);
        handler.fetchReportData(this);
        setExpandRatio(data, 1f);
        data.setSizeFull();
    }



    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    @Override
    public void afterSuccessfulFetch(String report) {
        data.setValue(report);
    }
}
