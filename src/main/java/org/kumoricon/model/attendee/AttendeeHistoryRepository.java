package org.kumoricon.model.attendee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface AttendeeHistoryRepository extends JpaRepository<AttendeeHistory, Integer> {

    @Query(value = "SELECT ah from AttendeeHistory ah where ah.attendee = ?1 ORDER BY ah.timestamp ASC")
    Set<AttendeeHistory> findByAttendee(Attendee attendee);
}
