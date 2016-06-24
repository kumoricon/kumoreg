package org.kumoricon.service.print;

import org.kumoricon.model.attendee.Attendee;
import org.springframework.stereotype.Service;
import javax.print.PrintException;
import java.util.List;

@Service
public class BadgePrintService extends PrintService {

    /**
     * Prints badges for a given list of attendees to either the appropriate printer name (from
     * the computers table), or the default printer on the server.
     * @param attendees List of attendees
     * @param clientIPAddress Client computer's IP address
     * @return String Result message
     */
    public String printBadgesForAttendees(List<Attendee> attendees, String clientIPAddress) {
        if (enablePrintingFromServer != null && enablePrintingFromServer) {
            BadgePrintFormatter badgePrintFormatter = new BadgePrintFormatter(attendees);
            String printerName = computerService.findPrinterNameForComputer(clientIPAddress);
            try {
                printDocument(badgePrintFormatter.getStream(), printerName);
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


}
