package org.kumoricon.site.report.panelist;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class PanelistReportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private static final Logger log = LoggerFactory.getLogger(PanelistReportPresenter.class);


    public PanelistReportPresenter() {
    }

    public void showAttendeeList(PanelistReportView view) {
        List<Attendee> attendees = attendeeRepository.findPanelists();
        view.afterSuccessfulFetch(attendees);
        log.info("{} viewed Panelist Report", view.getCurrentUser());
    }

    public AttendeeRepository getAttendeeRepository() { return attendeeRepository; }
    public void setAttendeeRepository(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }
}
