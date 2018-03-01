package org.kumoricon.model.badge;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BadgeFactoryTest {
    @Test
    public void createBadge() {
        Badge b = BadgeFactory.createBadge("Weekend Test", BadgeType.ATTENDEE, "Weekend", "#00FF00", 0f, 0f, 0f);
        assertEquals("Weekend Test", b.getName());
        assertEquals("Weekend", b.getBadgeTypeText());
        assertEquals("#00FF00", b.getBadgeTypeBackgroundColor());
        assertEquals(BadgeType.ATTENDEE, b.getBadgeType());
        for (AgeRange a : b.getAgeRanges()) {
            assertEquals(BigDecimal.ZERO, a.getCost());
        }
        assertNull(b.getWarningMessage());
        assertNull(b.getRequiredRight());
    }

    @Test
    public void createEmptyBadge() {
        Badge b = BadgeFactory.createEmptyBadge();
        assertEquals(4, b.getAgeRanges().size());
        assertEquals("", b.getName());
        assertEquals("", b.getBadgeTypeText());
        assertNull(b.getRequiredRight());
        assertNull(b.getWarningMessage());
    }
}