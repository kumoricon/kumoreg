package org.kumoricon.presenter.report;

import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.view.report.ReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class RoleReportPresenter implements ReportPresenter {
    @Autowired
    private RoleRepository roleRepository;

    public RoleReportPresenter() {
    }

    public RoleRepository getRoleRepository() { return roleRepository; }
    public void setRoleRepository(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

    private static String buildTable(String title, List<Role> roles) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h2>%s</h2>", title));
        sb.append("<table border=\"1\" cellpadding=\"2\"><tr>");
        sb.append("<td>Role</td><td>Rights</td></tr>");
        for (Role role : roles) {
            sb.append("<tr>");
            sb.append(String.format("<td>%s</td>", role.getName()));
            sb.append("<td>");
            for (Right right : role.getRights()) {
                sb.append(String.format("%s ", right.getName()));
            }
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    public void fetchReportData(ReportView view) {
        List<Role> roles = roleRepository.findAll();
        String report = buildTable("Role List", roles);
        view.afterSuccessfulFetch(report);
    }
}
