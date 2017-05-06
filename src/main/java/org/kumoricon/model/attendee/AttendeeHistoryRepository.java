package org.kumoricon.model.attendee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface AttendeeHistoryRepository extends JpaRepository<AttendeeHistory, Integer> {

    @Query(value = "SELECT ah from AttendeeHistory ah where ah.attendee = ?1 ORDER BY ah.timestamp ASC")
    Set<AttendeeHistory> findByAttendee(Attendee attendee);

    @Query(value = "SELECT users.firstName, users.lastName, COUNT(attendeehistory.id) FROM attendeehistory JOIN users ON attendeehistory.user_id = users.id WHERE message=\"Attendee checked in\" AND timestamp >= NOW() - INTERVAL 15 MINUTE AND attendeehistory.timestamp <= NOW() GROUP BY user_id", nativeQuery = true)
    List<Object[]> checkInCountByUsers();
}
