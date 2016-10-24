package org.kumoricon.model.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public interface BlacklistRepository extends JpaRepository<BlacklistName, Integer> {
    @Query(value = "SELECT COUNT(id) as cnt FROM blacklist WHERE firstName = ?1 AND lastName = ?2 ", nativeQuery = true)
    Integer numberOfMatches(String firstName, String lastName);

}
