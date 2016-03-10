package org.kumoricon.view.report;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.presenter.report.CheckInByHourReportPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = CheckInByHourReportView.VIEW_NAME)
public class CheckInByHourReportView extends BaseView implements View {
    public static final String VIEW_NAME = "checkInByHourReport";
    public static final String REQUIRED_RIGHT = "view_check_in_by_hour_report";

    @Autowired
    private CheckInByHourReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private Label data = new Label();

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();

        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.showReport());
        addComponent(data);
        data.setContentMode(ContentMode.HTML);
        handler.showReport();
        setExpandRatio(data, 1f);
        data.setSizeFull();
        data.setWidthUndefined();
    }

    public void afterSuccessfulFetch(String reportData) {
        data.setValue(reportData);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
