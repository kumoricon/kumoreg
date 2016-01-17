package org.kumoricon.view.utility;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.presenter.utility.LoadTestDataPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


@SpringView(name = LoadTestDataView.VIEW_NAME)
public class LoadTestDataView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "testdata";

    @Autowired
    private LoadTestDataPresenter handler;

    Button loadData = new Button("Load Data");
    TextArea results = new TextArea();

    @PostConstruct
    void init() {
        handler.setView(this);
        setMargin(true);
        setSpacing(true);
        addComponent(loadData);
        addComponent(results);
        results.setSizeFull();
        results.setEnabled(false);
        setExpandRatio(results, 1.0f);
        setSizeFull();

        loadData.addClickListener((Button.ClickListener) clickEvent -> handler.loadDataButtonClicked());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // the view is constructed in the init() method()
    }

    public void addResult(String message) {
        results.setValue(results.getValue() + message + "\n");
    }
}