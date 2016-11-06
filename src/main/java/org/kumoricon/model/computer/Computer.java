package org.kumoricon.model.computer;

import org.kumoricon.model.Record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "computers")
public class Computer extends Record {
    /**
     * Client computer's IP address
     */
    @NotNull
    @Column(unique=true)
    private String ipAddress;
    /**
     * Name of printer as it is installed on the server
     */
    private String printerName;
    /**
     * Horizontal page offset in points (1/72 inch) when printing pages. >0 moves right, <0 moves left
     */
    private Integer xOffset;
    /**
     * Vertical page offset in points (1/72 inch) when printing pages. >0 moves up, <0 moves down
     */
    private Integer yOffset;

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getPrinterName() { return printerName; }
    public void setPrinterName(String printerName) { this.printerName = printerName; }

    public Integer getxOffset() { return xOffset; }
    public void setxOffset(Integer xOffset) { this.xOffset = xOffset; }

    public Integer getyOffset() { return yOffset; }
    public void setyOffset(Integer yOffset) { this.yOffset = yOffset; }

    public String toString() {
        if (id != null) {
            return String.format("[Computer %s: %s]", id, ipAddress);
        } else {
            return String.format("[Computer: %s]", ipAddress);
        }
    }

}
