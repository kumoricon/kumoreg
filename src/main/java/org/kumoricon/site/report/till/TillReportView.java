package org.kumoricon.site.report.till;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import org.kumoricon.BaseGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = org.kumoricon.site.report.till.TillReportView.VIEW_NAME)
public class TillReportView extends BaseGridView implements View {
    public static final String VIEW_NAME = "tillReport";
    public static final String REQUIRED_RIGHT = "view_till_report";

    private final Button btnRefresh = new Button("Refresh");
    private final Label data = new Label();

    private final TillReportPresenter handler;

    @Autowired
    public TillReportView(TillReportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(2);
        setRows(1);
        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(1, 1);
        addComponent(data, 0, 0);
        addComponent(btnRefresh, 1, 0);
        data.setContentMode(ContentMode.HTML);
        data.setWidth("100%");
        data.setHeightUndefined();

        btnRefresh.addClickListener((Button.ClickListener) clickEvent -> {
            btnRefresh.setCaption("Refresh");
            handler.showAllTills(this);
        });

        handler.showAllTills(this);
    }

    public void showData(String report) { data.setValue(report); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}