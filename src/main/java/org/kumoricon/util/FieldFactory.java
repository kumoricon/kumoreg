package org.kumoricon.util;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class FieldFactory {
    public static final TextField createTextField(String name) {
        // Creates a TextField object and sets it to show an empty string for null values
        TextField textField = new TextField(name);
        textField.setNullRepresentation("");
        return textField;
    }

    public static final TextField createDisabledTextField(String name) {
        // Creates a TextField object that defaults to being disabled
        TextField textField = createTextField(name);
        textField.setEnabled(false);
        return textField;
    }

    public static final TextArea createTextArea(String name) {
        // Creates TextArea object and sets it to show an empty string for null values
        TextArea textArea = new TextArea(name);
        textArea.setNullRepresentation("");
        return textArea;
    }

    public static final TextArea createDisabledTextArea(String name) {
        TextArea textArea = createTextArea(name);
        textArea.setEnabled(false);
        return textArea;
    }
}
