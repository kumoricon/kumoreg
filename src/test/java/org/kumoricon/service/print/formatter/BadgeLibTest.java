package org.kumoricon.service.print.formatter;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BadgeLibTest {
    @Test
    public void splitBadgeNumberNormal() throws Exception {
        List<String> result = BadgeLib.splitBadgeNumber("TST12345");
        assertTrue("TST".equals(result.get(0)));
        assertTrue("12345".equals(result.get(1)));
        assertEquals(2, result.size());
    }

    @Test
    public void splitBadgeNumberShort() throws Exception {
        List<String> result = BadgeLib.splitBadgeNumber("TS123");
        assertTrue("TS".equals(result.get(0)));
        assertTrue("123".equals(result.get(1)));
        assertEquals(2, result.size());
    }

    @Test
    public void splitBadgeNumberLong() throws Exception {
        List<String> result = BadgeLib.splitBadgeNumber("TEST123456");
        assertTrue("TEST".equals(result.get(0)));
        assertTrue("123456".equals(result.get(1)));
        assertEquals(2, result.size());
    }

    @Test
    public void splitBadgeNumberWrongFormat() throws Exception {
        List<String> result = BadgeLib.splitBadgeNumber("12345");
        assertTrue("12345".equals(result.get(0)));
        assertEquals(1, result.size());
    }

}