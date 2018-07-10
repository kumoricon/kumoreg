package org.kumoricon.service.print.formatter.badgeimage;

public interface BadgeCreator {
    /**
     * Creates a badge image from the given information and returns a byte array
     * of the PNG.
     * @param attendee Attendee data
     * @return PNG byte array
     */
    byte[] createBadge(AttendeeBadgeDTO attendee);
}
