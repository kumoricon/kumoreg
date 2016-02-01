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

    @Query(value = "SELECT badges.name as Badge, IFNULL(atConCheckedIn.cnt, 0) as AtConCheckedIn, IFNULL(atConNotCheckedIn.cnt, 0) as AtConNotCheckedIn, IFNULL(preRegCheckedIn.cnt, 0) as PreRegCheckedIn, IFNULL(preRegNotCheckedIn.cnt, 0) as PreRegNotCheckedIn FROM badges LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.checked_in) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checked_in = TRUE AND attendees.pre_registered = FALSE GROUP BY badges.id) as atConCheckedIn ON badges.id = atConCheckedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.id) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checked_in = FALSE AND attendees.pre_registered = FALSE GROUP BY badges.id) as atConNotCheckedIn ON badges.id = atConNotCheckedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.checked_in) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checked_in = TRUE AND attendees.pre_registered = TRUE GROUP BY badges.id) as preRegCheckedIn ON badges.id = preRegCheckedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.id) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checked_in = FALSE AND attendees.pre_registered = TRUE GROUP BY badges.id) as preRegNotCheckedIn ON badges.id = preRegNotCheckedIn.subid;", nativeQuery = true)
    List<Object[]> findBadgeCounts();
}