package org.kumoricon.site.report.export.data;

import com.vaadin.server.Resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * Helper functions for formatting a tab-separated file
 */
public abstract class BaseTSVExport implements Resource {
    protected static String format(Boolean input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    protected static String format(String input) {
        if (input == null || input.trim().equals("")) {
            return "\t";
        } else {
            String output = input.replace("\t", "");
            return output.trim() + "\t";
        }
    }

    protected static String format(LocalDate input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    protected static String format(Date input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    protected static String format(BigDecimal input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    /**
     * Used to tell what type of file the Resource contains.
     * @return Mime type
     */
    @Override
    public String getMIMEType() {
        return "text/csv";
    }

}
