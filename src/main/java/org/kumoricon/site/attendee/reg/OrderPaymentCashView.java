package org.kumoricon.site.attendee.reg;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
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
import java.util.Map;

@ViewScope
@SpringView(name = OrderPaymentCashView.TEMPLATE)
public class OrderPaymentCashView extends BaseView implements View, PaymentView {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    public static final String TEMPLATE = "order/{orderId}/payment/addCash";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    private TextField balance = createDollarField("Balance Due", 1);
    private TextField amount = createDollarField("Amount", 2);
    private TextField change = createTextField("Change", 3);
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");

    protected Integer orderId;
    protected Order order;
    private OrderPresenter orderPresenter;

    @Autowired
    public OrderPaymentCashView(OrderPresenter orderPresenter) {
        this.orderPresenter = orderPresenter;
    }

    @PostConstruct
    public void init() {
        FormLayout leftSide = new FormLayout();
        leftSide.addComponents(balance, amount, change);
        addComponents(leftSide, buildButtons());
        balance.setEnabled(false);
        change.setEnabled(false);

        amount.focus();
        balance.addStyleName("align-right");
        amount.addStyleName("align-right");
        change.addStyleName("align-right");
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
        orderPresenter.showOrder(this, orderId);

    }


    private VerticalLayout buildButtons() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));

        amount.addValueChangeListener(e -> updateChange(e.getValue()));

        save.addClickListener(c -> {
            Payment p = new Payment();
            p.setPaymentType(Payment.PaymentType.CASH);
            BigDecimal amountPaid = new BigDecimal(amount.getValue());
            BigDecimal amountDue = order.getTotalAmount().subtract(order.getTotalPaid());
            if (amountPaid.compareTo(amountDue) > 0) {  // If change was given, only count payment of the amount due
                p.setAmount(amountDue);
            } else {
                p.setAmount(amountPaid);
            }
            p.setOrder(order);
            try {
                orderPresenter.savePayment(this, order, p);
                close();
            } catch (ValidationException e) {
                notifyError(e.getMessage());
            }
        });
        cancel.addClickListener(c -> close());

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);

        buttons.addComponents(save, cancel);
        return buttons;
    }

    private void updateChange(String inputText) {
        try {
            BigDecimal amountPaid = new BigDecimal(inputText);
            BigDecimal changeDue = amountPaid.subtract(order.getBalanceDue());
            if (changeDue.compareTo(BigDecimal.ZERO) >= 0) {
                change.setValue(String.format("$%s", changeDue));
            } else {
                change.setValue("$0.00");
            }
        } catch (NumberFormatException e) {
            // Ignore garbage input
        }
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

        balance.setValue(String.format("$%s", order.getBalanceDue()));
    }
}
