package org.kumoricon.site.computer;

import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerRepository;
import org.kumoricon.model.printer.Printer;
import org.kumoricon.site.computer.window.AddPrinterWindow;
import org.kumoricon.site.computer.window.PrinterWindowCallback;
import org.kumoricon.site.computer.window.ViewInstructionsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.lang.*;

@Controller
public class ComputerPresenter {
    @Autowired
    private ComputerRepository computerRepository;

    private static final Logger log = LoggerFactory.getLogger(ComputerPresenter.class);

    public ComputerPresenter() {
    }

    public void addNewComputer(ComputerView view) {
        log.info("{} added new computer", view.getCurrentUsername());

        String ipAddress = view.getCurrentClientIPAddress();

        Computer computer = new Computer();
        computer.setIpAddress(ipAddress);
        computer.setPrinterName(getNetworkFromIpAddress(ipAddress));
        computer.setxOffset(0);
        computer.setyOffset(0);
        saveComputer(view, computer);
    }

    public void addPrinter(ComputerView view) {
         // Prompt the user for the printer hostname and model
        AddPrinterWindow printerInstallWindow = new AddPrinterWindow();

        // Add a handler to run when a printer is installed successfully
        printerInstallWindow.installSuccessHandler = new PrinterWindowCallback() {
            @Override
            public void run() {
                showPrinterList(view);
                log.info("{} added printer", view.getCurrentUsername(), printerInstallWindow.getInstalledPrinter());
            }
        };

        // Add a handler to run when a printer fails to install
        printerInstallWindow.installFailureHandler = new PrinterWindowCallback() {
            @Override
            public void run() {
                view.notifyError((printerInstallWindow.getInstalledPrinter()).getStatus());
                log.error("{} failed to add printer", view.getCurrentUsername(), printerInstallWindow.getInstalledPrinter(), (printerInstallWindow.getInstalledPrinter()).getStatus());
            }
        };
        view.showWindow(printerInstallWindow);
    }

    public void saveComputer(ComputerView view, Computer computer) {
        log.info("{} saved computer", view.getCurrentUsername(), computer);
        try {
            computerRepository.save(computer);
            view.notify("Saved");
            view.navigateTo(ComputerView.VIEW_NAME);
            showComputerList(view);
        } catch (DataIntegrityViolationException e) {
            view.notifyError("Error: Could not save Computer. Duplicate IP Address?");
            log.error("{} got an error saving computer", view.getCurrentUsername(), computer, e);
            showComputerList(view);
        }
    }

    public void showComputerList(ComputerView view) {
        log.info("{} viewed computer list", view.getCurrentUsername());
        List<Computer> computers = computerRepository.findAll();
        view.afterSuccessfulComputerFetch(computers);
    }

    public void showPrinterList(ComputerView view) {
        log.info("{} viewed printer list", view.getCurrentUsername());
        List<Printer> printers = Printer.getPrinterList();
        if (printers != null) { view.afterSuccessfulPrinterFetch(printers); }
    }

    public void deleteComputer(ComputerView view, Computer computer) {
        log.info("{} deleted computer", view.getCurrentUsername(), computer);
        computerRepository.delete(computer);
        view.notify("Deleted " + computer.getIpAddress());
        view.afterSuccessfulComputerFetch(computerRepository.findAll());
    }

    public void deletePrinter(ComputerView view, Printer printer) {
        String result = printer.uninstall();
        if (result.startsWith("Error")) {
            view.notifyError(printer.getStatus());
            log.error("{} failed to delete printer", view.getCurrentUsername(), printer, printer.getStatus());
        }
        else {
            log.info("{} deleted printer '" + printer.getName() + "'", view.getCurrentUsername(), printer);
            showPrinterList(view);
        }
    }

    public void refreshPrinterList(ComputerView view) {
        showPrinterList(view);
    }

    public void showInstructions(ComputerView view) {
        log.info("{} viewed instructions", view.getCurrentUsername());

        String content = "These instructions explain how to associate this computer with a printer, " +
                "such as a printer that is next to or near this computer<br><br>" +
                "Step 1: Verify that the Printer is Installed on the Server<br>" +
                "Printers installed on the server show up on the right-hand column under 'Printers'. If the printer is not there, "+
                "then click Install, enter the DNS hostname, choose the model of the printer, and then click Install.<br><br>"+
                "Step 2: Verify that this Computer is Listed<br>"+
                "Listed computers show up on the left-hand column under 'Computers'. If this computer is not there, "+
                "then click Add. An entry for this computer will be added.<br><br>"+
                "Step 3: Map the DNS Hostname of the Printer to this Computer Mapping<br>"+
                "Select this computer from the list on the left-hand side, click the row to enter edit-mode, "+
                "then enter the printer's hostname in the 'Printer Name' column.<br><br>"+
                "Step 4: Print a Test Badge<br>"+
                "From the Utilities menu, print a test badge. If the image on the badge needs to be moved up/down (Y Offset) or left/right (X Offset), "+
                "adjust the appropriate values in the Computer list on the left-hand column "+
                "by clicking on the row to enter edit-mode. Values can be positive or negative.";
        ViewInstructionsWindow instructionsWindow = new ViewInstructionsWindow(content);
        view.showWindow(instructionsWindow);
    }

    /**
     * Find the network octets from an IPv4 address with trailing dot (IE, "192.168.1." from "192.168.1.23"). Assumes
     * 255.255.255.0 netmask, since there isn't an easy way to find that from the information we have at this level
     * (and that's correct for our usage). If passed something that's not an IP address, just return it as is. This
     * is used to auto-fill partial information when creating a new computer.
     * @param ipAddress IPv4 address as String
     * @return Network portion of address
     */
    static String getNetworkFromIpAddress(String ipAddress) {
        if (ipAddress == null) { return null; }
        if (ipAddress.trim().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            return ipAddress.substring(0, ipAddress.lastIndexOf(".")+1).trim();
        } else {
            return ipAddress;
        }
    }

}
