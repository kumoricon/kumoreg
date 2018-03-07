package org.kumoricon.site.utility.closeouttill;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.BaseGridView;
import org.kumoricon.site.tillsession.ReportWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = CloseOutTillView.VIEW_NAME)
public class CloseOutTillView extends BaseGridView implements View {
    public static final String VIEW_NAME = "closeOutTill";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    private final Label description = new Label("Print cash report when you are ready to turn in your till");
    private final Button closeTillButton = new Button("Close Out Till");

    private final CloseOutTillPresenter handler;

    @Autowired
    public CloseOutTillView(CloseOutTillPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(3);
        setRows(2);
        setColumnExpandRatio(1, 5);
        addComponent(description, 1, 0);
        addComponent(closeTillButton, 1, 1);
        closeTillButton.addClickListener((Button.ClickListener) clickEvent ->
                handler.closeTill(this, getCurrentUser()));
    }

    void showData(String report) {
        ReportWindow window = new ReportWindow("Close Till", report);
        showWindow(window);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}