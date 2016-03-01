package org.kumoricon.model.computer;

import javax.persistence.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Entity
@Table(name = "printers")
public class Printer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private InetAddress address;

    public String toString() { return "Printer at " + address.getHostAddress(); }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public InetAddress getAddress() { return address; }
    public void setAddress(InetAddress address) { this.address = address; }
    public void setAddress(byte[] address) throws Exception { this.address = InetAddress.getByAddress(address); }

    public void setAddress(String hostName) throws UnknownHostException {
        this.address = InetAddress.getByName(hostName);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Printer))
            return false;
        if (this.getId() == null) {
            return this == other;
        } else {
            Printer o = (Printer) other;
            return this.getId().equals(o.getId());
        }
    }

    @Override
    public int hashCode() {
        return getAddress().hashCode();
    }
}
