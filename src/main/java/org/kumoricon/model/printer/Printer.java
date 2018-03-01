package org.kumoricon.model.printer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.kumoricon.model.Record;
import org.kumoricon.model.system.Command;
import org.kumoricon.site.computer.ComputerPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *   Encapsulates non-persistent printer data and printer-related operations
 */
@Entity
@Table(name = "printers")
public class Printer extends Record {

    private static final Logger log = LoggerFactory.getLogger(ComputerPresenter.class);

    //@NotNull
    @Column(unique=true)
    private String name;

    //@NotNull
    @Column(unique=false)
    private String model;

    //@NotNull
    @Column(unique=false)
    private String status;

    //@NotNull
    @Column(unique=true)
    private String ipAddress;

    public Printer(String name, String model) {
        this.setName(name);
        this.setModel(model);
        this.setIpAddress("");
        this.setStatus("");
    }

    public Printer() {
        this.setName("");
        this.setModel("");
        this.setIpAddress("");
        this.setStatus("");
    }

    public String getName() { return this.name; }
    public void setName(String name) {

        // The name being assigned to the printer is an IP address
        if (name.trim().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            try {
                /* TODO validate that the IP address is reachable */
                this.name = name;
            }
            catch (Exception e) {
            }
        }

        // The name being assigned to the printer is a DNS hostname
        else if (name.trim().matches("([a-zA-Z0-9\\.\\-_ ])*")) {
            try {
                /* TODO validate that the printer exists in DNS */
                this.name = name;
            }
            catch (Exception e) {
            }
        }

        // The name being assigned to the printer is not valid
        else {
            String errorString = "Error: Unable to validate the name that was assigned to a printer";
            this.setStatus(errorString);
            log.error(errorString);
        }
    }

    public String getModel() { return this.model; }
    public void setModel(String model) {
        try {
            /* TODO validate that the given model is in the list of models */
            this.model = model;
        }
        catch (Exception e) {
        }
    }

    public String getIpAddress() { return this.ipAddress; }
    private void setIpAddress(String ipAddress) {

        // The IP address is a permissible value
        if (ipAddress.trim().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            try {
                /* TODO validate that the IP address is reachable */
                this.ipAddress = ipAddress;
            }
            catch (Exception e) {
            }
        }

        // The IP address is not a permissible value
        else {
            String errorString = "Error: Unable to validate the IP address that was assigned to printer '" + this.getName() + "'";
            this.setStatus(errorString);
            log.error(errorString);
        }
    }

    public String toString() {
        return String.format("[Printer %s: %s]", getName(), getIpAddress());
    }

    public static List<Printer> getPrinterList() {
        List<Printer> printers = new ArrayList<>();

        // Remove the PrintServiceLookup class from AppContext
        // This forces Java to rebuild its printer list during the next call to lookupPrinterServices
         Class<?>[] classes = PrintServiceLookup.class.getDeclaredClasses();
        for (int i = 0; i < classes.length; i++) {
            if ("javax.print.PrintServiceLookup$Services".equals(classes[i].getName())) {
                sun.awt.AppContext.getAppContext().remove(classes[i]);
                break;
            }
        }

        // Get platform-independent list of all printers on the server
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        // For each printer found add it to the list of printers
        for (PrintService ps : printServices) {
            Printer p = new Printer();
            p.setName(ps.getName());
            //p.setIpAddress();

            // NOTE: printer objects seem to linger on the server for a time after being uninstalled
            // Add the printer to the list only if its status is not Uninstalled
            if ((p.getStatus()).equals("Uninstalled") == false) {
                printers.add(p);
            }
        }

        return printers;
    }

    public String getStatus() {
        // The printer is in an error state
        String os = System.getProperty("os.name");
        if (this.status.startsWith("Error")) {
            return this.status;
        }

        // The printer is most likely OK and the platform is Linux
        else if (os.equals("Linux")) {
            List<String> command = Arrays.asList("lpstat","-s",this.getName());
            String result = Command.run(command, "/usr/bin/", true);

            // The printer has been uninstalled but the object is lingering on the server
            if (result.startsWith("lpstat: No destinations added.") == true) {
                this.setStatus("Uninstalled");
            }

            // The printer is most likely in good standing
            else {
                this.setStatus("OK");
            }
        }

        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String install() {
        this.setStatus("");
        String installStatus = "";
        String os = System.getProperty("os.name");

        if (os.equals("Linux")) {

            /* TODO pull command information from a database table */

            List<String> command = Arrays.asList("/usr/local/kumoreg/addprinter.sh", this.getName(), this.getModel());
            String result = Command.run(command, "", true);
            log.info(result);

            // The script to install the printer ran without problems
            if (result.startsWith("Command completed successfully")) {
                installStatus = "Printer '" + this.getName() + "' installed successfully.";
                log.info(installStatus);
                this.setStatus("OK");
            }

            // The script to install the printer encountered a problem
            else {
                installStatus = "Error: Unable to install printer '" + this.getName() + "'" + result;
                this.setStatus(installStatus);
            }
        }

        return this.getStatus();
    }

    public String uninstall() {
        this.setStatus("");
        String uninstallStatus = "";

        String os = System.getProperty("os.name");

        if (os.equals("Linux")) {
            List<String> command = Arrays.asList("lpadmin","-x",this.getName(),"2>/dev/null");
            String result = Command.run(command, "/usr/sbin/", true);

            // The printer was unexpectedly removed from the server
            if (result.contains("lpadmin: The printer or class does not exist")) {
                uninstallStatus = "Error: Printer '" + this.getName() + "' not found on the server. " + result;
                log.error(uninstallStatus);
                this.setStatus("Uninstalled");
            }

            // The command to uninstall the printer most likely succeeded
            else if (result.startsWith("Command completed successfully")) {
                uninstallStatus = "Printer '" + this.getName() + "' uninstalled successfully.";
                log.info(uninstallStatus);
                this.setStatus("Uninstalled");
            }

            // The command to uninstall the printer failed
            else {
                uninstallStatus = "Error: Unable to remove printer '" + this.getName() + "' " + result;
                log.error(uninstallStatus);
                this.setStatus(uninstallStatus);
            }
        }

        return uninstallStatus;
    }
}

