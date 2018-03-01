package org.kumoricon.site.utility.preprint;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerService;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.PrintBadgeHandler;
import org.kumoricon.site.attendee.PrintBadgeView;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.print.PrintException;
import java.time.LocalDate;
import java.util.List;


@Controller
@Scope("request")
public class PreprintBadgePresenter implements PrintBadgeHandler {
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

    /**
     * Show the attendee badge window with the given number of automatically generated badges.
     * Generates badges Adult - Child - Youth
     * @param view View
     * @param xOffset Horizontal offset in points
     * @param yOffset Vertical offset in points
     * @param dateForAgeCalculation Base age calculations on this date
     */
    public void showAttendeeBadgeWindow(AttendeePrintView view, Badge badge, Integer xOffset, Integer yOffset, LocalDate dateForAgeCalculation) {
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} pre-printing badge {} with horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), badge, xOffset, yOffset);
        List<Attendee> attendees = attendeeRepository.findByBadgeType(badge);

        attendeeRepository.setAttendeesPrePrinted(badge);

        log.info("{} pre-printing {} badges", view.getCurrentUsername(), attendees.size());
        showAttendeeBadgeWindow(view, attendees, xOffset, yOffset, dateForAgeCalculation);
    }

    public void showCurrentOffsets(PreprintBadgeView view, String ipAddress) {
        Computer currentClient = computerService.findComputerByIP(ipAddress);
        if (currentClient != null) {
            view.setXOffset(currentClient.getxOffset());
            view.setYOffset(currentClient.getyOffset());
        }
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList, boolean forcePrintAll) {
        if (attendeeList == null) { return; }
        printBadges((BaseView) view, attendeeList, null, null, LocalDate.now());
        view.showPrintBadgeWindow(attendeeList);
    }

    void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList, Integer xOffset, Integer yOffset, LocalDate dateForAgeCalculation) {
        if (attendeeList == null) { return; }
        printBadges((BaseView) view, attendeeList, xOffset, yOffset, dateForAgeCalculation);
        view.showPrintBadgeWindow(attendeeList);
    }



    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        if (printBadgeWindow != null) {
            log.info("{} reports test badges printed successfully for {}",
                    printBadgeWindow.getParentView().getCurrentUser(), attendees);
            printBadgeWindow.close();
        }
    }

    @Override
    public void badgePrintSuccess(PrintBadgeView view, List<Attendee> attendees) {
        log.info("{} reports test badges printed successfully for {}",
                view.getCurrentUser(), attendees);
        view.close();
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        if (printBadgeWindow == null) {
            return;
        }
        PreprintBadgeView view = (PreprintBadgeView) printBadgeWindow.getParentView();
        if (attendeeList.size() > 0) {
            log.info("{} reprinting test badges for {}",
                    view.getCurrentUsername(), attendeeList, view.getXOffset(), view.getYOffset());
            view.notify("Reprinting badges");
            printBadges(view, attendeeList, view.getXOffset(), view.getYOffset(), view.getDateForAgeCalculation());
        } else {
            view.notify("No attendees selected");
        }
    }

    @Override
    public void reprintBadges(BaseView baseView, List<Attendee> attendees) {
        PreprintBadgeView view = (PreprintBadgeView) baseView;
        if (attendees.size() > 0) {
            log.info("{} reprinting test badges for {}",
                    view.getCurrentUsername(), attendees, view.getXOffset(), view.getYOffset());
            view.notify("Reprinting badges");
            printBadges(view, attendees, view.getXOffset(), view.getYOffset(), view.getDateForAgeCalculation());
        } else {
            view.notify("No attendees selected");
        }
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        PreprintBadgeView view = (PreprintBadgeView) printBadgeWindow.getParentView();
        return badgePrintService.getCurrentBadgeFormatter(attendees, view.getXOffset(), view.getYOffset(), view.getDateForAgeCalculation());
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeView printBadgeView, List<Attendee> attendees) {
        // Todo: Wow, that's ugly.
        PreprintBadgeView view = (PreprintBadgeView) (BaseView)printBadgeView;
        return badgePrintService.getCurrentBadgeFormatter(attendees, view.getXOffset(), view.getYOffset(), view.getDateForAgeCalculation());
    }

    /**
     * Print badges for the given attendees and display any error or result messages
     * @param view Current view
     * @param attendeeList Attendees to print badges for
     * @param xOffset Printing X offset in points (1/72 inch)
     * @param yOffset printing Y offset in points (1/72 inch)
     */
    private void printBadges(BaseView view, List<Attendee> attendeeList, Integer xOffset, Integer yOffset, LocalDate ageAsOfDate) {
        try {
            String result = badgePrintService.printBadgesForAttendees(
                    attendeeList, view.getCurrentClientIPAddress(), xOffset, yOffset, ageAsOfDate);
            view.notify(result);
        } catch (PrintException e) {
            log.error("Error printing badges for {}", view.getCurrentUsername(), e);
            view.notifyError(e.getMessage());
        }
    }

    public List<Badge> getBadges() {
        return badgeRepository.findAll();
    }
}
