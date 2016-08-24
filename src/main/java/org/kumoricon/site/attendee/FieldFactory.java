package org.kumoricon.site.attendee;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.*;
import org.kumoricon.service.FieldCleaner;

import java.util.Date;
import java.util.GregorianCalendar;

public class FieldFactory {
    public static final TextField createTextField(String name) {
        // Creates a TextField object and sets it to show an empty string for null values
        TextField textField = new TextField(name);
        textField.setNullRepresentation("");
        return textField;
    }

    public static final TextField createTextField(String name, int tabIndex) {
        TextField textField = createTextField(name);
        textField.setTabIndex(tabIndex);
        return textField;
    }

    public static final PasswordField createPasswordField(String name, int tabIndex) {
        PasswordField passwordField = new PasswordField(name);
        passwordField.setTabIndex(tabIndex);
        return passwordField;
    }

    public static final TextField createNumberField(String name, int tabIndex) {
        TextField textField = createTextField(name);
        textField.addValidator(new RegexpValidator("[0-9]+", "This is not a number"));
        textField.setTabIndex(tabIndex);
        return textField;
    }

    /**
     * Creates a field that accepts digits 0-9 and optionally beings with "-"
     * @param name Field name
     * @param tabIndex Tab index
     * @return TextField
     */
    public static final TextField createNegativeNumberField(String name, int tabIndex) {
        TextField textField = createTextField(name);
        textField.addValidator(new RegexpValidator("-?[0-9]+", "This is not a number"));
        textField.setTabIndex(tabIndex);
        return textField;
    }


    public static final TextField createNameField(String name) {
        TextField textField = createTextField(name);
        textField.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                String input = event.getProperty().getValue().toString();
                event.getProperty().setValue(FieldCleaner.cleanName(input));
            }
        });
        return textField;
    }

    public static final TextField createNameField(String name, int tabIndex) {
        TextField textField = createNameField(name);
        textField.setTabIndex(tabIndex);
        return textField;
    }

    public static final TextField createPhoneNumberField(String name, int tabIndex) {
        TextField textField = createTextField(name);
        textField.addValidator(new RegexpValidator("[0-9 \\+\\-\\(\\)x]{10,25}",
                "Must contain 10-25 characters, only numbers, dash, space, parenthesis, + or x"));
        textField.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                String input = event.getProperty().getValue().toString();
                    event.getProperty().setValue(FieldCleaner.cleanPhoneNumber(input));
            }
        });
        textField.setTabIndex(tabIndex);
        return textField;
    }

    public static final TextField createDecimalField(String name, int tabIndex) {
        TextField textField = createTextField(name);
        textField.addValidator(new RegexpValidator("[0-9]*\\.?[0-9]+", "This is not a number"));
        textField.setTabIndex(tabIndex);
        return textField;
    }


    public static final TextField createDisabledTextField(String name) {
        // Creates a TextField object that defaults to being disabled
        TextField textField = createTextField(name);
        textField.setEnabled(false);
        return textField;
    }

    public static final DateField createDateField(String name, int tabIndex) {
        PopupDateField dateField = new PopupDateField(name){
            @Override
            protected Date handleUnparsableDateString(String dateString) throws Converter.ConversionException {
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
                        GregorianCalendar c = new GregorianCalendar(year, month, day);
                        return c.getTime();
                    } catch (NumberFormatException e) {
                        // Ignore, throw ConversionException below
                    }
                }

                // Bad date
                throw new Converter
                        .ConversionException("Your date must be in MMDDYYYY, MM/DD/YYYY, or MM-DD-YYYY format");
            }
        };

        dateField.setTabIndex(tabIndex);
        dateField.setInputPrompt("MMDDYYYY");
        return dateField;
    }

    public static final CheckBox createCheckBox(String name, int tabIndex) {
        CheckBox checkBox = new CheckBox(name);
        checkBox.setTabIndex(tabIndex);
        return checkBox;
    }

    public static final NativeSelect createNativeSelect(String name, int tabIndex) {
        NativeSelect nativeSelect = new NativeSelect(name);
        nativeSelect.setTabIndex(tabIndex);
        return nativeSelect;
    }

    public static final TextArea createTextArea(String name) {
        // Creates TextArea object and sets it to show an empty string for null values
        TextArea textArea = new TextArea(name);
        textArea.setNullRepresentation("");
        return textArea;
    }

    public static final TextArea createTextArea(String name, int tabIndex) {
        TextArea textArea = createTextArea(name);
        textArea.setTabIndex(tabIndex);
        return textArea;
    }

    public static final TextArea createDisabledTextArea(String name) {
        TextArea textArea = createTextArea(name);
        textArea.setEnabled(false);
        return textArea;
    }
}
