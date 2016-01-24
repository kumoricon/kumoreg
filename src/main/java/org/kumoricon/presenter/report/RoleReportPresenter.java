package org.kumoricon.presenter.report;

import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.view.report.RoleReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class RoleReportPresenter {
    @Autowired
    private RoleRepository roleRepository;

    private RoleReportView view;

    public RoleReportPresenter() {
    }

    public void showRoleList() {
        List<Role> roles = roleRepository.findAll();
        view.afterSuccessfulFetch(roles);
    }

    public RoleReportView getView() { return view; }
    public void setView(RoleReportView view) { this.view = view; }

    public RoleRepository getRoleRepository() { return roleRepository; }
    public void setRoleRepository(RoleRepository roleRepository) { this.roleRepository = roleRepository; }
}
