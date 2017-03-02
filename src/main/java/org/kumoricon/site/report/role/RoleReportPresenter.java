package org.kumoricon.site.report.role;

import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.site.report.ReportPresenter;
import org.kumoricon.site.report.ReportView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class RoleReportPresenter implements ReportPresenter {
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(RoleReportPresenter.class);

    @Autowired
    public RoleReportPresenter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private static String buildTable(String title, List<Role> roles) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h2>%s</h2>", title));
        sb.append("<table border=\"1\" cellpadding=\"2\"><tr>");
        sb.append("<td>Role</td><td>Rights</td></tr>");
        for (Role role : roles) {
            sb.append("<tr>");
            sb.append(String.format("<td>%s</td>", role.getName()));
            sb.append("<td>");
            sb.append("<table width=\"100%\">");
            for (Right right : role.getRights()) {
                if (right.getDescription() == null) {
                    sb.append(String.format("<tr><td>%s</td><td></td></tr>", right.getName()));
                } else {
                    sb.append(String.format("<tr><td>%s</td><td>(%s)</td></tr>",
                            right.getName(), right.getDescription()));
                }
            }
            sb.append("</table>");
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
        log.info("{} viewed Role Report", view.getCurrentUser());
    }
}
