package org.kumoricon.presenter.report;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.view.report.PanelistReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class PanelistReportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    public PanelistReportPresenter() {
    }

    public void showAttendeeList(PanelistReportView view) {
        List<Attendee> attendees = attendeeRepository.findPanelists();
        view.afterSuccessfulFetch(attendees);
    }

    public AttendeeRepository getAttendeeRepository() { return attendeeRepository; }
    public void setAttendeeRepository(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }
}
