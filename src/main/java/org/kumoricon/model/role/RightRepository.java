package org.kumoricon.model.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface RightRepository extends JpaRepository<Right, Integer> {
    Right findByNameIgnoreCase(String search);
}
