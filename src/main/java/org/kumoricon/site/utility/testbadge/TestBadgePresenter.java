package org.kumoricon.site.utility.testbadge;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestBadgePresenter {
    private static final Logger log = LoggerFactory.getLogger(BadgePrintService.class);
    private final AttendeeFactory attendeeFactory;
    private final BadgePrintService badgePrintService;
    private final ComputerService computerService;
    private final BadgeRepository badgeRepository;

    @Autowired
    public TestBadgePresenter(BadgePrintService badgePrintService, AttendeeFactory attendeeFactory, ComputerService computerService, BadgeRepository badgeRepository) {
        this.badgePrintService = badgePrintService;
        this.attendeeFactory = attendeeFactory;
        this.computerService = computerService;
        this.badgeRepository = badgeRepository;
    }

    public void showCurrentOffsets(TestBadgeView view, String ipAddress) {
        Computer currentClient = computerService.findComputerByIP(ipAddress);
        if (currentClient != null) {
            view.setXOffset(currentClient.getxOffset());
            view.setYOffset(currentClient.getyOffset());
        }
    }


    private void printAndShowPDF(TestBadgeView view, List<Attendee> attendeeList, Integer xOffset, Integer yOffset) {
        if (attendeeList == null) { return; }
        printBadges(view, attendeeList, xOffset, yOffset);
        BadgePrintFormatter formatter = getBadgeFormatter(view, attendeeList);

        view.showPDF(formatter);
    }



    public BadgePrintFormatter getBadgeFormatter(TestBadgeView view, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees, view.getXOffset(), view.getYOffset(), LocalDate.now());
    }

    /**
     * Print badges for the given attendees and display any error or result messages
     * @param view Current view
     * @param attendeeList Attendees to print badges for
     * @param xOffset Printing X offset in points (1/72 inch)
     * @param yOffset printing Y offset in points (1/72 inch)
     */
    private void printBadges(TestBadgeView view, List<Attendee> attendeeList, Integer xOffset, Integer yOffset) {
        try {
            String result = badgePrintService.printBadgesForAttendees(
                    attendeeList, view.getCurrentClientIPAddress(), xOffset, yOffset, LocalDate.now());
            view.notify(result);
        } catch (PrintException e) {
            log.error("Error printing badges for {}", view.getCurrentUsername(), e);
            view.notifyError(e.getMessage());
        }
    }

    public List<Badge> getBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Prints/shows the given number of automatically generated badges.
     * Generates badges Adult - Child - Youth
     * @param view View
     * @param numberOfBadges Number of badges to generate, minimum: 1, maximum: 3)
     * @param xOffset Horizontal offset in points
     * @param yOffset Vertical offset in points
     */
    public void printBadges(TestBadgeView view, Integer numberOfBadges, Badge badge, Integer xOffset, Integer yOffset) {
        if (numberOfBadges == null) { numberOfBadges = 1; }
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} generating {} test badge(s) with horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), numberOfBadges, xOffset, yOffset);
        List<Attendee> attendees = new ArrayList<>();

        if (numberOfBadges >= 1) { attendees.add(attendeeFactory.generateDemoAttendee(badge)); }
        if (numberOfBadges >= 2) { attendees.add(attendeeFactory.generateYouthAttendee(badge)); }
        if (numberOfBadges >= 3) { attendees.add(attendeeFactory.generateChildAttendee(badge)); }

        log.info("{} printing test badges for {}", view.getCurrentUsername(), attendees);
        printAndShowPDF(view, attendees, xOffset, yOffset);
    }

    /**
     * Prints/shows automatically generated badges for all badge types.
     * Generates badges Adult - Child - Youth
     * @param view View
     * @param xOffset Horizontal offset in points
     * @param yOffset Vertical offset in points
     */
    public void printBadges(TestBadgeView view, Integer xOffset, Integer yOffset) {
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} generating test badges for all badge types with horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), xOffset, yOffset);
        List<Attendee> attendees = new ArrayList<>();

        for (Badge badge : badgeRepository.findByVisibleTrue()) {
            attendees.add(attendeeFactory.generateDemoAttendee(badge));
            attendees.add(attendeeFactory.generateYouthAttendee(badge));
            attendees.add(attendeeFactory.generateChildAttendee(badge));
        }

        log.info("{} printing test badges for {}", view.getCurrentUsername(), attendees);

        printAndShowPDF(view, attendees, xOffset, yOffset);
    }

    public void printTroublesomeNames(TestBadgeView view, Badge badge, Integer xOffset, Integer yOffset) {
        if (xOffset == null) { xOffset = 0; }
        if (yOffset == null) { yOffset = 0; }

        log.info("{} generating test badges for troublesome names horizontal offset {} vertical offset {}",
                view.getCurrentUsername(), xOffset, yOffset);
        List<Attendee> attendees = new ArrayList<>();
        String[] names = {"I have a really long name here and it makes life miserable for text",
            "しん ★", "オリビア • ベルトラン",  "キャサティ-", "2814.5", ":3", "クララ・コアラ", "ミラちゃん",
                ">:(", "ಠ_ಠ", "∆$#", "( ͡° ͜ʖ ͡°)", "ひな", "もんど", "ルイ-ス", "高原・コーゲン"};
        for (String name : names) {
            Attendee attendee = attendeeFactory.generateDemoAttendee(badge);
            attendee.setFanName(name);
            attendees.add(attendee);
        }

        printAndShowPDF(view, attendees, xOffset, yOffset);
    }
}
