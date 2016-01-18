package org.kumoricon.view.order;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.order.Order;
import org.kumoricon.presenter.order.OrderPresenter;
import org.kumoricon.util.FieldFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@ViewScope
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends VerticalLayout implements View{
    public static final String VIEW_NAME = "order";

    @Autowired
    private OrderPresenter handler;

    private TextField orderId = FieldFactory.createDisabledTextField("Order ID");
    private TextField total = FieldFactory.createDisabledTextField("Total");
    private TextArea notes = FieldFactory.createTextArea("Notes");
    private Table attendeeList = new Table();
    private Button addAttendee = new Button("Add Attendee");
    private BeanItemContainer<Attendee> attendeeBeanList;

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSpacing(true);
        setMargin(true);

        FormLayout orderInfo = new FormLayout();
        orderId.setWidth(400, Unit.PIXELS);
        orderInfo.addComponent(orderId);
        addComponent(orderInfo);

        attendeeBeanList = new BeanItemContainer<>(Attendee.class, new ArrayList<Attendee>());;
        attendeeList.setContainerDataSource(attendeeBeanList);
        attendeeList.setPageLength(5);
        attendeeList.setWidth(600, Unit.PIXELS);

        orderInfo.addComponent(attendeeList);
        orderInfo.addComponent(addAttendee);
        orderInfo.addComponent(total);
        orderInfo.addComponent(notes);
        notes.setSizeFull();

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            // If no parameters, create a new order and navigate to it
            handler.createNewOrder();
        } else {
            String searchString = viewChangeEvent.getParameters();
            handler.showOrder(Integer.parseInt(searchString));
        }
    }

    public Order getOrder() {
        return null;
    }
    public void afterSuccessfulFetch(Order order) {
        orderId.setValue(order.getOrderId());
        total.setValue(order.getTotalAmount().toString());
        notes.setValue(order.getNotes());

        attendeeList.setContainerDataSource(new BeanItemContainer<>(Attendee.class, order.getAttendeeList()));
        attendeeList.setVisibleColumns(new String[] { "firstName", "lastName", "badge", "paidAmount"});
        attendeeList.setColumnHeaders("First Name", "Last Name", "Badge Type", "Cost");

//        attendeeBeanList.removeAllItems();
//        attendeeBeanList.addAll(attendees);
    }

    public void setHandler(OrderPresenter presenter) {
        this.handler = presenter;
    }

}
