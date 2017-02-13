package org.kumoricon.model.badge;

/**
 * Represents the type of badge, used for calculations and reporting
 */
public enum BadgeType {
    ATTENDEE, STAFF, OTHER;

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
