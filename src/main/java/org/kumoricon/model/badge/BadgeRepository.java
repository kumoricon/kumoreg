package org.kumoricon.model.badge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    List<Badge> findByNameStartsWithIgnoreCase(String name);
    Badge findOneByNameIgnoreCase(String name);
    List<Badge> findByVisibleTrue();
}