package org.kumoricon.model.attendee;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface AttendeeRepository extends JpaRepository<Attendee, Integer>, JpaSpecificationExecutor {

    @Query(value = "select a from Attendee a inner join a.order as o where o.orderId LIKE ?1 OR a.badgeNumber LIKE ?1")
    List<Attendee> findByBadgeNumberOrOrderId(String searchString);

    @Query(value = "select a from Attendee a where a.lastName like ?1% or a.badgeNumber like ?1%")
    List<Attendee> findByLastNameOrBadgeNumber(String searchString);
    List<Attendee> findByOrder(Order order);

    @Query(value = "select a from Attendee a where a.lastName like ?1% and a.checkedIn = false")
    List<Attendee> findNotCheckedInByLastName(String searchString);

    @Query(value = "select a from Attendee a inner join a.order as order where order.orderId like ?1")
    List<Attendee> findByOrderNumber(String searchString);

    @Query(value = "SELECT badges.name as Badge, IFNULL(atConCheckedIn.cnt, 0) as AtConCheckedIn, IFNULL(atConNotCheckedIn.cnt, 0) as AtConNotCheckedIn, IFNULL(preRegCheckedIn.cnt, 0) as PreRegCheckedIn, IFNULL(preRegNotCheckedIn.cnt, 0) as PreRegNotCheckedIn FROM badges LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.checkedIn) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checkedIn = TRUE AND attendees.preRegistered = FALSE GROUP BY badges.id) as atConCheckedIn ON badges.id = atConCheckedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.id) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checkedIn = FALSE AND attendees.preRegistered = FALSE GROUP BY badges.id) as atConNotCheckedIn ON badges.id = atConNotCheckedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.checkedIn) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checkedIn = TRUE AND attendees.preRegistered = TRUE GROUP BY badges.id) as preRegCheckedIn ON badges.id = preRegCheckedIn.subid LEFT OUTER JOIN (SELECT badges.id as subid, COUNT(attendees.id) as cnt FROM badges JOIN attendees ON badges.id = attendees.badge_id WHERE attendees.checkedIn = FALSE AND attendees.preRegistered = TRUE GROUP BY badges.id) as preRegNotCheckedIn ON badges.id = preRegNotCheckedIn.subid;", nativeQuery = true)
    List<Object[]> findBadgeCounts();

    @Query(value = "SELECT DATE(checkInTime) as CheckInDate, COUNT(id) AS cnt, SUM(paidAmount) as Amount FROM attendees WHERE checkedIn = TRUE AND preRegistered = FALSE GROUP BY CheckInDate ORDER BY CheckInDate;", nativeQuery = true)
    List<Object[]> findAtConCheckInCountsByDate();

    @Query(value = "SELECT DATE(checkInTime) as CheckInDate, COUNT(id) AS cnt, SUM(paidAmount) as Amount FROM attendees WHERE checkedIn = TRUE AND preRegistered = TRUE GROUP BY CheckInDate ORDER BY CheckInDate;", nativeQuery = true)
    List<Object[]> findPreRegCheckInCountsByDate();

    @Query(value = "SELECT DATE(checkInTime) as checkInDate, HOUR(checkInTime) as checkInHour, IFNULL(atConCheckedIn.cnt, 0) as AtConCheckedIn, IFNULL(preRegCheckedIn.cnt, 0) as PreRegCheckedIn, COUNT(checkedIn) as Total FROM attendees LEFT OUTER JOIN (SELECT DATE(checkInTime) as aCheckInDate, HOUR(checkInTime) as aCheckInHour, COUNT(attendees.checkedIn) as cnt FROM attendees  WHERE attendees.checkedIn = TRUE AND attendees.preRegistered = TRUE GROUP BY aCheckInDate, aCheckInHour) as preRegCheckedIn ON DATE(checkInTime) = preRegCheckedIn.aCheckInDate AND HOUR(checkInTime) = preRegCheckedIn.aCheckInHour LEFT OUTER JOIN (SELECT DATE(checkInTime) as aCheckInDate, HOUR(checkInTime) as aCheckInHour, COUNT(attendees.checkedIn) as cnt FROM attendees  WHERE attendees.checkedIn = TRUE AND attendees.preRegistered = FALSE GROUP BY aCheckInDate, aCheckInHour) as atConCheckedIn ON DATE(checkInTime) = atConCheckedIn.aCheckInDate AND HOUR(checkInTime) = atConCheckedIn.aCheckInHour WHERE checkedIn = TRUE GROUP BY checkInDate, checkInHour, atConCheckedIn.cnt, preRegCheckedIn.cnt;", nativeQuery = true)
    List<Object[]> findCheckInCountsByHour();

    // Warm body count includes all badges
    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT firstName, lastName, zip, birthDate FROM attendees WHERE checkedIn=TRUE) as t", nativeQuery = true)
    Integer findWarmBodyCount();

    // Total attendee count calculation. From: https://www.kumoricon.org/history
    // Attendance figures for all years are unique, paid (rather than "turnstile")—this means that weekend badges are
    // counted only once, and the count is a number of unique individual attendees who registered in a given year.
    // Attendance figures count paid membership purchases at standard or VIP rates (staff, exhibitors, artists,
    // guests, industry, press, and complimentary badges are not counted). Prior to 2014, multiple single-day badges
    // were double-counted (for example, a person purchases Saturday, then Sunday the next day); for 2014 and after,
    // only one is counted (this is an estimated less than 2% discrepancy).
    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT firstName, lastName, zip, birthDate FROM attendees WHERE checkedIn=TRUE AND paidAmount > 0) as t", nativeQuery = true)
    Integer findTotalAttendeeCount();

    @Query(value = "select a from Attendee a where a.badge in (select b from Badge b where b.name like '%Panelist%')")
    List<Attendee> findPanelists();

    @Query(value = "select a from Attendee a where a.badge = ?1")
    List<Attendee> findByBadgeType(Badge badge);

    @Transactional
    @Modifying
    @Query(value = "delete from Attendee a where a.id = ?1")
    void deleteById(Integer id);
}