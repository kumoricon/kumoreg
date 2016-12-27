package org.kumoricon.site.computer;

import com.vaadin.shared.ui.label.ContentMode;
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
        log.info("{} added new printer", view.getCurrentUsername());

        // Create a sub-window to prompt the user for the printer IP address and model
        Window subWindow = new Window("Install Printer");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        TextField ipAddress = new TextField("IP Address: ");

        //Create a combo box with an item for each printer model
        List<Printer> modelList = new ArrayList<Printer>();
            modelList.add(new Printer("", "", "8610"));
            modelList.add(new Printer("", "", "251"));
            modelList.add(new Printer("", "", "0000"));
            BeanItemContainer<Printer> objects = new BeanItemContainer(Printer.class, modelList);
            ComboBox modelComboBox = new ComboBox("Model", objects);
            modelComboBox.setTextInputAllowed(false);
            modelComboBox.setItemCaptionPropertyId("model");

        Button installButton = new Button("Install");
        subContent.addComponent(ipAddress);
        subContent.addComponent(modelComboBox);
        subContent.addComponent(installButton);

        installButton.addClickListener((Button.ClickListener) clickEvent -> {
            Printer newPrinter = new Printer();
            newPrinter.setIpAddress(ipAddress.getValue());
            String model = ((Printer)modelComboBox.getValue()).getModel();
            newPrinter.setModel(model);
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
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, new File("/usr/local/bin"));
            try {
                final int exitValue = p.waitFor();
                if (exitValue == 0) { view.notify("Printer successfully installed"); }
                else {
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        String errorString = "";
                        if ((errorString = b.readLine()) != null) {
                            log.error("{} got an error installing printer {}", view.getCurrentUsername(), printer, errorString);
                            view.notifyError("Error: Could not install printer. Error: " + errorString);
                        }
                    } catch (final IOException e) {
                        log.error("{} got an error installing printer {}", view.getCurrentUsername(), printer, e);
                        view.notifyError("Error: Could not install printer. " + e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                log.error("{} got an error installing printer {}", view.getCurrentUsername(), printer, e);
                view.notifyError("Error: Could not install printer. " + e.getMessage());
            }
        } catch (final IOException e) {
            log.error("{} got an error installing printer {}", view.getCurrentUsername(), printer, e);
            view.notifyError("Error: Could not install printer. " + e.getMessage());
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

            // TODO: use a different method to get the IP address of printer, because this one can't

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
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, null);
            try {
                final int exitValue = p.waitFor();
                if (exitValue == 0) { view.notify("Printer successfully uninstalled"); }
                else {
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        String errorString = "";
                        if ((errorString = b.readLine()) != null) {
                            log.error("{} got an error uninstalling printer {}", view.getCurrentUsername(), printer, errorString);
                            view.notifyError("Error: Could not uninstall printer. " + errorString);
                        }
                    } catch (final IOException e) {
                        log.error("{} got an error uninstalling printer {}", view.getCurrentUsername(), printer, e);
                        view.notifyError("Error: Could not uninstall printer. " + e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                log.error("{} got an error uninstalling printer {}", view.getCurrentUsername(), printer, e);
                view.notifyError("Error: Could not uninstall printer. " + e.getMessage());
            }
        } catch (final IOException e) {
            log.error("{} got an error uninstalling printer {}", view.getCurrentUsername(), printer, e);
            view.notifyError("Error: Could not uninstall printer. " + e.getMessage());
        }

        showInstalledPrinterList(view);
    }

    public void showInstructions(ComputerView view) {
        log.info("{} viewed instructions {}", view.getCurrentUsername());

        // Prompt the user for the printer name and IP address
        Window subWindow = new Window("Instructions");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        String content = "These instructions explain how to associate this computer with a given printer, presumably the printer that is next to or near this computer\n\n" +
                "Step 1: Verify that the Printer is Installed on the Server\n" +
                "Printers installed on the server show up on the right-hand column under 'Installed Printers'. If the printer to be installed is not there,\n"+
                "then click Install, enter the IP address and choose the model of the printer, and proceed with the installation.\n\n"+
                "Step 2: Verify that this Computer is Mapped\n"+
                "Mapped computers show up on the left-hand column under 'Computer-Printer Mappings'. If this computer is not there,\n"+
                "then click Add. An entry for the computer will be added.\n\n"+
                "Step 3: Enter the IP Address of the Printer to the Computer Mapping\n"+
                "On the row representing the mapping for this computer, enter the printer's IP address in the 'Printer Name' column.\n\n"+
                "Step 4: Print a Test Badge\n"+
                "From the Utilities menu, print a test badge. Adjust the X Offset and Y Offset in the Computer-Printer mapping\n"+
                "if the image on the badge needs to be moved up or down (Y Offset) or left or right (X Offset). Values can be positive or negative.";
        Label text = new Label(content, ContentMode.PREFORMATTED);
        Button closeButton = new Button("Close");
        subContent.addComponent(text);
        subContent.addComponent(closeButton);

        closeButton.addClickListener((Button.ClickListener) clickEvent -> {
            subWindow.close();
        });

        subWindow.center();
        UI.getCurrent().addWindow(subWindow);
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
