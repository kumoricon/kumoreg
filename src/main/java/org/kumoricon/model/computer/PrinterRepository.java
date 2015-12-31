package org.kumoricon.model.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface PrinterRepository extends JpaRepository<Printer, Integer> {
//    List<Printer> findByAddressStartsWithCaseInsensitive(String ipAddress);
}