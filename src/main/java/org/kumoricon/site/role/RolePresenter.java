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
    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;
    private static final Logger log = LoggerFactory.getLogger(RolePresenter.class);


    @Autowired
    public RolePresenter(RoleRepository roleRepository, RightRepository rightRepository) {
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
    }

    void addNewRole(RoleListView view) {
        log.info("{} adding new role", view.getCurrentUsername());
        view.navigateTo(RoleEditView.VIEW_NAME);
    }

    void saveRole(RoleEditView view, Role role) {
        roleRepository.save(role);
        view.navigateTo(RoleListView.VIEW_NAME);
        log.info("{} saved role {}", view.getCurrentUsername(), role);
    }

    void showRoleList(RoleListView view) {
        List<Role> roles = roleRepository.findAll();
        view.afterSuccessfulFetch(roles);
        log.info("{} viewed role list", view.getCurrentUsername());
    }

    public void showRole(RoleEditView view, String parameters) {
        Integer roleId;
        try {
            roleId = Integer.parseInt(parameters);
            Role role = roleRepository.findOne(roleId);
            view.showRole(role, rightRepository.findAll());
        } catch (NumberFormatException ex) {
            Role role = new Role();
            view.showRole(role, rightRepository.findAll());
        }
    }
}
