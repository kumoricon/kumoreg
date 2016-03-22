package org.kumoricon.presenter.role;

import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.view.role.RoleEditWindow;
import org.kumoricon.view.role.RoleView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class RolePresenter {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RightRepository rightRepository;

    public RolePresenter() {
    }

    public void roleSelected(RoleView view, Role role) {
        if (role != null) {
            view.navigateTo(RoleView.VIEW_NAME + "/" + role.getId().toString());
            view.showRole(role, rightRepository.findAll());
        }
    }

    public void roleSelected(RoleView view, Integer id) {
        if (id != null) {
            Role role = roleRepository.findOne(id);
            roleSelected(view, role);
        }
    }

    public void addNewRole(RoleView view) {
        view.navigateTo(RoleView.VIEW_NAME);
        Role role = new Role();
        view.showRole(role, rightRepository.findAll());
    }

    public void cancel(RoleEditWindow window) {
        RoleView view = window.getParentView();
        window.close();
        view.navigateTo(RoleView.VIEW_NAME);
        view.clearSelection();
    }

    public void saveRole(RoleEditWindow window, Role role) {
        RoleView view = window.getParentView();
        roleRepository.save(role);
        window.close();
        view.navigateTo(RoleView.VIEW_NAME);
        showRoleList(view);
    }

    public void showRoleList(RoleView view) {
        List<Role> roles = roleRepository.findAll();
        view.afterSuccessfulFetch(roles);
    }

    public void navigateToRole(RoleView view, String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            Role role = roleRepository.findOne(id);
            view.selectRole(role);
        }
    }
}
