package org.kumoricon.site.attendee.window;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.order.Payment;
import org.kumoricon.service.validate.PaymentValidator;
import org.kumoricon.site.attendee.FieldFactory;
import org.kumoricon.site.attendee.PaymentHandler;

import java.math.BigDecimal;

public class PaymentWindow extends Window {

    private NativeSelect paymentType = new NativeSelect("Payment Type");
    private TextField amount = FieldFactory.createTextField("Amount");
    private TextField authNumber = FieldFactory.createTextField("Note");
    private TextField paymentTakenBy = FieldFactory.createDisabledTextField("Payment Taken By");
    private TextField paymentTakenAt = FieldFactory.createDisabledTextField("Payment Taken At");
    private TextField paymentLocation = FieldFactory.createDisabledTextField("Payment Location");
    private final Payment thisPayment;

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete");

    private PaymentHandler handler;

    public PaymentWindow(PaymentHandler handler, Payment payment) {
        super("Payment");
        this.handler = handler;
        thisPayment = payment;
        initializeWindow();
        buildContents();
        showItem(payment);
        showDeleteIfPaymentHasId();
    }

    private void showItem(Payment payment) {
        paymentType.setValue(payment.getPaymentType());
        if (payment.getAmount() != null) {
            amount.setValue(payment.getAmount().toString());
        }
        authNumber.setValue(payment.getAuthNumber());
        showValueIfNotNull(paymentTakenAt, payment.getPaymentTakenAt());
        showValueIfNotNull(paymentTakenBy, payment.getPaymentTakenBy());
        showValueIfNotNull(paymentLocation, payment.getPaymentLocation());
    }

    private void showValueIfNotNull(TextField field, Object value) {
        if (value != null) {
            field.setVisible(true);
            field.setValue(value.toString());
        } else {
            field.setVisible(false);
        }
    }

    /**
     * Shows the delete button if the current payment has an ID value (in other words, it has
     * been persisted to the database)
     */
    private void showDeleteIfPaymentHasId() {
        if (thisPayment != null && thisPayment.getId() != null) {
            delete.setVisible(true);
        } else {
            delete.setVisible(false);
        }
    }

    public PaymentWindow(PaymentHandler handler) {
        super("Payment");
        this.handler = handler;
        this.thisPayment = new Payment();
        initializeWindow();
        buildContents();
        showItem(new Payment());
        paymentType.setValue(Payment.PaymentType.CASH);
    }

    private void initializeWindow() {
        setIcon(FontAwesome.DOLLAR);
        setModal(true);
        setClosable(true);
        center();
        setWidth(500, Unit.PIXELS);
    }

    private void buildContents() {

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        paymentType.setNullSelectionAllowed(false);
        for (Payment.PaymentType p : Payment.PaymentType.values()) {
            paymentType.addItem(p);
        }
        paymentType.setValue(Payment.PaymentType.CASH);
        verticalLayout.addComponent(paymentType);
        verticalLayout.addComponent(amount);
        verticalLayout.addComponent(authNumber);
        verticalLayout.addComponent(paymentTakenBy);
        verticalLayout.addComponent(paymentTakenAt);
        verticalLayout.addComponent(paymentLocation);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(save);
        horizontalLayout.addComponent(cancel);
        horizontalLayout.addComponent(delete);
        delete.addStyleName(ValoTheme.BUTTON_DANGER);

        save.addClickListener((Button.ClickListener) clickEvent -> saveClicked());
        cancel.addClickListener((Button.ClickListener) clickEvent -> close());
        delete.addClickListener((Button.ClickListener) clickEvent -> deleteClicked());

        verticalLayout.addComponent(horizontalLayout);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        amount.focus();
        setContent(verticalLayout);

    }

    private void deleteClicked() {
        handler.deletePayment(this, thisPayment);
    }

    private void saveClicked() {
        try {
            thisPayment.setAmount(new BigDecimal(amount.getValue()));
            thisPayment.setPaymentType((Payment.PaymentType)paymentType.getValue());
            thisPayment.setAuthNumber(authNumber.getValue());
            handler.addPayment(this, thisPayment);
        } catch (Exception ex) {
            Notification.show(ex.getMessage());
        }
    }
}
