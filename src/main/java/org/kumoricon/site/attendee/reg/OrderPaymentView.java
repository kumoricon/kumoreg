package org.kumoricon.site.attendee.reg;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.PaymentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@ViewScope
@SpringView(name = OrderPaymentView.TEMPLATE)
public class OrderPaymentView extends BaseView implements View, PaymentHandler, PaymentView {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    public static final String TEMPLATE = "order/{orderId}/payment";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    private TextField orderTotal = new TextField("Total Due");
    private TextField amountPaid = new TextField("Amount paid");
    private TextField remaining = new TextField("Remaining");
    private Button btnTakeCash = new Button("Take cash");
    private Button btnTakeCredit = new Button("Take Credit Card");
    private Button btnTakeCheck = new Button("Take check/money order");
    private Button btnClose = new Button("Close");
    private Grid<Payment> paymentGrid = new Grid<>();

    protected Integer orderId;
    protected Order order;
    private OrderPresenter orderPresenter;

    @Autowired
    public OrderPaymentView(OrderPresenter orderPresenter) {
        this.orderPresenter = orderPresenter;
    }

    @PostConstruct
    public void init() {
        FormLayout leftSide = new FormLayout();

        paymentGrid.addColumn(Payment::getPaymentType).setCaption("Type");
        paymentGrid.addColumn(Payment::getAmount).setCaption("Amount");
        paymentGrid.addColumn(Payment::getPaymentTakenBy).setCaption("Taken By");
        paymentGrid.addColumn(Payment::getPaymentTakenAt).setCaption("Timestamp");
        paymentGrid.addStyleName("kumoHandPointer");
        paymentGrid.setSelectionMode(Grid.SelectionMode.NONE);
        paymentGrid.addItemClickListener(a -> {
            navigateTo(OrderPaymentRecordView.VIEW_NAME + "/" + orderId + "/payment/" + a.getItem().getId());
        });
        paymentGrid.setWidth("700px");
        leftSide.addComponents(orderTotal, amountPaid, remaining, paymentGrid);
        leftSide.setWidth("52%");
        leftSide.setSpacing(false);
        leftSide.setMargin(false);
        orderTotal.setEnabled(false);
        amountPaid.setEnabled(false);
        remaining.setEnabled(false);
        orderTotal.addStyleName("align-right");
        amountPaid.addStyleName("align-right");
        remaining.addStyleName("align-right");

        addComponent(leftSide);
        addComponent(buildButtons());
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

        btnTakeCash.addClickListener(c -> navigateTo(OrderPaymentCashView.VIEW_NAME + "/" + this.orderId + "/payment/addCash"));
        btnTakeCredit.addClickListener(c -> navigateTo(OrderPaymentCreditView.VIEW_NAME + "/" + this.orderId + "/payment/addCredit"));
        btnTakeCheck.addClickListener(c -> navigateTo(OrderPaymentCheckView.VIEW_NAME + "/" + this.orderId + "/payment/addCheck"));
        orderPresenter.showPayment(this, orderId);
    }


    protected VerticalLayout buildButtons() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));

        btnClose.addClickListener((Button.ClickListener) clickEvent -> close());

        buttons.addComponents(btnTakeCash, btnTakeCredit, btnTakeCheck, btnClose);
        return buttons;
    }


    @Override
    public void close() {
        navigateTo(OrderView.VIEW_NAME + "/" + orderId);
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    public void showOrder(Order order) {
        this.order = order;
        orderTotal.setValue(String.format("$%s", order.getTotalAmount()));
        amountPaid.setValue(String.format("$%s", order.getTotalPaid()));
        remaining.setValue(String.format("$%s", order.getTotalAmount().subtract(order.getTotalPaid())));
        paymentGrid.setItems(order.getPayments());

        boolean paidInFull = order.getTotalAmount().equals(order.getTotalPaid());
        btnTakeCash.setEnabled(!paidInFull);
        btnTakeCredit.setEnabled(!paidInFull);
        btnTakeCheck.setEnabled(!paidInFull);
    }

    public void addPayment(Payment payment) {
        payment.setOrder(order);
        try {
            orderPresenter.savePayment(this, order, payment);
            close();
        } catch (ValidationException e) {
            notifyError(e.getMessage());
        }
    }

    @Override
    public void deletePayment(Payment payment) {
        orderPresenter.deletePayment(this, orderId, payment);
    }

    Order getOrder() {
        return this.order;
    }

}
