package org.kumoricon.site.attendee.reg;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.attendee.PaymentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@ViewScope
@SpringView(name = OrderPaymentView.TEMPLATE)
public class OrderPaymentView extends BaseGridView implements View, PaymentHandler, PaymentView {
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
        setColumns(5);
        setRows(5);

        setColumnExpandRatio(0, 10);
        setColumnExpandRatio(1, 2);
        setColumnExpandRatio(2, 1);
        setColumnExpandRatio(3, 1);
        setColumnExpandRatio(4, 10);

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
        addComponent(paymentGrid, 1, 0, 1, 3);

        addComponent(orderTotal, 2, 0);
        addComponent(amountPaid, 2, 1);
        addComponent(remaining, 2, 2);

        orderTotal.setEnabled(false);
        amountPaid.setEnabled(false);
        remaining.setEnabled(false);
        orderTotal.addStyleName("align-right");
        amountPaid.addStyleName("align-right");
        remaining.addStyleName("align-right");

        addComponent(btnTakeCash, 3, 0);
        addComponent(btnTakeCredit, 3, 1);
        addComponent(btnTakeCheck, 3, 2);
        addComponent(btnClose, 3, 3);
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
        btnClose.addClickListener((Button.ClickListener) clickEvent -> close());
        orderPresenter.showPayment(this, orderId);
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
