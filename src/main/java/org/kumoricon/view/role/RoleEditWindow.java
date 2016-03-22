package org.kumoricon.view.role;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.presenter.role.RolePresenter;
import org.kumoricon.util.FieldFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Set;

public class RoleEditWindow extends Window {

    private TextField name = FieldFactory.createTextField("Name");
    private TwinColSelect rightsList = new TwinColSelect("Rights");

    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");
    private BeanFieldGroup<Role> roleBeanFieldGroup = new BeanFieldGroup<>(Role.class);


    private RolePresenter handler;
    private RoleView parentView;

    public RoleEditWindow(RoleView parentView, RolePresenter rolePresenter, List<Right> rights) {
        super("Edit Role");
        this.handler = rolePresenter;
        this.parentView = parentView;
        setIcon(FontAwesome.GROUP);
        center();
        setModal(true);
        setResizable(false);
        setWidth(700, Unit.PIXELS);

        rightsList.setContainerDataSource(new BeanItemContainer<>(Right.class, rights));

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);

        roleBeanFieldGroup.bind(name, "name");
        form.addComponent(name);

        rightsList.setLeftColumnCaption("Available Rights");
        rightsList.setRightColumnCaption("Granted Rights");
        rightsList.setImmediate(true);
        rightsList.setSizeFull();
        form.addComponent(rightsList);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                roleBeanFieldGroup.commit();
                handler.saveRole(this, roleBeanFieldGroup.getItemDataSource().getBean());
            } catch (DataIntegrityViolationException e) {
                Notification.show("Error saving role: Constraint violation. Duplicate name?");
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancel(this));

        form.addComponent(buttons);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                roleBeanFieldGroup.commit();
                handler.saveRole(this, getRole());
            } catch (Exception e) {
                parentView.notifyError(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancel(this));

        setContent(form);
    }


    public Role getRole() {
        BeanItem<Role> roleBean = roleBeanFieldGroup.getItemDataSource();
        Role role = roleBean.getBean();
        role.clearRights();
        role.addRights((Set<Right>)rightsList.getValue());
        return role;
    }

    public void showRole(Role role) {
        roleBeanFieldGroup.setItemDataSource(role);
        for (Right r : role.getRights()) {
            rightsList.select(r);
        }
        name.selectAll();
    }

    public RolePresenter getHandler() { return handler; }
    public void setHandler(RolePresenter handler) { this.handler = handler; }

    public RoleView getParentView() { return parentView; }
}
