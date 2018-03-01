package org.kumoricon.site.role;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import org.kumoricon.model.role.Role;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = RoleListView.VIEW_NAME)
public class RoleListView extends BaseView implements View {
    public static final String VIEW_NAME = "roles";
    public static final String REQUIRED_RIGHT = "manage_roles";

    private final RolePresenter handler;

    private final Button btnAddNew = new Button("Add New");
    private final Grid<Role> roleGrid = new Grid<>();

    @Autowired
    public RoleListView(RolePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        roleGrid.setWidth("500px");
        roleGrid.setHeightMode(HeightMode.UNDEFINED);
        roleGrid.addColumn(Role::getId).setCaption("Id").setWidth(50);
        roleGrid.addColumn(Role::getName).setCaption("Name");
        roleGrid.setSelectionMode(Grid.SelectionMode.NONE);
        roleGrid.addStyleName("kumoHandPointer");
        addComponents(roleGrid, btnAddNew);

        roleGrid.addItemClickListener((ItemClickListener<Role>) itemClick ->
                navigateTo(RoleEditView.VIEW_NAME + "/" + itemClick.getItem().getId()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addNewRole(this);
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        handler.showRoleList(this);
    }

    public void afterSuccessfulFetch(List<Role> roles) {
        roleGrid.setItems(roles);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}