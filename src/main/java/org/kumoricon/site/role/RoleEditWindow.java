package org.kumoricon.site.role;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.attendee.FieldFactory;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;

import java.util.List;
import java.util.Set;

class RoleEditWindow extends Window {

    private final TextField name = FieldFactory.createTextField("Name");
    private final TwinColSelect rightsList = new TwinColSelect("Rights");

    private final Button btnSave = new Button("Save");
    private final Button btnCancel = new Button("Cancel");
    private final BeanFieldGroup<Role> roleBeanFieldGroup = new BeanFieldGroup<>(Role.class);


    private final RolePresenter handler;
    private final RoleListView parentView;

    RoleEditWindow(RoleListView parentView, RolePresenter rolePresenter, List<Right> rights) {
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
        form.addComponent(buttons);

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancel(this));

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                roleBeanFieldGroup.commit();
//                handler.saveRole(this, getRole());
            } catch (Exception e) {
                parentView.notifyError(e.getMessage());
            }
        });

        setContent(form);
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }


    private Role getRole() {
        BeanItem<Role> roleBean = roleBeanFieldGroup.getItemDataSource();
        Role role = roleBean.getBean();
        role.clearRights();
        role.addRights((Set<Right>)rightsList.getValue());
        return role;
    }

    void showRole(Role role) {
        roleBeanFieldGroup.setItemDataSource(role);
        for (Right r : role.getRights()) {
            rightsList.select(r);
        }
        name.selectAll();
    }

    public RoleListView getParentView() { return parentView; }
}
