package org.kumoricon.model.printer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.kumoricon.model.Record;

@Entity
@Table(name = "printers")
public class Printer extends Record {

    public Printer(String _ipAddress, String _name, String _model)
    {
        this.ipAddress = _ipAddress;
        this.name = _name;
        this.model = _model;
    }

    /**
     * Printer's IP address
     */
    @NotNull
    @Column(unique=true)
    private String ipAddress;

    /**
     * Name of printer as it is installed on the server
     */
    private String name;

    /**
     * Model of the printer
     */
    private String model;

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String toString() {
        if (id != null) {
            return String.format("[Computer %s: %s]", id, ipAddress);
        } else {
            return String.format("[Computer: %s]", ipAddress);
        }
    }

}

