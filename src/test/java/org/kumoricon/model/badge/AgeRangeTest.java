package org.kumoricon.model.badge;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgeRangeTest {
    @Before
    public void setUp() throws Exception {
        ageRange = new AgeRange();
    }

    private AgeRange ageRange;

    /*
     0 is the minimum age, and the maximum age is 255 for historic reasons. "255" was treated as "+" when displaying
     the age range (so it was easy to print things like "0-5, 6-17, 18+") The setters should clamp values
     to that range. Those limits are also set in the database.
     */
    @Test
    public void setMinAgeGTE0() throws Exception {
        ageRange.setMinAge(-1);
        assertEquals(0, ageRange.getMinAge());
    }

    @Test
    public void setMinAgeLTE255() throws Exception {
        ageRange.setMinAge(256);
        assertEquals(255, ageRange.getMinAge());
    }

    @Test
    public void setMaxAgeGTE0() throws Exception {
        ageRange.setMaxAge(-1);
        assertEquals(0, ageRange.getMaxAge());
    }

    @Test
    public void setMaxAgeLTE255() throws Exception {
        ageRange.setMaxAge(256);
        assertEquals(255, ageRange.getMaxAge());
    }


    @Test
    public void setCostGTE0() throws Exception {
        ageRange.setCost(BigDecimal.valueOf(-1.1));
        assertEquals(BigDecimal.ZERO, ageRange.getCost());
    }

    @Test
    public void isValidForAge() throws Exception {
        ageRange.setMinAge(6);
        ageRange.setMaxAge(18);
        assertFalse(ageRange.isValidForAge(-1));
        assertFalse(ageRange.isValidForAge(0));
        assertFalse(ageRange.isValidForAge(5));
        assertTrue(ageRange.isValidForAge(6));
        assertTrue(ageRange.isValidForAge(7));
        assertTrue(ageRange.isValidForAge(17));
        assertTrue(ageRange.isValidForAge(18));
        assertFalse(ageRange.isValidForAge(19));
    }

}