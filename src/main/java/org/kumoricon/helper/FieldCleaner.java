package org.kumoricon.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldCleaner {
    private static final Logger log = LoggerFactory.getLogger(FieldCleaner.class);

    /**
     * Removes characters from the given string except for [0-9 -] and formats it nicely. If
     * it's a 10 digit number, returns the format 123-456-7890. Otherwise, just returns
     * whatever digits, dashes and spaces exist.
     * @param phoneNumber String
     * @return String
     */
    public static String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) { return null; }
        String output;
        String p = phoneNumber.replaceAll("[^\\+0-9]", "");
        if (p.matches("\\d{10}")) {
            output = p.substring(0, 3) + "-" + p.substring(3, 6) + "-" + p.substring(6, 10);
        } else {
            output = phoneNumber.replaceAll("[^\\+0-9x -]", "").trim();
            output = output.replaceAll("\\s\\s+", " ");                 // Multiple spaces to single space
        }
        if (!phoneNumber.equals(output)) {
            log.info("While reformatting phone numbers, changed \"{}\" to \"{}\"", phoneNumber, output);
        }
        return output;
    }
}
