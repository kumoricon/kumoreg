package org.kumoricon.site.computer;

import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import java.util.List;


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

    public void showComputerList(ComputerView view) {
        log.info("{} viewed computer list", view.getCurrentUsername());
        List<Computer> computers = computerRepository.findAll();
        view.afterSuccessfulFetch(computers);
    }

    public void deleteComputer(ComputerView view, Computer computer) {
        log.info("{} deleted computer {}", view.getCurrentUsername(), computer);
        computerRepository.delete(computer);
        view.notify("Deleted " + computer.getIpAddress());
        view.afterSuccessfulFetch(computerRepository.findAll());
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
