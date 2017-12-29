package org.kumoricon.site.utility.closeouttill;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Label;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.tillsession.ReportWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = CloseOutTillView.VIEW_NAME)
public class CloseOutTillView extends BaseView implements View {
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
        addComponent(description);
        addComponent(closeTillButton);
        closeTillButton.addClickListener((Button.ClickListener) clickEvent ->
                handler.closeTill(this, getCurrentUser()));
    }

    void showData(String report) {
        ReportWindow window = new ReportWindow("Close Till", report);
        showWindow(window);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}