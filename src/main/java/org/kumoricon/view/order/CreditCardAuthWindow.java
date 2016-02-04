package org.kumoricon.view.order;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import org.kumoricon.presenter.order.OrderPresenter;
import org.kumoricon.util.FieldFactory;

public class CreditCardAuthWindow extends Window {

    TextField authNumber = FieldFactory.createTextField("Credit Card Authorization Number (6-7 characters)");
    Button save = new Button("Save");

    private OrderPresenter handler;

    public CreditCardAuthWindow(OrderPresenter orderPresenter) {
        super("Authorization Number");
        this.handler = orderPresenter;
        setIcon(FontAwesome.CREDIT_CARD);
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
                handler.saveAuthNumberClicked(authNumber.getValue());
                close();
            } else {
                authNumber.selectAll();
            }
        });

        setContent(formLayout);
    }

    public OrderPresenter getHandler() { return handler; }
    public void setHandler(OrderPresenter handler) { this.handler = handler; }

}
