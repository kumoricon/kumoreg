package org.kumoricon.site.utility.preprint;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerService;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.print.PrintException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class PreprintBadgePresenter {
    private static final Logger log = LoggerFactory.getLogger(PreprintBadgePresenter.class);
    private final AttendeeRepository attendeeRepository;
    private final BadgePrintService badgePrintService;
    private final ComputerService computerService;
    private final BadgeRepository badgeRepository;

    @Autowired
    public PreprintBadgePresenter(BadgePrintService badgePrintService, AttendeeRepository attendeeRepository, ComputerService computerService, BadgeRepository badgeRepository) {
        this.badgePrintService = badgePrintService;
        this.computerService = computerService;
        this.badgeRepository = badgeRepository;
        this.attendeeRepository = attendeeRepository;
    }


    public void showCurrentOffsets(PreprintBadgeView view, String ipAddress) {
        Computer currentClient = computerService.findComputerByIP(ipAddress);
        if (currentClient != null) {
            view.setXOffset(currentClient.getxOffset());
            view.setYOffset(currentClient.getyOffset());
        }
    }

    public BadgePrintFormatter getBadgeFormatter(PreprintBadgeView view, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees,
                view.getXOffset(), view.getYOffset(), view.getDateForAgeCalculation());
    }


    public List<Badge> getBadges() {
        return badgeRepository.findAll();
    }

    public void printBadges(PreprintBadgeView view, Badge badge, Integer xOffset, Integer yOffset, LocalDate dateForAgeCalculation) {
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} pre-printing badge {} with horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), badge, xOffset, yOffset);
        List<Attendee> attendees = attendeeRepository.findByBadgeType(badge);

        attendeeRepository.setAttendeesPrePrinted(badge);

        log.info("{} pre-printing {} badges", view.getCurrentUsername(), attendees.size());
        BadgePrintFormatter formatter = getBadgeFormatter(view, attendees);
        view.showPDF(formatter);
        try {

            String result = badgePrintService.printBadgesForAttendees(
                    attendees, view.getCurrentClientIPAddress(), xOffset, yOffset, dateForAgeCalculation);
            view.notify(result);
        } catch (PrintException e) {
            log.error("Error printing badges for {}", view.getCurrentUsername(), e);
            view.notifyError(e.getMessage());
        }

    }
}
