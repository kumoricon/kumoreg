package org.kumoricon.site.order;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import org.kumoricon.model.order.Order;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class OrderWindow extends AbstractForm<Order> {

    private MTextField orderId = new MTextField("Order ID");
    private MTextField totalAmount = new MTextField("Amount");
    private MTextField orderTakenBy = new MTextField("Order Taken By");
    private MCheckBox paid = new MCheckBox("Paid");

    @Override
    protected Component createContent() {
        Layout toolbar = getToolbar();
        return new MVerticalLayout(orderId, totalAmount, orderTakenBy, paid, toolbar);
    }

}
