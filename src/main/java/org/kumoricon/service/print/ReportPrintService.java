package org.kumoricon.service.print;

import org.kumoricon.model.computer.Computer;
import org.kumoricon.service.print.formatter.ReportPrintFormatter;
import org.springframework.stereotype.Service;

import javax.print.PrintException;

@Service
public class ReportPrintService extends PrintService {
    /**
     * Prints the given string either the appropriate printer name (from
     * the computers table), or the default printer on the server.
     * @param reportText Text of report
     * @param clientIPAddress Client computer's IP address
     * @return String Result message
     */
    public String printReport(String reportText, String clientIPAddress) {
        if (enablePrintingFromServer != null && enablePrintingFromServer) {
            Computer client = computerService.findComputerByIP(clientIPAddress);
            ReportPrintFormatter formatter =
                    new ReportPrintFormatter(reportText, client.getxOffset(), client.getyOffset());
            try {
                printDocument(formatter.getStream(), client.getPrinterName());
            } catch (PrintException e) {
                log.error(String.format("Error printing report for %s: %s",
                        clientIPAddress, e.getMessage()), e);
                return("Error printing. No printers found? More information in server logs");
            }
        } else {
            return("Printing from server not enabled.");
        }
        return "Printed";
    }
}
