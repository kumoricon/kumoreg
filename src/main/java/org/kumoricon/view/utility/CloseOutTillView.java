package org.kumoricon.view.utility;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.kumoricon.presenter.utility.CloseOutTillPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = CloseOutTillView.VIEW_NAME)
public class CloseOutTillView extends BaseView implements View {
    public static final String VIEW_NAME = "closeouttill";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    private Label description = new Label("Print cash report when you are ready to turn in your till");
    private Button closeTillButton = new Button("Close Out Till");
    private Label data = new Label();

    @Autowired
    private CloseOutTillPresenter handler;

    @PostConstruct
    public void init() {
        handler.setView(this);

        setSizeFull();

        addComponent(description);
        addComponent(closeTillButton);
        addComponent(data);
        data.setContentMode(ContentMode.PREFORMATTED);
        setExpandRatio(data, 1f);
        data.setSizeFull();

        closeTillButton.addClickListener((Button.ClickListener) clickEvent -> handler.closeTill(getCurrentUser()));
    }

    public void showData(String report) { data.setValue(report); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}