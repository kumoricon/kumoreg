package org.kumoricon.model.badge;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BadgeFactoryTest {
    @Test
    public void createBadge() throws Exception {
        Badge b = BadgeFactory.createBadge("Weekend Test", "Weekend", 0f, 0f, 0f);
        assertEquals("Weekend Test", b.getName());
        assertEquals("Weekend", b.getDayText());
        for (AgeRange a : b.getAgeRanges()) {
            assertEquals(BigDecimal.ZERO, a.getCost());
        }
        assertNull(b.getWarningMessage());
        assertNull(b.getRequiredRight());
    }

    @Test
    public void createEmptyBadge() throws Exception {
        Badge b = BadgeFactory.createEmptyBadge();
        assertEquals(4, b.getAgeRanges().size());
        assertEquals("", b.getName());
        assertEquals("", b.getDayText());
        assertNull(b.getRequiredRight());
        assertNull(b.getWarningMessage());
    }

    @Test
    public void createBadgeOverrideStripeColor() throws Exception {
        Badge b = BadgeFactory.createBadge("Weekend Test", "Weekend", 0f, 0f, 0f, "#EFEFEF");
        assertEquals("Weekend Test", b.getName());
        assertEquals("Weekend", b.getDayText());
        for (AgeRange a : b.getAgeRanges()) {
            assertEquals(BigDecimal.ZERO, a.getCost());
            // Make sure stripe color is only overridden for adults
            if (a.getName().equals("Adult")) {
                assertEquals("#EFEFEF", a.getStripeColor());
            } else {
                assertNotEquals("#EFEFEF", a.getStripeColor());
            }
        }
        assertNull(b.getWarningMessage());
        assertNull(b.getRequiredRight());
    }

}