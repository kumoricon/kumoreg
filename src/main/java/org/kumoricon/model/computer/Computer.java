package org.kumoricon.model.computer;

import javax.persistence.*;

@Entity
@Table(name = "computers")
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ipAddress;
    private String printerName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getPrinterName() { return printerName; }
    public void setPrinterName(String printerName) { this.printerName = printerName; }

    public String toString() {
        return String.format("(%d) %s -> printer %s", id, ipAddress, printerName);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Computer))
            return false;
        if (this.getId() == null) {
            return this == other;
        } else {
            Computer o = (Computer) other;
            return this.getId().equals(o.getId());
        }
    }

//    @Override
//    public int hashCode() {
//        return getId().hashCode();
//    }
}
