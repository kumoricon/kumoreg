package org.kumoricon.view.role;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.presenter.role.RolePresenter;
import org.kumoricon.util.FieldFactory;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@ViewScope
@SpringView(name = RoleView.VIEW_NAME)
public class RoleView extends BaseView implements View {
    public static final String VIEW_NAME = "roles";
    public static final String REQUIRED_RIGHT = "manage_roles";

    @Autowired
    private RolePresenter handler;

    private TextField name = FieldFactory.createTextField("Name");
    private TwinColSelect rightsList = new TwinColSelect("Rights");

    private Button btnAddNew = new Button("Add");
    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");

    private ListSelect roleList = new ListSelect("Roles");

    private BeanFieldGroup<Role> roleBeanFieldGroup = new BeanFieldGroup<>(Role.class);

    private Layout leftPanel;
    private Layout rightPanel;

    @PostConstruct
    public void init() {
        leftPanel = buildLeftPanel();
        rightPanel = buildRightPanel();
        addComponent(leftPanel);
        addComponent(rightPanel);

        handler.showRoleList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            hideRoleForm();
            clearRoleForm();
        } else {
            handler.navigateToRole(this, viewChangeEvent.getParameters());
        }
    }

    public void setHandler(RolePresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<Role> roles) {
        roleList.setContainerDataSource(new BeanItemContainer<Role>(Role.class, roles));
    }

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        roleList.setCaption("Roles");
        roleList.setNullSelectionAllowed(false);
        roleList.setWidth(500, Unit.PIXELS);
        roleList.setImmediate(true);
        leftPanel.addComponent(roleList);
        leftPanel.addComponent(btnAddNew);

        roleList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                handler.roleSelected(this, (Role)valueChangeEvent.getProperty().getValue()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            roleList.select(null);
            handler.addNewRole(this);
        });

        return leftPanel;
    }

    private FormLayout buildRightPanel() {

        FormLayout form = new FormLayout();
        form.setVisible(false);
        form.setMargin(true);
        form.setSpacing(true);

        roleBeanFieldGroup.bind(name, "name");
        form.addComponent(name);

        rightsList.setLeftColumnCaption("Available Rights");
        rightsList.setRightColumnCaption("Granted Rights");
        rightsList.setImmediate(true);
        form.addComponent(rightsList);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                roleBeanFieldGroup.commit();
                handler.saveRole(this);
            } catch (DataIntegrityViolationException e) {
                Notification.show("Error saving role: Constraint violation. Duplicate name?");
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancel(this));

        form.addComponent(buttons);

        return form;
    }

    public void clearRoleForm() {
        name.clear();
    }

    public void showRole(Role role) {
        clearRoleForm();
        showRoleForm();
        roleBeanFieldGroup.setItemDataSource(role);
        for (Right r : role.getRights()) {
            rightsList.select(r);
        }
        name.selectAll();
    }

    public void hideRoleForm() {
        rightPanel.setVisible(false);
    }
    public void showRoleForm() { rightPanel.setVisible(true);}

    public void selectRole(Role role) {
        roleList.select(role);
    }

    public void clearSelection() {
        roleList.select(null);
    }

    public Role getRole() {
        BeanItem<Role> roleBean = roleBeanFieldGroup.getItemDataSource();
        Role role = roleBean.getBean();
        role.clearRights();
        role.addRights((Set<Right>)rightsList.getValue());
        return role;
    }

    public void setAvailableRights(List<Right> all) {
        rightsList.setContainerDataSource(new BeanItemContainer<Right>(Right.class, all));
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}