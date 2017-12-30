package org.kumoricon.site.role;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = RoleEditView.VIEW_NAME)
class RoleEditView extends BaseView implements View {

    private final TextField nameField = new TextField("Name");
    private final TwinColSelect<Right> rightsList = new TwinColSelect<>("Rights");

    private final Button btnSave = new Button("Save");
    private final Button btnCancel = new Button("Cancel");

    private final RolePresenter handler;
    public static final String VIEW_NAME = "role";
    public static final String REQUIRED_RIGHT = "manage_roles";
    private Role currentRole;

    @Autowired
    public RoleEditView(RolePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.addComponents(btnSave, btnCancel);
        buttons.setWidthUndefined();
        buttons.setSpacing(false);

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);

        nameField.setWidth("300px");
        rightsList.setWidth("800px");
        rightsList.setHeight("500px");
        form.addComponents(nameField, rightsList);
        form.setWidthUndefined();

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                currentRole.setName(nameField.getValue());
                currentRole.setRights(rightsList.getSelectedItems());
                handler.saveRole(this, currentRole);
            } catch (DataIntegrityViolationException e) {
                Notification.show("Error saving user: Constraint violation. Duplicate username or badge prefix?");
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> navigateTo(RoleListView.VIEW_NAME));

        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnCancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        addComponents(form, buttons);
    }


    void showRole(Role role, List<Right> availableRights) {
        rightsList.setItems(availableRights);
        this.currentRole = role;
        nameField.setValue(role.getName());
        rightsList.setValue(role.getRights());
        nameField.focus();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        handler.showRole(this, parameters);
    }

}
