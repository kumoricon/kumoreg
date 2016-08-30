package org.kumoricon.site.attendee.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.attendee.reg.OrderView;


public class ConfirmationWindow extends Window {

    private Label message = new Label();
    private Button confirm = new Button("Yes");
    private Button close = new Button("No");

    public ConfirmationWindow(OrderView view, String messageText) {
        this(view);
        message.setCaption(messageText);
    }

    public ConfirmationWindow(OrderView view) {
        super(" Warning");

        setIcon(FontAwesome.WARNING);
        setModal(true);
        setClosable(true);
        center();
        setWidth(500, Unit.PIXELS);

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(message);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(confirm);
        horizontalLayout.addComponent(close);

        confirm.addClickListener((Button.ClickListener) clickEvent -> {
            close();
            view.confirmCancelOrder();
        });
        close.addClickListener((Button.ClickListener) clickEvent -> close());

        verticalLayout.addComponent(horizontalLayout);

        setContent(verticalLayout);

        confirm.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        confirm.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }
}
