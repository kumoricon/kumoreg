package org.kumoricon.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldCleanerTest {
    @Test
    public void testCleanNameTrimsWhitespace() {
        assertEquals("Test", FieldCleaner.cleanName("  Test  \n"));
    }

    @Test
    public void testCleanNameHandlesNull() {
        assertEquals(null, FieldCleaner.cleanName(null));
    }

    @Test
    public void testCleanNameCapitalizesFirstLetter() {
        assertEquals("John Smith", FieldCleaner.cleanName("john smith"));
    }

    @Test
    public void testCleanNameDoesNotChangeOtherLetters() {
        // Don't try to fix capital inside words - MacDonald and Macdonald are both correct
        assertEquals("Old McDonald", FieldCleaner.cleanName("old mcDonald"));
        assertEquals("Old Mcdonald", FieldCleaner.cleanName("old mcdonald"));
    }

    @Test
    public void testCleanPhoneNumberTrimsSpaces() {
        String testNumber = "  123  ";
        assertEquals("123", FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberRemovesCharacters() {
        String testNumber = "(123) 456-2712 And stuff";
        assertEquals("(123) 456-2712", FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberHandlesNull() {
        String testNumber = null;
        assertEquals(null, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberAddsParens() {
        String testNumber = "1234567890";
        assertEquals("(123) 456-7890", FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberHandlesLongNumbers() {
        // For long (non-American) phone numbers, just leave the numbers and spaces as is.
        String testNumber = "+1 04 12 123-12341234";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberHandlesShortNumbers() {
        // Leave short numbers as is - though hopefully people will remember to enter
        // the area code, that shouldn't be enforced in this function.
        String testNumber = "867-5309";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberLeavesXcharacter() {
        String testNumber = "867-5309 x1234";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberStartsWithPlus() {
        String testNumber = "+64 6-759 9128";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }


    @Test
    public void testCleanPhoneNumberRemovesLettersFromLongNumber() {
        String testNumber = "+1 04 12 123-12341234 asdfasdf x12321";
        assertEquals("+1 04 12 123-12341234 x12321", FieldCleaner.cleanPhoneNumber(testNumber));
    }

}