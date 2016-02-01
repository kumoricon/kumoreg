package org.kumoricon.view.order;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.order.Order;
import org.kumoricon.presenter.order.OrderPresenter;
import org.kumoricon.util.FieldFactory;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@ViewScope
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends BaseView implements View{
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

    protected FieldGroup fieldGroup;

    @PostConstruct
    public void init() {
        handler.setView(this);

        FormLayout orderInfo = new FormLayout();
        orderId.setWidth(400, Unit.PIXELS);
        orderInfo.addComponent(orderId);
        addComponent(orderInfo);

        attendeeBeanList = new BeanItemContainer<>(Attendee.class, new ArrayList<Attendee>());;
        attendeeList.setContainerDataSource(attendeeBeanList);
        attendeeList.setPageLength(5);
        attendeeList.setWidth(600, Unit.PIXELS);

        orderInfo.addComponent(attendeeList);
        attendeeList.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent ->
                handler.selectAttendee((Attendee)itemClickEvent.getItemId()));

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

        addAttendee.addClickListener((Button.ClickListener) clickEvent -> handler.addNewAttendee());
        takeMoney.addClickListener((Button.ClickListener) clickEvent -> handler.takeMoney());
        cancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelOrder());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            // If no parameters, create a new order and navigate to it
            handler.createNewOrder();
        } else {
            String searchString = viewChangeEvent.getParameters();
            handler.showOrder(Integer.parseInt(searchString));
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
        attendeeList.setVisibleColumns(new String[] { "firstName", "lastName", "badge", "paidAmount"});
        attendeeList.setColumnHeaders("First Name", "Last Name", "Badge Type", "Cost");

        setEnabled(!order.getPaid());   // Disable editing if the order has been paid
    }

    public void setHandler(OrderPresenter presenter) {
        this.handler = presenter;
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}