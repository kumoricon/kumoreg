package org.kumoricon.site.attendee.reg;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.order.Order;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.FieldFactory;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.ConfirmationWindow;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.site.fieldconverter.BadgeToStringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ViewScope
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends BaseView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "at_con_registration";

    @Autowired
    private OrderPresenter handler;

    private TextField orderId = FieldFactory.createDisabledTextField("Order ID");
    private TextField total = FieldFactory.createDisabledTextField("Total");
    private TextArea notes = FieldFactory.createTextArea("Notes");
    private Table attendeeList = new Table();
    private Button addAttendee = new Button("Add Attendee");
    private NativeSelect paymentType = new NativeSelect("Payment Type");
    private Button takeMoney = new Button("Take Money");
    private Button cancel = new Button("Cancel");
    private BeanItemContainer<Attendee> attendeeBeanList;
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


        orderInfo.addComponent(attendeeList);
        attendeeList.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent ->
                handler.selectAttendee(this, (Attendee)itemClickEvent.getItemId()));

        orderInfo.addComponent(addAttendee);
        orderInfo.addComponent(total);
        paymentType.setNullSelectionAllowed(false);
        orderInfo.addComponent(paymentType);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.addComponent(takeMoney);
        buttonLayout.addComponent(cancel);
        orderInfo.addComponent(buttonLayout);

        orderInfo.addComponent(notes);
        notes.setSizeFull();

        addAttendee.addClickListener((Button.ClickListener) clickEvent -> handler.addNewAttendee(this));
        takeMoney.addClickListener((Button.ClickListener) clickEvent -> handler.takeMoney(this));
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

    private void enableFields(boolean enable) {
        addAttendee.setEnabled(enable);
        addAttendee.setVisible(enable);
        paymentType.setEnabled(enable);
        notes.setEnabled(enable);
        takeMoney.setEnabled(enable);
        takeMoney.setVisible(enable);
    }

    public Order getOrder() {
        currentOrder.setPaymentType((Order.PaymentType)paymentType.getValue());
        currentOrder.setNotes(notes.getValue());
        return currentOrder;
    }

    public void afterSuccessfulFetch(Order order) {
        this.currentOrder = order;
        orderId.setValue(order.getOrderId());
        total.setValue(order.getTotalAmount().toString());
        notes.setValue(order.getNotes());

        paymentType.removeAllItems();
        paymentType.addItems(Order.PaymentType.values());
        paymentType.select(order.getPaymentType());

        attendeeList.setContainerDataSource(new BeanItemContainer<>(Attendee.class, order.getAttendeeList()));
        attendeeList.setVisibleColumns("firstName", "lastName", "badge", "paidAmount");
        attendeeList.setColumnHeaders("First Name", "Last Name", "Badge Type", "Cost");
        attendeeList.setConverter("badge", new BadgeToStringConverter());

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
}
