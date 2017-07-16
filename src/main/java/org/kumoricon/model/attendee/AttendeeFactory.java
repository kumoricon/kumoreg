package org.kumoricon.model.attendee;

import com.vaadin.server.ServiceException;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AttendeeFactory {

    private static final Logger log = LoggerFactory.getLogger(AttendeeFactory.class);

    private final BadgeRepository badgeRepository;

    @Autowired
    public AttendeeFactory(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public Attendee generateDemoAttendee(Badge badge) {
        Attendee attendee = new Attendee();
        attendee.setFirstName("Test");
        attendee.setLastName("Guy");
        attendee.setFanName("SuperFlyGuy");
        attendee.setBadgeNumber("TST12340");
        attendee.setBadge(badge);
        attendee.setCountry("United States of America");
        attendee.setZip("97201");
        attendee.setPhoneNumber("123-123-1234");
        attendee.setCheckedIn(true);
        attendee.setEmergencyContactFullName("Mom");
        attendee.setEmergencyContactPhone("321-321-4321");
        attendee.setBirthDate(LocalDate.now().minusYears(30L));
        attendee.setPaid(true);
        try {
            attendee.setPaidAmount(attendee.getBadge().getCostForAge(attendee.getAge()));
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
        }
        return attendee;
    }

    public Attendee generateYouthAttendee(Badge badge) {
        Attendee attendee = generateDemoAttendee(badge);
        attendee.setFirstName("Timmy");
        attendee.setFanName("Teen-o-rama");
        attendee.setBadgeNumber("TST12341");
        attendee.setBirthDate(LocalDate.now().minusYears(13L));
        attendee.setParentFullName(attendee.getEmergencyContactFullName());
        attendee.setParentPhone(attendee.getEmergencyContactPhone());
        attendee.setParentIsEmergencyContact(true);
        try {
            attendee.setPaidAmount(attendee.getBadge().getCostForAge(attendee.getAge()));
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
        }

        return attendee;
    }

    public Attendee generateChildAttendee(Badge badge) {
        Attendee attendee = generateDemoAttendee(badge);
        attendee.setFirstName("Billy");
        attendee.setFanName("Whippersnapper");
        attendee.setBirthDate(LocalDate.now().minusYears(7L));
        attendee.setBadgeNumber("TST12342");
        try {
            attendee.setPaidAmount(attendee.getBadge().getCostForAge(attendee.getAge()));
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
        }
        return attendee;
    }

}
