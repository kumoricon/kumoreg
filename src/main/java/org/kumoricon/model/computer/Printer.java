package org.kumoricon.model.computer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Entity
public class Printer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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
}
