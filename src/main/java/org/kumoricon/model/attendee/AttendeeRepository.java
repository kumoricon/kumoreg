package org.kumoricon.model.attendee;

import org.kumoricon.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
    List<Attendee> findByLastNameStartsWithIgnoreCase(String lastName);

    @Query(value = "select a from Attendee a where a.lastName like ?1% or a.badgeNumber like ?1%")
    List<Attendee> findByLastNameOrBadgeNumber(String searchString);
    List<Attendee> findByOrder(Order order);

    @Query(value = "select a.badge, count(a) from Attendee a group by a.badge")
    List<Object[]> findCountPerBadgeType();

    @Query(value = "select a.badge, count(a) from Attendee a where a.checkedIn = true group by a.badge ")
    List<Object[]> findCountPerBadgeTypeCheckedIn();

    @Query(value = "select a.badge, count(a) from Attendee a where a.checkedIn = false group by a.badge ")
    List<Object[]> findCountPerBadgeTypeNotCheckedIn();

    @Query(value = "select a from Attendee a where a.lastName like ?1% and a.checkedIn = false")
    List<Attendee> findNotCheckedInByLastName(String searchString);

    @Query(value = "select a from Attendee a inner join a.order as order where order.orderId like ?1")
    List<Attendee> findByOrderNumber(String searchString);

    @Query(value = "SELECT badges.name as Badge, IFNULL(checkedIn.cnt, 0) as CheckedIn, IFNULL(notCheckedIn.cnt, 0) as NotCheckedIn FROM badges LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.checked_in) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checked_in = TRUE GROUP BY badges.id) as checkedIn ON badges.id = checkedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.id) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checked_in = FALSE GROUP BY badges.id) as notCheckedIn ON badges.id = notCheckedIn.subid", nativeQuery = true)
    List<Object[]> findBadgeCounts();
}