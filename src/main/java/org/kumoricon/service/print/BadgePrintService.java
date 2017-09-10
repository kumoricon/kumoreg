package org.kumoricon.service.print;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.PrintException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BadgePrintService extends PrintService {
    private final BadgeFormatterFactory badgeFormatterFactory;

    @Autowired
    public BadgePrintService(BadgeFormatterFactory badgeFormatterFactory) {
        this.badgeFormatterFactory = badgeFormatterFactory;
    }

    /**
     * Prints badges for a given list of attendees to either the appropriate printer name (from
     * the computers table), or the default printer on the server with the given offset
     * @param attendees List of attendees
     * @param clientIPAddress Client computer's IP address
     * @return String Result message
     * @throws PrintException Printer error
     */
    public String printBadgesForAttendees(List<Attendee> attendees, String clientIPAddress, Integer xOffset, Integer yOffset, LocalDate ageAsOfDate) throws PrintException {
        String printerName;
        List<Attendee> attendeesNotPrinted = findPrintedAttendees(attendees);

        if (attendeesNotPrinted.size() == attendees.size()) {
            return("Pre-printed badges ready for pickup: " + attendeesNotPrinted);
        }

        if (enablePrintingFromServer != null && enablePrintingFromServer) {
            Computer client = computerService.findComputerByIP(clientIPAddress);
            BadgePrintFormatter badgePrintFormatter =
                    getCurrentBadgeFormatter(attendees, xOffset, yOffset, ageAsOfDate);
            printerName = client.getPrinterName();
            printDocument(badgePrintFormatter.getStream(), printerName);
        } else {
            return("Printing from server not enabled. Select \"Show Selected in Browser\".");
        }
        return String.format("Printed %s badges to %s. %s pre-printed badges.",
                attendees.size() - attendeesNotPrinted.size(),
                printerName,
                attendeesNotPrinted.size());
    }


    private static List<Attendee> findPrintedAttendees(List<Attendee> attendees) {
        List<Attendee> attendeesNotPrinted = new ArrayList<>();
        for (Attendee attendee : attendees) {
            if (attendee.isBadgePrePrinted()) {
                attendeesNotPrinted.add(attendee);
            }
        }
        return attendeesNotPrinted;
    }


    /**
     * Prints badges for a given list of attendees to either the appropriate printer name (from
     * the computers table), or the default printer on the server.
     * @param attendees List of attendees
     * @param clientIPAddress Client computer's IP address
     * @return String Result message
     * @throws PrintException Printer Error
     */
    public String printBadgesForAttendees(List<Attendee> attendees, String clientIPAddress) throws PrintException {
        String printerName;
        List<Attendee> attendeesNotPrinted = findPrintedAttendees(attendees);
        if (attendeesNotPrinted.size() == attendees.size()) {
            return("Pre-printed badges ready for pickup: " + attendeesNotPrinted);
        }

        if (enablePrintingFromServer != null && enablePrintingFromServer) {
            Computer client = computerService.findComputerByIP(clientIPAddress);
            BadgePrintFormatter badgePrintFormatter =
                    getCurrentBadgeFormatter(attendees, client.getxOffset(), client.getyOffset(), LocalDate.now());
            printerName = client.getPrinterName();
            printDocument(badgePrintFormatter.getStream(), printerName);
        } else {
            return("Printing from server not enabled. Select \"Show Selected in Browser\".");
        }
        return String.format("Printed %s badges to %s. %s pre-printed badges.",
                attendees.size() - attendeesNotPrinted.size(),
                printerName,
                attendeesNotPrinted.size());
    }

    /**
     * Return the currently defined badge print formatter, which generates a PDF from the given attendees
     * @param attendees Attendees to generate badges for
     * @return BadgePrintFormatter
     */
    public BadgePrintFormatter getCurrentBadgeFormatter(List<Attendee> attendees) {
        return getCurrentBadgeFormatter(attendees, 0, 0, LocalDate.now());
    }

    /**
     * Return the currently defined badge print formatter, which generates a PDF from the given attendees
     * @param attendees Attendees to generate badges for
     * @param ipAddress IP address of current client computer
     * @return BadgeFormatter
     */
    public BadgePrintFormatter getCurrentBadgeFormatter(List<Attendee> attendees, String ipAddress) {
        Computer client = computerService.findComputerByIP(ipAddress);
        return getCurrentBadgeFormatter(attendees, client.getxOffset(), client.getyOffset(), LocalDate.now());
    }

    /**
     * Return the currently defined badge print formatter, which generates a PDF from the given attendees
     * @param attendees Attendees to generate badges for
     * @param xOffset offset horizontally by x points (1/72 inch)
     * @param yOffset offset vertically by x points (1/72 inch)
     * @return BadgePrintFormatter
     */
    public BadgePrintFormatter getCurrentBadgeFormatter(List<Attendee> attendees, Integer xOffset, Integer yOffset, LocalDate ageAsOfDate) {
        return badgeFormatterFactory.getCurrentBadgeFormatter(attendees, xOffset, yOffset, ageAsOfDate);
    }



}
