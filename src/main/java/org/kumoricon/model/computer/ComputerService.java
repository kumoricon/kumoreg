package org.kumoricon.model.computer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComputerService {
    @Autowired
    private ComputerRepository repository;

    public String findPrinterNameForComputer(String ipAddress) {
        if (ipAddress == null) { return "Default"; }
        List<Computer> computers = repository.findByIpAddress(ipAddress);

        if (computers.size() > 0) {
            return computers.get(0).getPrinterName();
        } else {
            return "Default";
        }
    }
}
