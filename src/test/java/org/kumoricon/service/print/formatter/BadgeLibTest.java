package org.kumoricon.service.print.formatter;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BadgeLibTest {
    @Test
    public void wrapPositionsMultiplePositionsNotWrapped() {
        List<String> input = Arrays.asList("Position 1 is really really long", "Position 2");
        List<String> output = BadgeLib.wrapPositions(input,18);
        assertArrayEquals(input.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsEmptyPositionsNotWrapped() {
        List<String> input = Arrays.asList();
        List<String> output = BadgeLib.wrapPositions(input, 18);
        assertArrayEquals(input.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsSingleShortPositionNotWrapped() {
        List<String> input = Arrays.asList("Position 1");
        List<String> output = BadgeLib.wrapPositions(input, 18);
        assertArrayEquals(input.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsSinglePositionsWrapped() {
        List<String> input = Arrays.asList("Position 1 is really really long");
        List<String> expected = Arrays.asList("Position 1 is really", "really long");
        List<String> output = BadgeLib.wrapPositions(input, 18);
        assertArrayEquals(expected.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsNullInput() {
        List<String> output = BadgeLib.wrapPositions(null, 18);
        List<String> expected = new ArrayList<>();
        assertArrayEquals(expected.toArray(), output.toArray());
    }

    @Test
    public void wrapPositionsAssistantDirector() {
        List<String> input = Arrays.asList("Assistant Director of Operations");
        List<String> output = BadgeLib.wrapPositions(input, 18);
        List<String> expected = Arrays.asList("Assistant Director of", "Operations");
        assertArrayEquals(expected.toArray(), output.toArray());
    }


    @Test
    public void getForegroundColorForBlack() {
        assertEquals(Color.WHITE, BadgeLib.getForegroundColor("#000000"));
    }

    @Test
    public void getForegroundColorForWhite() {
        assertEquals(Color.BLACK, BadgeLib.getForegroundColor("#FFFFFF"));
    }

    @Test
    public void getForegroundColorForBrightYellow() {
        assertEquals(Color.BLACK, BadgeLib.getForegroundColor("#FFFF00"));
    }

    @Test
    public void splitBadgeNumberNormal() {
        List<String> result = BadgeLib.splitBadgeNumber("TST12345");
        assertTrue("TST".equals(result.get(0)));
        assertTrue("12345".equals(result.get(1)));
        assertEquals(2, result.size());
    }

    @Test
    public void splitBadgeNumberShort() {
        List<String> result = BadgeLib.splitBadgeNumber("TS123");
        assertTrue("TS".equals(result.get(0)));
        assertTrue("123".equals(result.get(1)));
        assertEquals(2, result.size());
    }

    @Test
    public void splitBadgeNumberLong() {
        List<String> result = BadgeLib.splitBadgeNumber("TEST123456");
        assertTrue("TEST".equals(result.get(0)));
        assertTrue("123456".equals(result.get(1)));
        assertEquals(2, result.size());
    }

    @Test
    public void splitBadgeNumberWrongFormat() {
        List<String> result = BadgeLib.splitBadgeNumber("12345");
        assertTrue("12345".equals(result.get(0)));
        assertEquals(1, result.size());
    }

}