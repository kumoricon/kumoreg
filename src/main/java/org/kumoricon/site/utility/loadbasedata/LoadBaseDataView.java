package org.kumoricon.site.utility.loadbasedata;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


@SpringView(name = LoadBaseDataView.VIEW_NAME)
public class LoadBaseDataView extends BaseView implements View {
    public static final String VIEW_NAME = "baseData";
    public static final String REQUIRED_RIGHT = "load_base_data";

    @Autowired
    private LoadBaseDataPresenter handler;

    private Label description = new Label("Load base rights, roles, and example users");
    private Button loadData = new Button("Load Data");
    private TextArea results = new TextArea();

    @PostConstruct
    void init() {
        setSizeFull();
        addComponent(description);
        addComponent(loadData);
        addComponent(results);
        results.setSizeFull();
        results.setEnabled(false);
        setExpandRatio(results, 1.0f);

        loadData.addClickListener((Button.ClickListener) clickEvent -> handler.loadDataButtonClicked(this));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    public void addResult(String message) {
        results.setValue(results.getValue() + message + "\n");
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}