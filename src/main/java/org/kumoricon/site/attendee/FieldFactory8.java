package org.kumoricon.site.attendee;

import com.vaadin.data.Result;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.service.FieldCleaner;

import java.time.LocalDate;

/**
 * Helpers for generating different Vaadin form fields
 */
public class FieldFactory8 {
    /**
     * Returns a TextField with the given name that will show an empty string instead of "null" for null values
     * @param name Field Name
     * @return TextField
     */
    public static TextField createTextField(String name) {
        TextField textField = new TextField(name);
        return textField;
    }

    /**
     * Returns a TextField with the given name that will show an empty string instead of "null" for null values
     * @param name Field Name
     * @param tabIndex Tab Index
     * @return TextField
     */
    public static TextField createTextField(String name, int tabIndex) {
        TextField textField = createTextField(name);
        textField.setTabIndex(tabIndex);
        return textField;
    }

    public static com.vaadin.ui.PasswordField createPasswordField(String name, int tabIndex) {
        com.vaadin.ui.PasswordField passwordField = new com.vaadin.ui.PasswordField(name);
        passwordField.setTabIndex(tabIndex);
        return passwordField;
    }

    /**
     * Returns a TextField that will only accept digits (no . or -). Shows empty for null values.
     * @param name Field Name
     * @param tabIndex Tab Index
     * @return TextField
     */
    public static TextField createNumberField(String name, int tabIndex) {
        TextField textField = createTextField(name);
//        textField.addValidator(new RegexpValidator("[0-9]+", "This is not a number"));
        textField.setTabIndex(tabIndex);
        return textField;
    }

    /**
     * Returns a field that accepts digits 0-9 and optionally beings with "-". Shows empty for null values.
     * @param name Field name
     * @param tabIndex Tab index
     * @return TextField
     */
    public static TextField createNegativeNumberField(String name, int tabIndex) {
        TextField textField = createTextField(name);
//        textField.addValidator(new RegexpValidator("-?[0-9]+", "This is not a number"));
        textField.setTabIndex(tabIndex);
        return textField;
    }

    /**
     * Returns a field that accepts digits 0-9 and "." Shows empty for null values.
     * @param name Field name
     * @param tabIndex Tab index
     * @return TextField
     */
    public static TextField createDollarField(String name, int tabIndex) {
        TextField textField = createDollarField(name);
        textField.setTabIndex(tabIndex);
        return textField;
    }

    public static TextField createDollarField(String name) {
        TextField textField = createTextField(name);
//        textField.addValidator(new RegexpValidator("\\$?[0-9]+?\\.?[0-9]?[0-9]?", "This is not a number"));
        return textField;
    }



