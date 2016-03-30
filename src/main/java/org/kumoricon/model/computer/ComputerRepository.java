package org.kumoricon.model.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComputerRepository extends JpaRepository<Computer, Integer> {
    List<Computer> findByIpAddress(String ipAddress);
}