package org.kumoricon.site.attendee.reg;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.FieldFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

@ViewScope
@SpringView(name = OrderPaymentCreditView.TEMPLATE)
public class OrderPaymentCreditView extends BaseView implements View, PaymentView {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    public static final String TEMPLATE = "order/{orderId}/payment/addCredit";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);


    com.vaadin.v7.ui.TextField balance = FieldFactory.createDollarField("Balance Due", 1);
    com.vaadin.v7.ui.TextField amount = FieldFactory.createDollarField("Amount", 3);
    TextField authNumber = FieldFactory.createTextField("Credit Card Authorization Number (6-7 characters)");
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");

    protected Integer orderId;
    protected Order order;
    private OrderPresenter orderPresenter;

    @Autowired
    public OrderPaymentCreditView(OrderPresenter orderPresenter) {
        this.orderPresenter = orderPresenter;
    }

    @PostConstruct
    public void init() {
        FormLayout leftSide = new FormLayout();
        leftSide.addComponents(balance, amount, authNumber);
        addComponents(leftSide, buildButtons());
        balance.setEnabled(false);

        amount.focus();
        balance.addStyleName("align-right");
        amount.addStyleName("align-right");
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


    protected VerticalLayout buildButtons() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));

        save.addClickListener(c -> {
            Payment p = new Payment();
            p.setPaymentType(Payment.PaymentType.CREDIT);
            BigDecimal amountPaid = new BigDecimal(amount.getValue());
            BigDecimal amountDue = order.getTotalAmount().subtract(order.getTotalPaid());
            if (amountPaid.compareTo(amountDue) > 0) {  // If change was given, only count payment of the amount due
                notifyError("Amount paid can not be more than the balance due");
                amount.selectAll();
                return;
            } else {
                p.setAmount(amountPaid);
            }
            p.setOrder(order);
            p.setAuthNumber(authNumber.getValue());
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
