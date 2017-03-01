package org.kumoricon.service.validate;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatorTest {
    @Test
    public void isNullOrEmptyTrue() throws Exception {
        assertTrue(Validator.isNullOrEmpty(""));
        assertTrue(Validator.isNullOrEmpty(" "));
        assertTrue(Validator.isNullOrEmpty("      "));
        assertTrue(Validator.isNullOrEmpty(" \n "));
        assertTrue(Validator.isNullOrEmpty(null));
    }

    @Test
    public void isNullOrEmptyFalse() throws Exception {
        assertFalse(Validator.isNullOrEmpty("a"));
        assertFalse(Validator.isNullOrEmpty("The quick brown fox jumped over the lazy dog"));
    }

}