package org.kumoricon.site.report.staff;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = StaffReportView.VIEW_NAME)
public class StaffReportView extends BaseGridView implements View {
    public static final String VIEW_NAME = "staffReport";
    public static final String REQUIRED_RIGHT = "view_staff_report";

    private final StaffReportPresenter handler;

    private final Button btnRefresh = new Button("Refresh");
    private final Grid<User> dataGrid = new Grid<>("");

    @Autowired
    public StaffReportView(StaffReportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(4);
        setRows(1);
        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(3, 10);
        btnRefresh.addClickListener((Button.ClickListener) clickEvent -> handler.showUserList(this));

        addComponent(dataGrid, 1, 0);
        addComponent(btnRefresh, 2, 0);
        handler.showUserList(this);
        dataGrid.addColumn(User::getLastName).setCaption("Last Name");
        dataGrid.addColumn(User::getFirstName).setCaption("First Name");
        dataGrid.addColumn(User::getUsername).setCaption("User Name");
        dataGrid.addColumn(User::getPhone).setCaption("Phone Number");
        dataGrid.addColumn(User::getRole, Role::getName).setCaption("Role");
        dataGrid.getEditor().setEnabled(false);
        dataGrid.setHeightMode(HeightMode.ROW);
        dataGrid.setSelectionMode(Grid.SelectionMode.NONE);
        dataGrid.setWidth("600px");
        dataGrid.addStyleName("kumoHeaderOnlyHandPointer");
    }

    public void afterSuccessfulFetch(List<User> users) {
        dataGrid.setItems(users);
        if (users.size() > 0) { dataGrid.setHeightByRows(users.size()); }
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
