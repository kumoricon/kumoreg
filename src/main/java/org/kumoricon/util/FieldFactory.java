package org.kumoricon.util;

import com.vaadin.ui.*;

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

    public static final TextField createDisabledTextField(String name) {
        // Creates a TextField object that defaults to being disabled
        TextField textField = createTextField(name);
        textField.setEnabled(false);
        return textField;
    }

    public static final DateField createDateField(String name, int tabIndex) {
        DateField dateField = new DateField(name);
        dateField.setTabIndex(tabIndex);
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
