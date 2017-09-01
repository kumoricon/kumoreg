package org.kumoricon.scheduledtasks.staffimport;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.service.print.formatter.BadgeLib;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Person {
    public int id;
    public String namePreferred;
    public String namePreferredFirst;
    public String namePreferredLast;
    public String nameOnId;
    public String nameOnIdFirst;
    public String nameOnIdLast;
    public String namePrivacy;
    public String namePrivacyFirst;
    public String namePrivacyLast;
    public LocalDate birthdate;
    public String ageCategoryConCurrentTerm;
    public String badgeImpactingLastModified;
    public List<Position> positions;
    public boolean hasBadgeImage;
    public String badgeImageFileType;

    public Attendee toAttendee() {
        Attendee a = new Attendee();
        updateAttendee(a);
        return a;
    }


    /**
     * Updates the given Attendee object with values from this Person object.
     * Returns true if the Attendee was updated, false if it was not
     * @param attendee Attende object mutated in place
     * @return boolean
     */
    public boolean updateAttendee(Attendee attendee) {
        boolean updated = false;

        if (isDifferent(attendee.getFirstName(), namePrivacyFirst)) {
            updated = true;
            attendee.setFirstName(namePrivacyFirst);
        }
        if (isDifferent(attendee.getLastName(), namePrivacyLast)) {
            updated = true;
            attendee.setLastName(namePrivacyLast);
        }
        if (isDifferent(attendee.getLegalFirstName(), nameOnIdFirst)) {
            updated = true;
            attendee.setLegalFirstName(nameOnIdFirst);
        }
        if (isDifferent(attendee.getLegalLastName(), nameOnIdLast)) {
            updated = true;
            attendee.setLegalLastName(nameOnIdLast);
        }

        // Treat all staff as prereg, even if they actually sign up at con
        // This is sort of incorrect, but seems better than the alternative to me right now
        if (isDifferent(attendee.isPreRegistered(), true)) {
            updated = true;
            attendee.setPreRegistered(true);
        }

        if (isDifferent(attendee.getBirthDate(), birthdate)) {
            updated = true;
            attendee.setBirthDate(birthdate);
        }

        // Phone number isn't in imported data, add dummy information
        if (isDifferent(attendee.getPhoneNumber(), "555-555-5555")) {
            updated = true;
            attendee.setPhoneNumber("555-555-5555");
        }

        // Emergency contact isn't in imported data, add dummy information
        if (isDifferent(attendee.getEmergencyContactFullName(), "See Staff site")) {
            updated = true;
            attendee.setEmergencyContactFullName("See Staff site");
        }

        // Emergency contact isn't in imported data, add dummy information
        if (isDifferent(attendee.getEmergencyContactPhone(), "555-555-5555")) {
            updated = true;
            attendee.setEmergencyContactPhone("555-555-5555");
        }

        String parentName = "";
        String parentPhone = "";
        if (attendee.isMinor()) {
            parentName = "See Staff site";
            parentPhone = "555-555-5555";
        }
        if (isDifferent(attendee.getParentFullName(), parentName)) {
            updated = true;
            attendee.setParentFullName(parentName);
        }
        if (isDifferent(attendee.getParentPhone(), parentPhone)) {
            updated = true;
            attendee.setParentPhone(parentPhone);
        }

        if (isDifferent(attendee.getStaffIDNumber(), String.format("%s", id))) {
            updated = true;
            attendee.setStaffIDNumber(String.format("%s", id));
        }

        List<String> positionStrings = new ArrayList<>();
        // Todo: Check if lists are equal and update
        for (Position p : positions) {
            positionStrings.add(p.title);
        }
        attendee.setStaffPositions(positionStrings);
        // Suppress department if it's in their title. Todo: What if one department has suppressed=true
        //                                                   and others have suppressed=false?
        if (positions.size() > 0) {
            if (positions.get(0).departmentSupresesd) {
                attendee.setStaffDepartment("");
            } else {
                attendee.setStaffDepartment(positions.get(0).department);
            }
        }

        String colorCode = BadgeLib.findDepartmentColorCode(positions.get(0).department);
        if (isDifferent(colorCode, attendee.getStaffDepartmentColor())) {
            updated = true;
            attendee.setStaffDepartmentColor(colorCode);
        }

        if (isDifferent(attendee.getCompedBadge(), true)) {
            updated = true;
            attendee.setCompedBadge(true);
        }

        if (isDifferent(attendee.getPaid(), true)) {
            updated = true;
            attendee.setPaid(true);
        }

        if (isDifferent(attendee.getPaidAmount(), BigDecimal.ZERO)) {
            updated = true;
            attendee.setPaidAmount(BigDecimal.ZERO);
        }

        String filename = null;
        if (hasBadgeImage) {
            filename = String.format("%s.%s", id, badgeImageFileType);
        }

        if (isDifferent(attendee.getStaffImageFilename(), filename)) {
            updated = true;
            attendee.setStaffImageFilename(filename);
        }

        return updated;
    }

    static boolean isDifferent(String string1, String string2) {
        return (string1 == null ? string2 != null : !string1.equals(string2));
    }

    static boolean isDifferent(boolean boolean1, boolean boolean2) {
        return boolean1 != boolean2;
    }

    static boolean isDifferent(LocalDate localDate1, LocalDate localDate2) {
        if (localDate1 == null) {
            return localDate2 != null;
        } else {
            return !localDate1.equals(localDate2);
        }
    }

    static boolean isDifferent(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        if (bigDecimal1 == null) {
            return bigDecimal2 != null;
        } else {
            return !bigDecimal1.equals(bigDecimal2);
        }
    }
}
