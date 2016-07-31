package org.kumoricon.service.print;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.PrintException;
import java.util.List;

@Service
public class BadgePrintService extends PrintService {
    private final BadgeFormatterFactory badgeFormatterFactory;

    @Autowired
    public BadgePrintService(BadgeFormatterFactory badgeFormatterFactory) {this.badgeFormatterFactory = badgeFormatterFactory;}

    /**
     * Prints badges for a given list of attendees to either the appropriate printer name (from
     * the computers table), or the default printer on the server.
     * @param attendees List of attendees
     * @param clientIPAddress Client computer's IP address
     * @return String Result message
     */
    public String printBadgesForAttendees(List<Attendee> attendees, String clientIPAddress) {
        if (enablePrintingFromServer != null && enablePrintingFromServer) {
            Computer client = computerService.findComputerByIP(clientIPAddress);
            BadgePrintFormatter badgePrintFormatter =
                    getCurrentBadgeFormatter(attendees, client.getxOffset(), client.getyOffset());
            try {
                printDocument(badgePrintFormatter.getStream(), client.getPrinterName());
            } catch (PrintException e) {
                log.error(String.format("Error printing badge for %s: %s",
                        clientIPAddress, e.getMessage()), e);
                return("Error printing. No printers found? More information in server logs");
            }
        } else {
            return("Printing from server not enabled. Select \"Show Badges in Browser\".");
        }
        return "Printed";
    }

    /**
     * Return the currently defined badge print formatter, which generates a PDF from the given attendees
     * @param attendees Attendees to generate badges for
     * @return BadgePrintFormatter
     */
    public BadgePrintFormatter getCurrentBadgeFormatter(List<Attendee> attendees) {
        return getCurrentBadgeFormatter(attendees, 0, 0);
    }

    /**
     * Return the currently defined badge print formatter, which generates a PDF from the given attendees
     * @param attendees Attendees to generate badges for
     * @param xOffset offset horizontally by x points (1/72 inch)
     * @param yOffset offset vertically by x points (1/72 inch)
     * @return BadgePrintFormatter
     */
    public BadgePrintFormatter getCurrentBadgeFormatter(List<Attendee> attendees, Integer xOffset, Integer yOffset) {
        return badgeFormatterFactory.getCurrentBadgeFormatter(attendees, xOffset, yOffset);
    }



}
