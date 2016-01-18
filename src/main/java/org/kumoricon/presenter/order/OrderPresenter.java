package org.kumoricon.presenter.order;

import com.vaadin.ui.Notification;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.view.order.OrderView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class OrderPresenter {
    @Autowired
    private OrderRepository orderRepository;

    private OrderView view;

    public OrderPresenter() {
    }

    public void createNewOrder() {
        Order order = new Order();
        order.setOrderId(order.generateOrderId());
        orderRepository.save(order);
        KumoRegUI.getCurrent().getNavigator().navigateTo(view.VIEW_NAME + "/" + order.getId());
    }

    public void showOrder(int id) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            view.afterSuccessfulFetch(order);
        } else {
            Notification.show("Error: order " + id + " not found.");
        }
    }

    public void saveOrder() {
        Order order = view.getOrder();
        orderRepository.save(order);
        Notification.show(String.format("Saved %s %s", order.getId()));
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public void cancelOrder() {
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public OrderView getView() { return view; }
    public void setView(OrderView view) { this.view = view; }

}