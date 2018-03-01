package org.kumoricon.scheduledtasks.staffimport;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class PersonTest {
    @Test
    public void isDifferentString() {
        assertTrue(Person.isDifferent("abc", "def"));
        assertTrue(Person.isDifferent(null, "abc"));
        assertFalse(Person.isDifferent((String)null, null));
    }

    @Test
    public void isDifferentBoolean() {
        assertTrue(Person.isDifferent(true, false));
        assertTrue(Person.isDifferent(false, true));
        assertFalse(Person.isDifferent(true, true));
        assertFalse(Person.isDifferent(false, false));
    }

    @Test
    public void isDifferentLocalDate() {
        LocalDate localDate1 = LocalDate.of(2017, 1, 1);
        LocalDate localDate2 = LocalDate.of(2017, 1, 2);
        LocalDate localDate3 = LocalDate.of(2017, 1, 1);
        assertTrue(Person.isDifferent(localDate1, localDate2));
        assertTrue(Person.isDifferent(null, localDate2));
        assertFalse(Person.isDifferent(localDate1, localDate3));
    }

    @Test
    public void isDifferentBigDecimal() {
        assertTrue(Person.isDifferent(BigDecimal.ZERO, BigDecimal.ONE));
        assertTrue(Person.isDifferent(null, BigDecimal.ZERO));
        assertFalse(Person.isDifferent(BigDecimal.TEN, BigDecimal.TEN));
    }

}