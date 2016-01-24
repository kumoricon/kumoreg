package org.kumoricon.view.report;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.presenter.report.RoleReportPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TreeSet;

@ViewScope
@SpringView(name = RoleReportView.VIEW_NAME)
public class RoleReportView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "roleReport";
    @Autowired
    private RoleReportPresenter handler;

    private Button refresh = new Button("Refresh");
    private TextArea data = new TextArea();

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSpacing(true);
        setMargin(true);

        addComponent(refresh);
        refresh.addClickListener((Button.ClickListener) clickEvent -> handler.showRoleList());

        addComponent(data);
        handler.showRoleList();

        setSizeFull();
        setExpandRatio(data, 1f);
        data.setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
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

}
