package org.kumoricon.site.role;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.VerticalLayout;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = RoleView.VIEW_NAME)
public class RoleView extends BaseView implements View {
    public static final String VIEW_NAME = "roles";
    public static final String REQUIRED_RIGHT = "manage_roles";

    private final RolePresenter handler;

    private final Button btnAddNew = new Button("Add");
    private final ListSelect roleList = new ListSelect("Roles");

    @Autowired
    public RoleView(RolePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        roleList.setCaption("");
        roleList.setNullSelectionAllowed(false);
        roleList.setWidth(500, Unit.PIXELS);
        roleList.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        roleList.setItemCaptionPropertyId("name");

        roleList.setImmediate(true);
        addComponent(btnAddNew);
        addComponent(roleList);

        roleList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                handler.roleSelected(this, (Role)valueChangeEvent.getProperty().getValue()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            roleList.select(null);
            handler.addNewRole(this);
        });

        handler.showRoleList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.equals("")) {
            handler.navigateToRole(this, viewChangeEvent.getParameters());
        }
    }

    public void afterSuccessfulFetch(List<Role> roles) {
        roleList.setContainerDataSource(new BeanItemContainer<>(Role.class, roles));
    }

    void showRole(Role role, List<Right> rights) {
        RoleEditWindow window = new RoleEditWindow(this, handler, rights);
        window.showRole(role);
        showWindow(window);
    }

    void selectRole(Role role) {
        roleList.select(role);
    }

    void clearSelection() {
        roleList.select(null);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}