package org.kumoricon.view.report;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.presenter.report.RoleReportPresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TreeSet;

@ViewScope
@SpringView(name = RoleReportView.VIEW_NAME)
public class RoleReportView extends BaseView implements View {
    public static final String VIEW_NAME = "roleReport";
    public static final String REQUIRED_RIGHT = "view_role_report";

    @Autowired
    private RoleReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private TextArea data = new TextArea();

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();

        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.showRoleList());

        addComponent(data);
        data.setEnabled(false);
        setExpandRatio(data, 1f);
        data.setSizeFull();

        handler.showRoleList();
    }

    public void afterSuccessfulFetch(List<Role> roles) {
        StringBuilder output = new StringBuilder();
        for (Role role : roles) {
            output.append("Role: " + role.getName() + "\n");
            TreeSet<Right> rights = new TreeSet<>(role.getRights());
            for (Right right : rights) {
                output.append("    " + right.getName() + "\n");
            }
            output.append("\n\n");
        }
        data.setValue(output.toString());
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
