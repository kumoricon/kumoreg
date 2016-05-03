package org.kumoricon.site.role;

import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class RolePresenter {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RightRepository rightRepository;

    private static final Logger log = LoggerFactory.getLogger(RolePresenter.class);


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
        log.info("{} added new role", view.getCurrentUser());
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
        log.info("{} saved role {}", view.getCurrentUser(), role);
    }

    public void showRoleList(RoleView view) {
        List<Role> roles = roleRepository.findAll();
        view.afterSuccessfulFetch(roles);
        log.info("{} viewed role list", view.getCurrentUser());
    }

    public void navigateToRole(RoleView view, String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            Role role = roleRepository.findOne(id);
            view.selectRole(role);
            log.info("{} viewed role {}", view.getCurrentUser(), role);
        }
    }
}
