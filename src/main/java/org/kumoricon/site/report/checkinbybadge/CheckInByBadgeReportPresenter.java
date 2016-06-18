package org.kumoricon.site.report.checkinbybadge;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;


@Controller
public class CheckInByBadgeReportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private static final Logger log = LoggerFactory.getLogger(CheckInByBadgeReportPresenter.class);


    public CheckInByBadgeReportPresenter() {
    }

    public void showAttendeeList(CheckInByBadgeReportView view, Badge badge) {
        log.info("{} viewed Check In Time Report for {}", view.getCurrentUser(), badge);
        if (badge == null) {
            view.afterAttendeeFetch(new ArrayList<>());
        }
        List<Attendee> attendees = attendeeRepository.findByBadgeType(badge);
        view.afterAttendeeFetch(attendees);
    }

    public void showAttendeeList(CheckInByBadgeReportView view, Integer badgeId) {
        if (badgeId != null) {
            Badge badge = badgeRepository.findOne(badgeId);
            if (badge == null) {
                log.error("{} viewed Check In Time Report for badge id {} but it was not found",
                        view.getCurrentUser(), badgeId);
                view.notifyError("Badge id " + badgeId.toString() + " not found");
                view.navigateTo(CheckInByBadgeReportView.VIEW_NAME);
//                view.afterAttendeeFetch(new ArrayList<>());
            } else {
                showAttendeeList(view, badge);
            }
        }
    }

    public AttendeeRepository getAttendeeRepository() { return attendeeRepository; }
    public void setAttendeeRepository(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    public void showBadgeTypes(CheckInByBadgeReportView view) {
         view.afterBadgeTypeFetch(badgeRepository.findByVisibleTrue());
    }
}
