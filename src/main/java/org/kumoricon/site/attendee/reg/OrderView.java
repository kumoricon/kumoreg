package org.kumoricon.site.attendee.reg;

import com.vaadin.ui.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.FieldFactory8;
import org.kumoricon.site.attendee.window.ConfirmationWindow;
import org.kumoricon.site.attendee.window.WarningWindow;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;


@ViewScope
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends BaseGridView implements View, AttendeePrintView, PaymentView {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    @Autowired
    private OrderPresenter handler;

    private TextField orderId = FieldFactory8.createDisabledTextField("Order ID");
    private TextField orderTotal = FieldFactory8.createDisabledTextField("Order Total");
    private TextField paymentTotal = FieldFactory8.createDisabledTextField("Payment Total");
    private TextArea notes = FieldFactory8.createTextArea("Notes");
    private Grid<Attendee> attendeeList = new Grid<>();
    private Grid<Payment> paymentList = new Grid<>();
    private Button addAttendee = new Button("Add Attendee");
    private Button orderComplete = new Button("Order Complete");
    private Button cancel = new Button("Cancel");
    private Button addPayment = new Button("Take Payment");
    private Order currentOrder;
    private String orderIdNumber;

    @PostConstruct
    public void init() {
        setColumns(3);
        setRows(6);

        attendeeList.setWidth(700, Unit.PIXELS);
        attendeeList.addStyleName("kumoHandPointer");

        attendeeList.addColumn(Attendee::getFirstName).setCaption("First Name");
        attendeeList.addColumn(Attendee::getLastName).setCaption("Last Name");
        attendeeList.addColumn(attendee -> attendee.getBadge().getName()).setCaption("Badge Type");
        attendeeList.addColumn(Attendee::getPaid).setCaption("Paid");
        attendeeList.addColumn(Attendee::getPaidAmount).setCaption("Amount");
        attendeeList.addItemClickListener(itemClickEvent -> {
            Attendee attendee = itemClickEvent.getItem();
            navigateTo(AttendeeRegDetailView.VIEW_NAME + "/" + orderIdNumber + "/" + attendee.getId());
        });

        addComponent(attendeeList, 0, 0, 0, 3);

        paymentList.setWidth(700, Unit.PIXELS);
        paymentList.setHeightByRows(2);
        paymentList.addColumn(Payment::getPaymentType).setCaption("Payment Type");
        paymentList.addColumn(Payment::getAmount).setCaption("Amount");
        paymentList.addColumn(Payment::getPaymentTakenBy).setCaption("Taken By");
        paymentList.addColumn(Payment::getPaymentTakenAt).setCaption("Time");
        addComponent(paymentList, 0, 4);

        addComponent(addAttendee, 2, 0);
        addComponent(orderTotal, 1, 0);
        addComponent(paymentTotal, 1, 1);

        addComponent(addPayment, 2, 1);
        addComponent(orderComplete, 2, 2);
        addComponent(cancel, 2, 3);

        addComponent(notes, 0, 5, 2, 5);
        notes.setSizeFull();

        addAttendee.addClickListener((Button.ClickListener) clickEvent -> navigateTo(AttendeeRegDetailView.VIEW_NAME + "/" + orderIdNumber + "/" + "new"));
        addPayment.addClickListener((Button.ClickListener) clickEvent -> navigateTo(OrderPaymentView.VIEW_NAME + "/" + orderIdNumber + "/payment"));
        orderComplete.addClickListener((Button.ClickListener) clickEvent -> handler.takeMoney(this));
        cancel.addClickListener((Button.ClickListener) clickEvent -> showConfirmCancelWindow());

        addAttendee.focus();
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            // If no parameters, create a new order and navigate to it
            handler.createNewOrder(this);
        } else {
            orderIdNumber = viewChangeEvent.getParameters();
            handler.showOrder(this, Integer.parseInt(orderIdNumber));
        }
    }


    private void enableFields(boolean enable) {
        addAttendee.setEnabled(enable);
        addAttendee.setVisible(enable);
        notes.setEnabled(enable);
        orderComplete.setEnabled(enable);
        orderComplete.setVisible(enable);
    }

    public Order getOrder() {
        currentOrder.setNotes(notes.getValue());
        return currentOrder;
    }

    public void showOrder(Order order) {
        this.currentOrder = order;
        orderId.setValue(order.getOrderId());
        orderTotal.setValue(order.getTotalAmount().toString());
        if (order.getNotes() != null) {
            notes.setValue(order.getNotes());
        }

        attendeeList.setItems(order.getAttendees());
        paymentTotal.setValue(order.getTotalPaid().toString());

        if (order.getTotalAmount().compareTo(order.getTotalPaid()) == 0 && order.getAttendees().size() > 0) {
            orderComplete.setEnabled(true);
            addAttendee.setEnabled(false);
        } else {
            orderComplete.setEnabled(false);
            addAttendee.setEnabled(true);
        }

        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            addPayment.setEnabled(false);
        } else {
            addPayment.setEnabled(true);
        }

        if (order.getPaid()) {
            attendeeList.setEnabled(false);
            orderComplete.setEnabled(false);
            addAttendee.setEnabled(false);
            addPayment.setEnabled(false);
            paymentList.setEnabled(false);
            notes.setEnabled(false);
        }

        paymentList.setItems(order.getPayments());

        if (!currentUserHasRight("manage_orders")) {
            setEnabled(!order.getPaid());   // Disable editing if the order has been paid
        }
    }

    public void setHandler(OrderPresenter presenter) {
        this.handler = presenter;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showConfirmCancelWindow() {
        ConfirmationWindow window = new ConfirmationWindow(this, "Are you sure you want to cancel?");
        showWindow(window);
    }

    public void confirmCancelOrder() {
        handler.cancelOrder(this);
    }
}
