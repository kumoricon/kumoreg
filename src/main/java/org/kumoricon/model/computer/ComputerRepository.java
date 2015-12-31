package org.kumoricon.model.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface ComputerRepository extends JpaRepository<Computer, Integer> {
//    List<Computer> findByAddressStartsWithCaseInsensitive(String ipAddress);
}