package org.kumoricon.site.attendee.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;


public class WarningWindow extends Window {
    Label lblMessage = new Label("");
    Button btnOk = new Button("Ok");

    public WarningWindow(String message) {
        super("Warning");

        setIcon(FontAwesome.WARNING);
        setModal(true);
        center();

        lblMessage.setValue(message);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(lblMessage);

        verticalLayout.addComponent(btnOk);
        btnOk.focus();
        btnOk.addClickListener((Button.ClickListener) clickEvent -> this.close());
        setContent(verticalLayout);

        btnOk.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnOk.addStyleName(ValoTheme.BUTTON_DANGER);
    }
}
