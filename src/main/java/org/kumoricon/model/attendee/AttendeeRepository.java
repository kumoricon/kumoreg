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


}