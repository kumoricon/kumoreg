package org.kumoricon.presenter.report;

import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.report.ReportLine;
import org.kumoricon.view.report.AttendeeReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
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
        List<ReportLine> data = new ArrayList<>();

        List<Object[]> results = attendeeRepository.findCountPerBadgeType();
        data.add(new ReportLine("Total by Badge Type"));
        for (Object[] result : results) {
            data.add(new ReportLine(result[0].toString(), (Long)result[1]));
        }

        data.add(new ReportLine("Checked in by Badge Type"));
        results = attendeeRepository.findCountPerBadgeTypeCheckedIn();
        for (Object[] result : results) {
            data.add(new ReportLine(result[0].toString(), (Long)result[1]));
        }

        data.add(new ReportLine("Not checked in in by Badge Type"));
        results = attendeeRepository.findCountPerBadgeTypeNotCheckedIn();
        for (Object[] result : results) {
            data.add(new ReportLine(result[0].toString(), (Long)result[1]));
        }

        view.afterSuccessfulFetch(data);
    }
}
