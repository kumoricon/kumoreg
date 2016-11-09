package org.kumoricon.site.attendee.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;


public class BadgeWarningWindow extends Window {
    Label lblMessage = new Label("");
    Button btnAbort = new Button("Abort");

    public BadgeWarningWindow(Attendee attendee) {
        super("Warning");

        setIcon(FontAwesome.WARNING);
        setModal(true);
        center();

        String warning = attendee.getFirstName() + " " + attendee.getLastName() + ": " +
                attendee.getBadge().getWarningMessage();

        lblMessage.setValue(warning);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(lblMessage);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(btnAbort);
        btnAbort.focus();
        btnAbort.addClickListener((Button.ClickListener) clickEvent -> this.close());
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);

        btnAbort.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnAbort.addStyleName(ValoTheme.BUTTON_DANGER);
    }
}
