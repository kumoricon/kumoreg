package org.kumoricon.service.validate;

/**
 * Base class for implementing form validators
 */
public class Validator {
    static Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