    /**
     * Returns a field that will automatically capitalize names on blur (tab-out). Shows empty for null values.
     * See {@link FieldCleaner#cleanName FieldCleaner} for capitalization rules
     * @param name Field Name
     * @return TextField
     */
    public static TextField createNameField(String name) {
        TextField textField = createTextField(name);
        textField.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                String input = event.getValue();
                textField.setValue(FieldCleaner.cleanName(input));
            }
        });
        return textField;
    }

    /**
     * Returns a field that will automatically capitalize names on blur (tab-out)
     * See {@link FieldCleaner#cleanName(String) FieldCleaner} for capitalization rules
     * @param name Field Name
     * @param tabIndex Tab Index
     * @return TextField
     */
    public static TextField createNameField(String name, int tabIndex) {
        TextField textField = createNameField(name);
        textField.setTabIndex(tabIndex);
        return textField;
    }

    /**
     * Returns a TextField with validation rules to only allow phone numbers. Shows empty for null values.
     * Must contain 10-25 characters; Only allows digits, -, space, (, ), + or x; on blur, attempts to reformat the
     * phone number to (xxx) xxx-xxxx format (see {@link FieldCleaner#cleanPhoneNumber(String) FieldCleaner} for rules
     * @param name Field Name
     * @param tabIndex Tab Index
     * @return TextField
     */
    public static TextField createPhoneNumberField(String name, int tabIndex) {
        TextField textField = createTextField(name);
//        textField.addValidator(new RegexpValidator("[0-9 \\+\\-\\(\\)x]{10,25}",
//                "Must contain 10-25 characters, only numbers, dash, space, parenthesis, + or x"));
        textField.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                String input = event.getValue();
                    textField.setValue(FieldCleaner.cleanPhoneNumber(input));
            }
        });
        textField.setTabIndex(tabIndex);
        return textField;
    }

    /**
     * Returns TextField that only allows digits and optionally a single period in the
     * middle of the number. Shows empty for null values.
     * @param name Field Name
     * @return TextField
     */
    public static TextField createDecimalField(String name) {
        TextField textField = createTextField(name);
//        textField.addValidator(new RegexpValidator("[0-9]*\\.?[0-9]+", "This is not a number"));
        return textField;
    }


    /**
     * Returns TextField that only allows digits and optionally a single period in the
     * middle of the number. Shows empty for null values.
     * @param name Field Name
     * @param tabIndex Tab Index
     * @return TextField
     */
    public static TextField createDecimalField(String name, int tabIndex) {
        TextField textField = createDecimalField(name);
        textField.setTabIndex(tabIndex);
        return textField;
    }


    /**
     * Returns a TextField that is disabled by default. Shows empty for null values.
     * @param name Field Name
     * @return TextField
     */
    public static TextField createDisabledTextField(String name) {
        TextField textField = createTextField(name);
        textField.setEnabled(false);
        return textField;
    }

    /**
     * Returns a DateField that will attempt to parse dates in MMDDYYYY and MM-DD-YYYY formats as well
     * as the built-in MM/DD/YYYY format
     * @param name Name of field
     * @param tabIndex Tab Index
     * @return DateField
     */
    public static DateField createDateField(String name, int tabIndex) {
        DateField dateField = new DateField(name) {
            @Override
            protected Result<LocalDate> handleUnparsableDateString(String dateString) {
                Integer year = null;
                Integer month = null;
                Integer day = null;
                // Try to parse date without delimiters -- MMDDYYYY format (must have leading zeros)
                String dateDigits = dateString.trim();
                if (dateDigits.matches("^\\d{8}$")) {
                    year = Integer.parseInt(dateDigits.substring(4, 8));
                    month = Integer.parseInt(dateDigits.substring(0, 2)) -1;
                    day = Integer.parseInt(dateDigits.substring(2, 4));
                } else {
                    // Try to parse date with - instead of /
                    String fields[] = dateString.split("-");
                    if (fields.length >= 3) {
                        try {
                            year = Integer.parseInt(fields[2]);
                            month = Integer.parseInt(fields[0]) - 1;
                            day = Integer.parseInt(fields[1]);
                        } catch (NumberFormatException e) {
                            year = null;
                            month = null;
                            day = null;
                        }
                    }
                }

                if (year != null && month != null && day != null && month >= 0 && month <= 11 && day >= 1 && day <= 31) {
                    try {
                        LocalDate l = LocalDate.of(year, month, day);
                        return Result.ok(l);
                    } catch (NumberFormatException e) {
                        // Ignore, throw ConversionException below
                    }
                }

                // Bad date
                return Result.error("Your date must be in MMDDYYYY, MM/DD/YYYY, or MM-DD-YYYY format");
            }
        };
        dateField.setDateFormat("MM/dd/yyyy");

        dateField.setTabIndex(tabIndex);
        dateField.setPlaceholder("MMDDYYYY");
        return dateField;
    }

    public static CheckBox createCheckBox(String name, int tabIndex) {
        CheckBox checkBox = new CheckBox(name);
        checkBox.setTabIndex(tabIndex);
        return checkBox;
    }

    public static NativeSelect<Badge> createBadgeSelect(String name, int tabIndex) {
        NativeSelect<Badge> nativeSelect = new NativeSelect<>(name);
        nativeSelect.setTabIndex(tabIndex);
        return nativeSelect;
    }

    /**
     * Returns a TextArea that shows as empty for null values
     * @param name Field Name
     * @return TextArea
     */
    public static com.vaadin.ui.TextArea createTextArea(String name) {
        // Creates TextArea object and sets it to show an empty string for null values
        com.vaadin.ui.TextArea textArea = new com.vaadin.ui.TextArea(name);
        return textArea;
    }
}
