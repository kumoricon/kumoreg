package org.kumoricon.presenter.report;

import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.view.report.AttendeeReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class AttendeeReportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private AttendeeReportView view;

    public AttendeeReportPresenter() {
    }

    public AttendeeReportView getView() { return view; }
    public void setView(AttendeeReportView view) { this.view = view; }

    public void showReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\"><tr><td>Badge Type</td><td>Checked In</td><td>Not Checked In</td></tr>");
        List<Object[]> results = attendeeRepository.findBadgeCounts();
        for (Object[] line : results) {
            sb.append("<tr>");
            sb.append("<td>" + line[0].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[1].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[2].toString() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        view.afterSuccessfulFetch(sb.toString());
    }
}
