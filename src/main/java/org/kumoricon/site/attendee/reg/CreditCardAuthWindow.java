package org.kumoricon.site.attendee.reg;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.site.attendee.FieldFactory;


public class CreditCardAuthWindow extends Window {

    TextField authNumber = FieldFactory.createTextField("Credit Card Authorization Number (6-7 characters)");
    Button save = new Button("Save");

    private OrderPresenter handler;
    private OrderView parentView;

    public CreditCardAuthWindow(OrderView parentView, OrderPresenter orderPresenter) {
        super("Authorization Number");
        this.handler = orderPresenter;
        this.parentView = parentView;
        setIcon(FontAwesome.CREDIT_CARD);
        setModal(true);
        center();

        setWidth(600, Unit.PIXELS);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(true);
        formLayout.setSpacing(true);

        formLayout.addComponent(authNumber);
        formLayout.addComponent(save);

        authNumber.setMaxLength(7);
        authNumber.focus();

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener((Button.ClickListener) clickEvent -> {
            if (authNumber.getValue().trim().length() >= 6) {
                handler.saveAuthNumberClicked(this.parentView, authNumber.getValue());
                close();
            } else {
                authNumber.selectAll();
            }
        });

        setContent(formLayout);

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public OrderPresenter getHandler() { return handler; }
    public void setHandler(OrderPresenter handler) { this.handler = handler; }

}
