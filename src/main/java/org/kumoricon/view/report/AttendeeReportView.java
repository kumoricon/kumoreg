package org.kumoricon.view.report;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.presenter.report.AttendeeReportPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = AttendeeReportView.VIEW_NAME)
public class AttendeeReportView extends BaseView implements View {
    public static final String VIEW_NAME = "attendeeReport";
    public static final String REQUIRED_RIGHT = "view_attendee_report";
    @Autowired
    private AttendeeReportPresenter handler;

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

    public void afterSuccessfulFetch(String data) {
        this.data.setValue(data);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
