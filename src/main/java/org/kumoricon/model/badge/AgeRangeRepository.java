package org.kumoricon.model.badge;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SpringComponent
public interface AgeRangeRepository extends JpaRepository<AgeRange, Integer> {
    List<AgeRange> findByNameStartsWithIgnoreCase(String name);
}