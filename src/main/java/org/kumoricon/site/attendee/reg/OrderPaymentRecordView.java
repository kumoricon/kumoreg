package org.kumoricon.site.attendee.reg;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.BaseView;
import static org.kumoricon.site.attendee.FieldFactory8.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ViewScope
@SpringView(name = OrderPaymentRecordView.TEMPLATE)
public class OrderPaymentRecordView extends BaseView implements View, PaymentView {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    public static final String TEMPLATE = "order/{orderId}/payment/{paymentId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    private NativeSelect<Payment.PaymentType> paymentType = new NativeSelect<>("Payment Type");
    private TextField amount = createTextField("Amount");
    private TextField authNumber = createTextField("Note");
    private TextField paymentTakenBy = createDisabledTextField("Payment Taken By");
    private TextField paymentTakenAt = createDisabledTextField("Payment Taken At");
    private TextField paymentLocation = createDisabledTextField("Payment Location");
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete");

    protected Integer orderId;
    private Integer paymentId;
    protected Payment payment;
    protected Order order;
    private OrderPresenter orderPresenter;

    @Autowired
    public OrderPaymentRecordView(OrderPresenter orderPresenter) {
        this.orderPresenter = orderPresenter;
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        try {
            this.orderId = Integer.parseInt((map.get("orderId")));
        } catch (NumberFormatException ex) {
            notifyError("Bad order id: must be an integer");
            navigateTo("/");
        }
        try {
            this.paymentId = Integer.parseInt((map.get("paymentId")));
        } catch (NumberFormatException ex) {
            notifyError("Bad payment id: must be an integer");
            navigateTo("/");
        }

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        paymentType.setEmptySelectionAllowed(false);
        List<Payment.PaymentType> visibleTypes = new ArrayList<>();
        for (Payment.PaymentType p : Payment.PaymentType.values()) {
            // Don't show the PREREG payment type unless the record is already set to PREREG.
            // IE - the order was imported with prereg payment type)
            if (p != Payment.PaymentType.PREREG) {
                visibleTypes.add(p);
            }
        }
        paymentType.setItems(visibleTypes);
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
        addComponent(verticalLayout);

        orderPresenter.showOrder(this, orderId);
    }


    @Override
    public void close() {
        navigateTo(OrderView.VIEW_NAME + "/" + orderId + "/payment");
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    @Override
    public void showOrder(Order order) {
        this.order = order;
        for (Payment p : order.getPayments()) {
            if (p.getId().equals(paymentId)) {
                this.payment = p;
            }
        }

        // Only make PREREG an option if the order has that payment type set already
        if (Payment.PaymentType.PREREG.equals(payment.getPaymentType())) {
            paymentType.setItems(Payment.PaymentType.values());
        }
        paymentType.setValue(payment.getPaymentType());
        if (payment.getAmount() != null) {
            amount.setValue(payment.getAmount().toString());
        }
        authNumber.setValue(payment.getAuthNumber());
        showValueIfNotNull(paymentTakenAt, payment.getPaymentTakenAt());
        showValueIfNotNull(paymentTakenBy, payment.getPaymentTakenBy());
        showValueIfNotNull(paymentLocation, payment.getPaymentLocation());
    }

    private void deleteClicked() {
        orderPresenter.deletePayment(this, orderId, payment);
        close();
    }

    private void saveClicked() {
        try {
            payment.setAmount(new BigDecimal(amount.getValue()));
            payment.setPaymentType(paymentType.getValue());
            payment.setAuthNumber(authNumber.getValue());
            try {
                orderPresenter.savePayment(this, order, payment);
                close();
            } catch (ValidationException e) {
                notifyError(e.getMessage());
            }
        } catch (Exception ex) {
            Notification.show(ex.getMessage());
        }
    }


    private void showValueIfNotNull(TextField field, Object value) {
        if (value != null) {
            field.setVisible(true);
            field.setValue(value.toString());
        } else {
            field.setVisible(false);
        }
    }
}
