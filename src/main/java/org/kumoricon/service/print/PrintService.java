package org.kumoricon.service.print;

import org.kumoricon.model.computer.ComputerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for implementing services that will send data to a printer installed on the server
 */
public abstract class PrintService {
    protected static final Logger log = LoggerFactory.getLogger(PrintService.class);
    @Value("${kumoreg.printing.enablePrintingFromServer}")
    protected Boolean enablePrintingFromServer;
    @Autowired
    protected ComputerService computerService;

    /**
     * Prints the given inputStream to the printer with the given name, or the default printer
     * if a printer with that name isn't found.
     *
     * @param inputStream Data stream (Usually PDF formatted)
     * @param printerName Destination printer name (case insensitive)
     */
    public void printDocument(InputStream inputStream, String printerName) throws PrintException {
        javax.print.PrintService printService = findPrinter(printerName);

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        DocPrintJob job = printService.createPrintJob();
        PrintRequestAttributeSet printRequestSet = new HashPrintRequestAttributeSet();

        Doc doc = new SimpleDoc(inputStream, flavor, null);
        job.print(doc, printRequestSet);
    }

    /**
     * Returns a PrintService object for the printer with the given name, or the default printer if
     * no match is found.
     * @param name Printer name (case insensitive)
     * @return PrintService
     */
    public javax.print.PrintService findPrinter(String name) {
        name = name.toLowerCase().trim();
        javax.print.PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (javax.print.PrintService printer : printServices) {
            String thisPrinterName = printer.getName().trim().toLowerCase();
            if (name.equals(thisPrinterName)) { return printer; }
        }

        javax.print.PrintService printer = PrintServiceLookup.lookupDefaultPrintService();
        if (printer != null) {
            log.warn("Printer \"{}\" not found, using default printer \"{}\"", name, printer.getName());
            return printer;
        }
        log.error("Printer \"%s\" not found, no default printer found. Set a printer as default.", name);
        return null;
    }

    /**
     * Gets list of installed printers from system
     * @return List of PrintService objects
     */
    public List<javax.print.PrintService> getAvailablePrinters() {
        return Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null));
    }
}
