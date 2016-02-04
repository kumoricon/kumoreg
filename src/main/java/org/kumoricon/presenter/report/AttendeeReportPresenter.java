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

    public String getTotalsByBadgeType() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Check Ins by Badge Type</h2>");
        sb.append("<table border=\"1\"><tr><td>Badge Type</td>");
        sb.append("<td>At-Con Checked In</td><td>At-Con Not Checked In</td>");
        sb.append("<td>Pre Reg Checked In</td><td>Pre Reg Not Checked In</td></tr>");
        List<Object[]> results = attendeeRepository.findBadgeCounts();
        for (Object[] line : results) {
            sb.append("<tr>");
            sb.append("<td>" + line[0].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[1].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[2].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[3].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[4].toString() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public void showReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTotalsByBadgeType());
        sb.append(buildTable("At Con Check Ins By Day", attendeeRepository.findAtConCheckInCountsByDate()));
        sb.append(buildTable("Pre Reg Check Ins By Day", attendeeRepository.findPreRegCheckInCountsByDate()));
        view.afterSuccessfulFetch(sb.toString());
    }

    private static String buildTable(String title, List<Object[]> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>" + title + "</h2>");
        sb.append("<table border=\"1\"><tr><td>Date</td><td> Checked In</td></tr>");
        for (Object[] line : data) {
            sb.append("<tr>");
            sb.append("<td>" + line[0].toString() + "</td>");
            sb.append("<td align=\"right\">" + line[1].toString() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}
