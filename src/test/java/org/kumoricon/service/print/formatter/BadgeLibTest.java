package org.kumoricon.service.print.formatter;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BadgeLibTest {
    @Test
    public void wrapPositionsMultiplePositionsNotWrapped() throws Exception {
        List<String> input = Arrays.asList("Position 1 is really really long", "Position 2");
        List<String> output = BadgeLib.wrapPositions(input);
        assertArrayEquals(input.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsEmptyPositionsNotWrapped() throws Exception {
        List<String> input = Arrays.asList();
        List<String> output = BadgeLib.wrapPositions(input);
        assertArrayEquals(input.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsSingleShortPositionNotWrapped() throws Exception {
        List<String> input = Arrays.asList("Position 1");
        List<String> output = BadgeLib.wrapPositions(input);
        assertArrayEquals(input.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsSinglePositionsWrapped() throws Exception {
        List<String> input = Arrays.asList("Position 1 is really really long");
        List<String> expected = Arrays.asList("Position 1 is really", "really long");
        List<String> output = BadgeLib.wrapPositions(input);
        assertArrayEquals(expected.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsNullInput() throws Exception {
        List<String> output = BadgeLib.wrapPositions(null);
        List<String> expected = new ArrayList<>();
        assertArrayEquals(expected.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsAssistantDirector() throws Exception {
        List<String> input = Arrays.asList("Assistant Director of Operations");
        List<String> output = BadgeLib.wrapPositions(input);
        List<String> expected = Arrays.asList("Assistant Director of", "Operations");
        assertArrayEquals(expected.toArray(), output.toArray());
    }


    @Test
    public void getForegroundColorForBlack() throws Exception {
        assertEquals(Color.WHITE, BadgeLib.getForegroundColor("#000000"));
    }

    @Test
    public void getForegroundColorForWhite() throws Exception {
        assertEquals(Color.BLACK, BadgeLib.getForegroundColor("#FFFFFF"));
    }

    @Test
    public void getForegroundColorForBrightYellow() throws Exception {
        assertEquals(Color.BLACK, BadgeLib.getForegroundColor("#FFFF00"));
    }

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