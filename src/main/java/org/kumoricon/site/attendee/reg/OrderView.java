package org.kumoricon.site.attendee.reg;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.Payment;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.FieldFactory;
import org.kumoricon.site.attendee.PaymentHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.ConfirmationWindow;
import org.kumoricon.site.attendee.window.PaymentWindow;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.site.attendee.window.WarningWindow;
import org.kumoricon.site.fieldconverter.BadgeToStringConverter;
import org.kumoricon.site.fieldconverter.UserToStringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends BaseView implements View, AttendeePrintView, PaymentHandler {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    @Autowired
    private OrderPresenter handler;

    private TextField orderId = FieldFactory.createDisabledTextField("Order ID");
    private TextField orderTotal = FieldFactory.createDisabledTextField("Order Total");
    private TextField paymentTotal = FieldFactory.createDisabledTextField("Payment Total");
    private TextArea notes = FieldFactory.createTextArea("Notes");
    private Table attendeeList = new Table();
    private Table paymentList = new Table();
    private Button addAttendee = new Button("Add Attendee");
    private Button orderComplete = new Button("Order Complete");
    private Button cancel = new Button("Cancel");
    private BeanItemContainer<Attendee> attendeeBeanList;
    private BeanItemContainer<Payment> paymentBeanList;
    private Button addPayment = new Button("Take Payment");
    private Order currentOrder;

    @PostConstruct
    public void init() {
        FormLayout orderInfo = new FormLayout();
        orderId.setWidth(400, Unit.PIXELS);
        orderInfo.addComponent(orderId);
        addComponent(orderInfo);

        attendeeBeanList = new BeanItemContainer<>(Attendee.class, new ArrayList<>());
        attendeeList.setContainerDataSource(attendeeBeanList);
        attendeeList.setPageLength(5);
        attendeeList.setWidth(600, Unit.PIXELS);
        attendeeList.addStyleName("kumoHandPointer");

        paymentBeanList = new BeanItemContainer<>(Payment.class, new ArrayList<>());
        paymentList.setContainerDataSource(paymentBeanList);
        paymentList.setWidth(500, Unit.PIXELS);
        paymentList.addStyleName("kumoHandPointer");
        paymentList.setPageLength(3);
        paymentList.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent -> {
                    BeanItem b = (BeanItem)itemClickEvent.getItem();
                    showPaymentWindow((Payment)b.getBean());
                });

        orderInfo.addComponent(attendeeList);
        attendeeList.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent ->
                handler.selectAttendee(this, (Attendee)itemClickEvent.getItemId()));

        orderInfo.addComponent(addAttendee);
        orderInfo.addComponent(orderTotal);
        orderInfo.addComponent(paymentTotal);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.addComponent(addPayment);
        buttonLayout.addComponent(orderComplete);
        buttonLayout.addComponent(cancel);
        orderInfo.addComponent(buttonLayout);

        orderInfo.addComponent(paymentList);

        orderInfo.addComponent(notes);
        notes.setSizeFull();

        addAttendee.addClickListener((Button.ClickListener) clickEvent -> handler.addNewAttendee(this));
        addPayment.addClickListener((Button.ClickListener) clickEvent -> showPaymentWindow());
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
            String searchString = viewChangeEvent.getParameters();
            handler.showOrder(this, Integer.parseInt(searchString));
        }
    }

    private void showPaymentWindow(Payment payment) {
        PaymentWindow window = new PaymentWindow(this, payment);
        showWindow(window);
    }

    private void showPaymentWindow() {
        PaymentWindow window = new PaymentWindow(this);
        showWindow(window);
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

    public void afterSuccessfulFetch(Order order) {
        Object[] sortBy = {attendeeList.getSortContainerPropertyId()};
        boolean[] sortOrder = {attendeeList.isSortAscending()};

        this.currentOrder = order;
        orderId.setValue(order.getOrderId());
        orderTotal.setValue(order.getTotalAmount().toString());
        notes.setValue(order.getNotes());

        attendeeList.setContainerDataSource(new BeanItemContainer<>(Attendee.class, order.getAttendeeList()));
        attendeeList.setVisibleColumns("firstName", "lastName", "badge", "paid", "paidAmount");
        attendeeList.setColumnHeaders("First Name", "Last Name", "Badge Type", "Paid", "Cost");
        attendeeList.setConverter("badge", new BadgeToStringConverter());

        attendeeList.sort(sortBy, sortOrder);

        paymentList.setContainerDataSource(new BeanItemContainer<>(Payment.class, order.getPayments()));
        paymentList.setVisibleColumns("paymentType", "amount", "paymentTakenBy");
        paymentList.setColumnHeaders("Payment Type", "Amount", "Taken By");
        paymentList.setConverter("paymentTakenBy", new UserToStringConverter());

        paymentTotal.setValue(order.getTotalPaid().toString());

        if (order.getTotalAmount().equals(order.getTotalPaid())) {
            orderComplete.setEnabled(true);
        } else {
            orderComplete.setEnabled(false);
        }

        setEnabled(!order.getPaid());   // Disable editing if the order has been paid
    }

    public void setHandler(OrderPresenter presenter) {
        this.handler = presenter;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showCreditCardAuthWindow() {
        CreditCardAuthWindow creditCardAuthWindow = new CreditCardAuthWindow(this, handler);
        showWindow(creditCardAuthWindow);
    }

    public void showAttendeeDetail(Attendee attendee, List<Badge> availableBadgeTypes) {
        AttendeeWindow attendeeWindow = new AttendeeWindow(this, handler);
        AttendeeDetailForm form = attendeeWindow.getDetailForm();
        form.setAvailableBadges(availableBadgeTypes);
        form.show(attendee);
        form.setManualPriceEnabled(currentUserHasRight("attendee_override_price"));
        form.setParentFormReceivedVisible(true);
        showWindow(attendeeWindow);
    }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow printBadgeWindow = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(printBadgeWindow);
    }

    public void showConfirmCancelWindow() {
        ConfirmationWindow window = new ConfirmationWindow(this, "Are you sure you want to cancel this order?");
        showWindow(window);
    }

    public void confirmCancelOrder() {
        handler.cancelOrder(this);
    }

    /**
     * Message shown to users who do not have the right allowing them to check in someone on the blacklist
     */
    public void showBlacklistWarningWindow() {
        WarningWindow window = new WarningWindow("Please send this person to the manager's booth right away");
        showWindow(window);
    }

    /**
     * Message shown to users who have the right allowing them to check in someone on the blacklist
     */
    public void showBlacklistConfirmationWindow() {
        WarningWindow window = new WarningWindow("This person matches a name on the attendee blacklist");
        showWindow(window);
    }

    public void addPayment(PaymentWindow window, Payment payment) {
        try {
            handler.savePayment(this, payment);
            window.close();
        } catch (ValueException ex) {
            notifyError(ex.getMessage());
        }
    }

    public void deletePayment(PaymentWindow window, Payment payment) {
        try {
            handler.deletePayment(this, payment);
            window.close();
        } catch (Exception ex) {
            notifyError(ex.getMessage());
        }
    }

}
