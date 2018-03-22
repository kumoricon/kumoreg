package org.kumoricon.site;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class ButtonField extends CustomField<String> {

    private final TextField textField = new TextField();
    private final Button button = new Button();

    @Override
    protected Component initContent() {
        CssLayout layout = new CssLayout();
        layout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        textField.setWidth(200, Unit.PERCENTAGE);
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);

        layout.addComponents(textField, button);
        return layout;
    }


    @Override
    public void setValue(String newFieldValue) {
        textField.setValue(newFieldValue);
        super.setValue(newFieldValue);
    }

    @Override
    public String getValue() {
        return textField.getValue();
    }

    @Override
    protected void doSetValue(String value) {
        textField.setValue(value);
    }


    public void setButtonCaption(String caption) {
        button.setCaption(caption);
    }

    public void addClickListener(Button.ClickListener listener) {
        button.addClickListener(listener);
    }

    public void setClickShortcut(int clickShortcut) {
        button.setClickShortcut(clickShortcut);
    }

    public void setPlaceholder(String placeholder) {
        textField.setPlaceholder(placeholder);
    }

    @Override
    public void focus() {
        textField.focus();
    }

    public void selectAll() {
        textField.selectAll();
    }
}