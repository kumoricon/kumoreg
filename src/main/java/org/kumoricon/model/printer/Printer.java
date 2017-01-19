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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Printer class encapsulates non-persistent printer data and printer operations
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

        // Supported case where the name is a valid IP address
        if (name.trim().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            /* TODO validate that the IP address is reachable */
            this.name = name;
        }

        // Supported case where the name is a valid DNS hostname
        else if (name.trim().matches("([a-zA-Z0-9\\.\\-_ ])*")) {
            try {
                /* TODO validate that the printer exists in DNS */
                this.name = name;
            }
            catch (Exception e) {
                this.setStatus("Error: The printer name that was entered could not be found in DNS.");
            }
        }
        else {
            this.setStatus("Error: Unable to validate the printer name that was entered.");
        }
    }

    public String getModel() { return this.model; }
    public void setModel(String model) {
        //TODO add validation
        this.model = model;
    }

    public String getIpAddress() { return this.ipAddress; }
    private void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        //TODO add validation
    }

    public String toString() {
        return String.format("[Printer %s: %s]", getName(), getIpAddress());
    }

    public static List<Printer> getPrinterList() {
        List<Printer> printers = new ArrayList<Printer>();

        // Get platform-independent list of all printers on the server
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService ps : printServices) {
            Printer p = new Printer();
            p.setName(ps.getName());
            //p.setIpAddress(); /* Tried but not working yet */
            if ((p.getStatus()).equals("Uninstalled") == false) {
                printers.add(p);
            }
        }

        return printers;
    }

    public String getStatus() {
        // Return error information if any
        String os = System.getProperty("os.name");
        if (this.status.startsWith("Error")) {
            return this.status;
        }
        else if (os.equals("Linux")) {
            // Get IP address for printers on a Linux system
            List<String> command = Arrays.asList("lpstat","-s",this.getName());
            String result = Command.run(command, "/usr/bin/", true);
            if (result.startsWith("lpstat: No destinations added.") == true) {
                this.setStatus("Uninstalled");
            }
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
            List<String> command = Arrays.asList("/usr/local/bin/addprinter/addprinter.sh", this.getName(), this.getModel());
            String result = Command.run(command, "", true);
            log.info(result);
            if (result.startsWith("Command completed successfully")) {
                installStatus = "Printer '" + this.getName() + "' installed successfully.";
                log.info(installStatus);
                this.setStatus("OK");
            } else {
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
            if (result.startsWith("Command completed successfully")) {
                uninstallStatus = "Printer '" + this.getName() + "' uninstalled successfully.";
                log.info(uninstallStatus);
                this.setStatus("Uninstalled");
            } else {
                uninstallStatus = "Error: Unable to remove printer '" + this.getName() + "'" + result;
                log.info(uninstallStatus);
                this.setStatus(uninstallStatus);
            }
        }

        this.setStatus(uninstallStatus);
        return uninstallStatus;
    }
}

