package org.kumoricon.presenter.role;

import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.view.role.RoleView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class RolePresenter {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RightRepository rightRepository;

    private RoleView view;

    public RolePresenter() {
    }


    public void roleSelected(Role role) {
        if (role != null) {
            view.navigateTo(view.VIEW_NAME + "/" + role.getId().toString());
            view.setAvailableRights(rightRepository.findAll());
            view.showRole(role);
        }
    }

    public void roleSelected(Integer id) {
        if (id != null) {
            Role role = roleRepository.findOne(id);
            roleSelected(role);
        }
    }

    public void addNewRole() {
        view.clearRoleForm();
        view.showRoleForm();
        view.navigateTo(RoleView.VIEW_NAME);
        Role role = new Role();
        view.showRole(role);
    }

    public void cancel() {
        view.navigateTo(RoleView.VIEW_NAME);
        view.clearRoleForm();
        view.hideRoleForm();
        view.clearSelection();
    }

    public void saveRole() {
        Role role = view.getRole();

        roleRepository.save(role);
        view.navigateTo(RoleView.VIEW_NAME);
        showRoleList();
    }

    public void showRoleList() {
        List<Role> roles = roleRepository.findAll();
        view.afterSuccessfulFetch(roles);
    }

    public void navigateToRole(String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            Role role = roleRepository.findOne(id);
            view.selectRole(role);
        }
    }


    public RoleView getView() { return view; }
    public void setView(RoleView roleView) { this.view = roleView; }

}
