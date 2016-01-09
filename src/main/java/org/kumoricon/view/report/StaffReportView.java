package org.kumoricon.view.report;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.model.user.User;
import org.kumoricon.presenter.report.StaffReportPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = StaffReportView.VIEW_NAME)
public class StaffReportView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "staffReport";
    @Autowired
    private StaffReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private Grid dataGrid = new Grid("User List");

    private BeanFieldGroup<User> userBeanFieldGroup = new BeanFieldGroup<>(User.class);


    @PostConstruct
    public void init() {
        handler.setView(this);
        setSpacing(true);
        setMargin(true);

        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.showUserList());

        addComponent(dataGrid);
        handler.showUserList();
        dataGrid.setColumns(new String[] {"lastName", "firstName", "username", "phone", "role"});
        dataGrid.setEditorEnabled(false);

        setSizeFull();
        setExpandRatio(dataGrid, 1f);
        dataGrid.setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    public void setHandler(StaffReportPresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<User> users) {
        dataGrid.setContainerDataSource(new BeanItemContainer<User>(User.class, users));
    }

}
