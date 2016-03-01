package org.kumoricon.model.computer;

import javax.persistence.*;
import java.net.InetAddress;

@Entity
@Table(name = "computers")
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private java.net.InetAddress address;
    @OneToOne
    private Printer printer;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public InetAddress getAddress() { return address; }
    public void setAddress(InetAddress address) { this.address = address; }
    public void setAddress(byte[] address) throws Exception { this.address = InetAddress.getByAddress(address); }

    public Printer getPrinter() { return printer; }
    public void setPrinter(Printer printer) { this.printer = printer; }

    public String toString() {
        return String.format("Computer %s with printer %s", address.toString(), getPrinter().getAddress().toString());
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

    @Override
    public int hashCode() {
        return getAddress().hashCode();
    }
}
