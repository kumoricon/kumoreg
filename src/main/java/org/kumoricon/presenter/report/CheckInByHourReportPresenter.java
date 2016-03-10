package org.kumoricon.presenter.report;

import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.view.report.CheckInByHourReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class CheckInByHourReportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private CheckInByHourReportView view;

    public CheckInByHourReportPresenter() {
    }

    public CheckInByHourReportView getView() { return view; }
    public void setView(CheckInByHourReportView view) { this.view = view; }

    public void showReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTable("Check Ins By Hour", attendeeRepository.findCheckInCountsByHour()));
        view.afterSuccessfulFetch(sb.toString());
    }

    private static String buildTable(String title, List<Object[]> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>" + title + "</h2>");
        sb.append("<table border=\"1\" cellpadding=\"2\"><tr>");
        sb.append("<td>Date</td><td>Hour</td><td>At Con Checked In</td><td>PreReg Checked In</td><td>Total</td></tr>");
        for (Object[] line : data) {
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

}
