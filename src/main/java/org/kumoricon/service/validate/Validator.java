package org.kumoricon.service.validate;

/**
 * Created by jason on 1/27/17.
 */
public class Validator {
    static Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
