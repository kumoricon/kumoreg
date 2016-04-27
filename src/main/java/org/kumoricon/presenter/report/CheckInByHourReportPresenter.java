package org.kumoricon.presenter.report;

import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.view.report.ReportView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class CheckInByHourReportPresenter implements ReportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private static final Logger log = LoggerFactory.getLogger(CheckInByHourReportPresenter.class);


    public CheckInByHourReportPresenter() {
    }

    private static String buildTable(String title, List<Object[]> data) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h2>%s</h2>", title));
        sb.append("<table border=\"1\" cellpadding=\"2\"><tr>");
        sb.append("<td>Date</td><td>Hour</td><td>At Con Checked In</td><td>PreReg Checked In</td><td>Total</td></tr>");
        for (Object[] line : data) {
            sb.append("<tr>");
            sb.append(String.format("<td>%s</td>", line[0].toString()));
            sb.append(String.format("<td align=\"right\">%s</td>", line[1].toString()));
            sb.append(String.format("<td align=\"right\">%s</td>", line[2].toString()));
            sb.append(String.format("<td align=\"right\">%s</td>", line[3].toString()));
            sb.append(String.format("<td align=\"right\">%s</td>", line[4].toString()));
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    public void fetchReportData(ReportView view) {
        String report = buildTable("Check Ins By Hour", attendeeRepository.findCheckInCountsByHour());
        view.afterSuccessfulFetch(report);
        log.info("{} viewed Check Ins By Hour Report", view.getCurrentUser());
    }
}
