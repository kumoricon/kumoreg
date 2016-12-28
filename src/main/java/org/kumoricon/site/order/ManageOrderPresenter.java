package org.kumoricon.site.order;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class ManageOrderPresenter {
    @Autowired
    private OrderRepository orderRepository;

    private static final Logger log = LoggerFactory.getLogger(ManageOrderPresenter.class);

    public ManageOrderPresenter() {
    }

    public void orderSelected(ManageOrderView view, Order order) {
        if (order != null) {
            log.info("{} viewed order {}", view.getCurrentUsername(), order);
            view.navigateTo(ManageOrderView.VIEW_NAME + "/" + order.getId().toString());
            view.showOrder(order);
        }
    }

    public void orderSelected(ManageOrderView view, Integer id) {
        if (id != null) {
            Order order = orderRepository.findOne(id);
            orderSelected(view, order);
        }
    }

    public void saveOrder(ManageOrderView view, Order order) {
        log.info("{} saved order {}", view.getCurrentUsername(), order);
        orderRepository.save(order);
        view.navigateTo(ManageOrderView.VIEW_NAME);
    }

    public void showOrderList(ManageOrderView view) {
        log.info("{} viewed order list", view.getCurrentUsername());
        List<Order> orders = orderRepository.findAll();
        view.afterSuccessfulFetch(orders);
    }

    public void navigateToOrder(ManageOrderView view, String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            Order order = orderRepository.findOne(id);
            if (order != null) {
                view.selectOrder(order);
            } else {
                log.error("{} tried to view order id {} but it was not found in the database",
                    view.getCurrentUsername(), id);
            }
        }
    }
}
