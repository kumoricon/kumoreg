package org.kumoricon.model.computer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComputerService {
    @Autowired
    private ComputerRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ComputerService.class);


    /**
     * Finds a computer record or returns a Computer object with default information
     * (Destination printer "Default" with no offsets)
     * @param ipAddress IP address to mySearch for
     * @return Computer
     */
    public Computer findComputerByIP(String ipAddress) {
        Computer computer = repository.findOneByIpAddress(ipAddress);
        if (computer != null) {
            return computer;
        } else {
            log.warn("Couldn't find a Computer record for {}, using default values", ipAddress);
            Computer defaultClient = new Computer();
            defaultClient.setIpAddress(null);
            defaultClient.setPrinterName("Default");
            defaultClient.setxOffset(0);
            defaultClient.setyOffset(0);
            return defaultClient;
        }

    }
}
