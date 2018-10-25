package org.kumoricon.site.attendee.search;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.site.attendee.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
public class PrintBadgePresenter extends BadgePrintingPresenter {
    private AttendeeRepository attendeeRepository;

    private static final Logger log = LoggerFactory.getLogger(PrintBadgePresenter.class);

    @Autowired
    public PrintBadgePresenter(AttendeeRepository repository) {
        this.attendeeRepository = repository;
    }

    public void showAttendee(PrintBadgeView view, int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            log.info("{} displayed Attendee {}", view.getCurrentUsername(), attendee);
            if (!attendee.isBadgePrePrinted()) {
                printBadges(view, Arrays.asList(attendee));
            } else {
                view.notify("Badge is preprinted, not printing");
            }
            view.showAttendee(attendee);
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUsername(), id);
            view.notify("Error: attendee " + id + " not found.");
        }
    }

    public void showAttendeeWithoutPrinting(PrintBadgeView view, int id) {
        Attendee attendee = attendeeRepository.findOne(id);
        if (attendee != null) {
            log.info("{} displayed Attendee {}", view.getCurrentUsername(), attendee);
            view.showAttendee(attendee);
        } else {
            log.error("{} tried to display Attendee id {} and it was not found", view.getCurrentUsername(), id);
            view.notify("Error: attendee " + id + " not found.");
        }
    }

    public void badgePrintSuccess(PrintBadgeView view, List<Attendee> attendees) {
        log.info("{} reported badge(s) printed successfully for {}",
                view.getCurrentUser(), attendees);
        if (attendees.size() > 0) {
            Attendee attendee = attendees.get(0);
        }
    }


    public void reprintBadges(PrintBadgeView view, List<Attendee> attendees) {
        if (attendees.size() > 0) {
            log.info("{} reprinting badges due to error for {}", view.getCurrentUsername(), attendees);
            printBadges(view, attendees);
        } else {
            view.notify("No attendees selected");
        }
    }

    public void reprintLostBadge(PrintBadgeView view, Integer attendeeId, User overrideUser) {
        Attendee attendee = attendeeRepository.findOne(attendeeId);
        if (attendee == null) {
            log.error("{} tried to reprint badge for attendee {} but they weren't found", view.getCurrentUser(), attendeeId);
            return;
        }
        String historyMessage;
        if (overrideUser != null) {
            historyMessage = String.format("Badge reprinted with override by %s", overrideUser);
        } else {
            historyMessage = "Badge reprinted";
        }
        attendee.addHistoryEntry(view.getCurrentUser(), historyMessage);
        attendee = attendeeRepository.save(attendee);
        printBadges(view, Arrays.asList(attendee));
    }

    public void reprintBadge(PrintBadgeView view, Integer attendeeId) {
        Attendee attendee = attendeeRepository.findOne(attendeeId);
        if (attendee != null) {
            log.info("{} reprinting badges due to error for {}", view.getCurrentUsername(), attendee);
            printBadges(view, Arrays.asList(attendee));
        } else {
            view.notify("No attendees selected");
        }
    }

}
