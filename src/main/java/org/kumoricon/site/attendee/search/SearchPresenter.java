package org.kumoricon.site.attendee.search;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.service.AttendeeSearchService;
import org.kumoricon.site.attendee.search.bybadge.SearchByBadgeView;
import org.kumoricon.site.attendee.search.byname.SearchByNameView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchPresenter {
    private AttendeeRepository attendeeRepository;
    private AttendeeSearchService attendeeSearchService;
    private BadgeRepository badgeRepository;
    private static final Logger log = LoggerFactory.getLogger(SearchPresenter.class);

    @Autowired
    public SearchPresenter(AttendeeRepository attendeeRepository,
                           AttendeeSearchService attendeeSearchService,
                           BadgeRepository badgeRepository) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeSearchService = attendeeSearchService;
        this.badgeRepository = badgeRepository;
    }

    public void searchFor(SearchByNameView view, String searchString) {
        if (searchString != null && !searchString.trim().isEmpty()) {
            searchString = searchString.trim();
            long start = System.currentTimeMillis();
            List<Attendee> attendees = attendeeSearchService.search(searchString);
            long finish = System.currentTimeMillis();
            log.info("{} searched Attendees for \"{}\" and got {} results in {} ms",
                    view.getCurrentUsername(), searchString, attendees.size(), finish-start);
            view.afterSuccessfulFetch(attendees);
            if (attendees.size() == 0) {
                view.notify("No matching attendees found");
            }
        }
    }

    public void showAttendeeList(SearchByBadgeView view, Integer badgeId) {
        if (badgeId != null) {
            Badge badge = badgeRepository.findOne(badgeId);
            if (badge == null) {
                log.error("{} viewed attendees for badge id {} but it was not found",
                        view.getCurrentUsername(), badgeId);
                view.notifyError("Badge id " + badgeId.toString() + " not found");
                view.navigateTo(SearchByNameView.VIEW_NAME);
            } else {
                showAttendeeList(view, badge);
            }
        }

    }

    public void showAttendeeList(SearchByBadgeView view, Badge badge) {
        log.info("{} viewed attendees with badge {}", view.getCurrentUsername(), badge);
        if (badge == null) {
            view.afterAttendeeFetch(new ArrayList<>());
        }
        List<Attendee> attendees = attendeeRepository.findByBadgeType(badge);
        view.afterAttendeeFetch(attendees);
    }


    public void showBadgeTypes(SearchByBadgeView view) {
        view.afterBadgeTypeFetch(badgeRepository.findByVisibleTrue());
    }

}
