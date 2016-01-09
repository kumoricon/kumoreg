package org.kumoricon.presenter.report;

import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.report.ReportLine;
import org.kumoricon.view.report.AttendeeReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
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

        data.add(new ReportLine("Total Attendees", attendeeRepository.findAll().size()));

        view.afterSuccessfulFetch(data);
    }
}
