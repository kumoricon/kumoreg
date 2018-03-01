package org.kumoricon.site.report.checkinbyuser;

import org.kumoricon.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.site.report.ReportPresenter;
import org.kumoricon.site.report.ReportView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class CheckInByUserReportPresenter implements ReportPresenter {
    private final AttendeeHistoryRepository attendeeHistoryRepository;

    private static final Logger log = LoggerFactory.getLogger(CheckInByUserReportPresenter.class);


    @Autowired
    public CheckInByUserReportPresenter(AttendeeHistoryRepository attendeeHistoryRepository) {
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    private static String buildTable(String title, List<Object[]> data) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h2>%s</h2>", title));
        sb.append("<table border=\"1\" cellpadding=\"2\"><tr>");
        sb.append("<td>First Name</td><td>Last Name</td><td>Count</td></tr>");
        for (Object[] line : data) {
            sb.append("<tr>");
            sb.append(String.format("<td>%s</td>", line[0].toString()));
            sb.append(String.format("<td>%s</td>", line[1].toString()));
            sb.append(String.format("<td align=\"right\">%s</td>", line[2].toString()));
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    public void fetchReportData(ReportView view) {
        StringBuilder report = new StringBuilder();
        report.append("<div class=\"kumoReport\">");
        report.append(buildTable("Check Ins By User in Last 15 Minutes", attendeeHistoryRepository.checkInCountByUsers()));
        report.append("</div>");
        view.afterSuccessfulFetch(report.toString());
        log.info("{} viewed Check Ins By User Report", view.getCurrentUser());
    }
}
