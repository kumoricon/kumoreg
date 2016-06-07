package org.kumoricon.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldCleanerTest {

    @Test
    public void testCleanPhoneNumberTrimsSpaces() throws Exception {
        String testNumber = "  123  ";
        assertEquals("123", FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberRemovesCharacters() throws Exception {
        String testNumber = "(123) 456-2712 And stuff";
        assertEquals("123-456-2712", FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberHandlesNull() throws Exception {
        String testNumber = null;
        assertEquals(null, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberAddsDashes() throws Exception {
        String testNumber = "1234567890";
        assertEquals("123-456-7890", FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberHandlesLongNumbers() throws Exception {
        // For long (non-American) phone numbers, just leave the numbers and spaces as is.
        String testNumber = "+1 04 12 123-12341234";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberHandlesShortNumbers() throws Exception {
        // Leave short numbers as is - though hopefully people will remember to enter
        // the area code, that shouldn't be enforced in this function.
        String testNumber = "867-5309";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberLeavesXcharacter() throws Exception {
        String testNumber = "867-5309 x1234";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }

    @Test
    public void testCleanPhoneNumberStartsWithPlus() throws Exception {
        String testNumber = "+64 6-759 9128";
        assertEquals(testNumber, FieldCleaner.cleanPhoneNumber(testNumber));
    }


    @Test
    public void testCleanPhoneNumberRemovesLettersFromLongNumber() throws Exception {
        String testNumber = "+1 04 12 123-12341234 asdfasdf x12321";
        assertEquals("+1 04 12 123-12341234 x12321", FieldCleaner.cleanPhoneNumber(testNumber));
    }

}