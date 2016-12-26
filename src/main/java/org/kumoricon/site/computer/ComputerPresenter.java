package org.kumoricon.site.computer;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.data.util.*;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerRepository;
import org.kumoricon.model.printer.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import javax.print.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;
import java.io.*;

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

    public void addNewInstalledPrinter(ComputerView view) {
        // Prompt the user for the printer name and IP address
        Window subWindow = new Window("Install Printer");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        TextField ipAddress = new TextField("IP Address: ");

        List<Printer> modelList = new ArrayList<Printer>();
        modelList.add(new Printer("", "", "8610"));
        modelList.add(new Printer("", "", "251"));
        modelList.add(new Printer("", "", "0000"));
        BeanItemContainer<Printer> objects = new BeanItemContainer(Printer.class,modelList);
        ComboBox model = new ComboBox("Model", objects);
        model.setTextInputAllowed(false);
        model.setItemCaptionPropertyId("model");

        Button closeButton = new Button("Install");
        subContent.addComponent(ipAddress);
        subContent.addComponent(model);
        subContent.addComponent(closeButton);

        closeButton.addClickListener((Button.ClickListener) clickEvent -> {
            Printer newPrinter = new Printer();
            newPrinter.setIpAddress(ipAddress.getValue());
            newPrinter.setModel(model.getValue().toString());
            subWindow.setVisible(false);
            savePrinter(view, newPrinter);
            subWindow.close();
        });

        subWindow.center();
        UI.getCurrent().addWindow(subWindow);
    }

    public void saveComputer(ComputerView view, Computer computer) {
        log.info("{} saved computer {}", view.getCurrentUsername(), computer);
        try {
            computerRepository.save(computer);
            view.notify("Saved");
            view.navigateTo(ComputerView.VIEW_NAME);
            showComputerList(view);
        } catch (DataIntegrityViolationException e) {
            view.notifyError("Error: Could not save Computer. Duplicate IP Address?");
            log.error("{} got an error saving computer {}", view.getCurrentUsername(), computer, e);
            showComputerList(view);
        }
    }

    public void savePrinter(ComputerView view, Printer printer) {
        log.info("{} saved printer {}", view.getCurrentUsername(), printer);
        final String command = "addprinter.sh " + printer.getIpAddress() + " " + printer.getModel();
        String errorString = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, new File("/usr/local/bin"));
            try {
                final int exitValue = p.waitFor();
                if (exitValue == 0) { view.notify("Printer successfully installed"); }
                else {
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        if ((errorString = b.readLine()) != null) { view.notifyError("Error: Could not install printer. Error: " + errorString); }
                    } catch (final IOException e) {
                        errorString = e.getMessage();
                        view.notifyError("Error: Could not install printer. " + errorString);
                    }
                }
            } catch (InterruptedException e) {
                errorString = e.getMessage();
                view.notifyError("Error: Could not install printer. " + errorString);
            }
        } catch (final IOException e) {
            log.error("{} got an error installing printer {}", view.getCurrentUsername(), printer, e);
            errorString = e.getMessage();
            view.notifyError("Error: Could not install printer. " + errorString);
        }

        showInstalledPrinterList(view);
    }

    public void showComputerList(ComputerView view) {
        log.info("{} viewed computer list", view.getCurrentUsername());
        List<Computer> computers = computerRepository.findAll();
        view.afterSuccessfulComputerFetch(computers);
    }

    public void showInstalledPrinterList(ComputerView view) {
        log.info("{} viewed printer list", view.getCurrentUsername());

        // Pull a list of printers on the server
        List<Printer> printers = new ArrayList<Printer>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService ps : printServices) {
            Printer p = new Printer();
            p.setName(ps.getName());
            printers.add(p);

            // TODO: get IP address of printer and add it here

        }
        view.afterSuccessfulPrinterFetch(printers);
    }

    public void deleteComputer(ComputerView view, Computer computer) {
        log.info("{} deleted computer {}", view.getCurrentUsername(), computer);
        computerRepository.delete(computer);
        view.notify("Deleted " + computer.getIpAddress());
        view.afterSuccessfulComputerFetch(computerRepository.findAll());
    }

    public void deleteInstalledPrinter(ComputerView view, Printer printer) {
        log.info("{} deleted printer {}", view.getCurrentUsername(), printer);

        final String command = "lpadmin -x " + printer.getIpAddress() + " 2>/dev/null";
        String errorString = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, null);
            try {
                final int exitValue = p.waitFor();
                if (exitValue == 0) { view.notify("Printer successfully uninstalled"); }
                else {
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        if ((errorString = b.readLine()) != null) { view.notifyError("Error: Could not uninstall printer. " + errorString); }
                    } catch (final IOException e) {
                        errorString = e.getMessage();
                        view.notifyError("Error: Could not uninstall printer. " + errorString);
                    }
                }
            } catch (InterruptedException e) {
                errorString = e.getMessage();
                view.notifyError("Error: Could not uninstall printer. " + errorString);
            }
        } catch (final IOException e) {
            log.error("{} got an error uninstalling printer {}", view.getCurrentUsername(), printer, e);
            errorString = e.getMessage();
            view.notifyError("Error: Could not uninstall printer. " + errorString);
        }

        showInstalledPrinterList(view);
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
