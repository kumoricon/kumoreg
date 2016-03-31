package org.kumoricon.presenter.order;

import org.kumoricon.attendee.BadgePrintService;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.view.BaseView;
import org.kumoricon.view.attendee.AttendeePrintView;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.kumoricon.view.order.OrderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderPresenter implements PrintBadgeHandler {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgePrintService badgePrintService;

    @Autowired
    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderPresenter.class);

    public OrderPresenter() {
    }

    public void createNewOrder(OrderView view) {
        Order order = new Order();
        order.setOrderId(order.generateOrderId());
        orderRepository.save(order);
        log.info(String.format("%s created new order %s", view.getCurrentUser(), order));
        view.navigateTo(view.VIEW_NAME + "/" + order.getId());
    }

    public void showOrder(OrderView view, int id) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            view.afterSuccessfulFetch(order);
        } else {
            log.error(String.format("%s tried to view order %s and it was not found.", view.getCurrentUser(), id));
            view.notifyError("Error: order " + id + " not found.");
        }
    }

    public void cancelOrder(OrderView view) {
        Order order = view.getOrder();
        if (order.getAttendeeList().size() == 0 && !order.getPaid()) {
            orderRepository.delete(order);
            log.info(String.format("%s canceled empty order %s. It was deleted.", view.getCurrentUser(), order));
        }
        view.navigateTo("");
    }

    public void addNewAttendee(OrderView view) {
        Attendee newAttendee = new Attendee();
        newAttendee.setBadgeNumber(generateBadgeNumber(view));
        newAttendee.setOrder(view.getOrder());
        selectAttendee(view, newAttendee);
    }

    public void addAttendeeToOrder(OrderView view, Attendee attendee) {
        Order order = view.getOrder();
        order.addAttendee(attendee);
        order.setTotalAmount(getOrderTotal(order));
        order = orderRepository.save(order);
        log.info(String.format("%s added attendee %s to order %s", view.getCurrentUser(), attendee, order));
        view.afterSuccessfulFetch(order);
    }

    private static BigDecimal getOrderTotal(Order order) {
        // Just get the total for all the attendees instead of keeping a running total
        // and adding the latest amount to it. Keeping a running total made testing a pain
        // if a value somehow got corrupt along the way
        BigDecimal total = BigDecimal.ZERO;
        for (Attendee a : order.getAttendeeList()) {
            total = total.add(a.getPaidAmount());
        }
        return total;
    }

    public void removeAttendeeFromOrder(OrderView view, Attendee attendee) {
        if (attendee != null && !attendee.isCheckedIn()) {
            String name = attendee.getName();
            Order order = view.getOrder();
            order.removeAttendee(attendee);
            attendee.setOrder(null);

            order.setTotalAmount(getOrderTotal(order));
            Order result = orderRepository.save(order);
            view.afterSuccessfulFetch(result);
            attendeeRepository.delete(attendee);
            log.info(String.format("%s removed attendee %s from order %s. Attendee was deleted.",
                    view.getCurrentUser(), attendee, order));
            view.notify(name + " deleted");
            view.afterSuccessfulFetch(order);
        }
    }

    public void takeMoney(OrderView view) {
        Order currentOrder = view.getOrder();
        if (currentOrder.getAttendeeList().size() == 0) {
            view.notify("Error: No attendees in order");
            return;
        }
        if (currentOrder.getPaymentType() == null) {
            view.notify("Error: Payment type not selected");
            return;
        }

        if (currentOrder.getPaymentType().equals(Order.PaymentType.CREDIT)) {
            view.showCreditCardAuthWindow();
        } else {
            orderComplete(view, currentOrder);
        }

    }

    public void orderComplete(OrderView view, Order currentOrder) {
        currentOrder.paymentComplete(view.getCurrentUser());
        orderRepository.save(currentOrder);
        log.info(String.format("%s completed order %s and took payment $%s",
                view.getCurrentUser(), currentOrder, currentOrder.getTotalAmount()));
        showAttendeeBadgeWindow(view, currentOrder.getAttendees());
    }

    public void selectAttendee(OrderView view, Attendee attendee) {
        List<Badge> badgeTypesUserCanSee = new ArrayList<>();
        for (Badge badge : badgeRepository.findByVisibleTrue()) {
            if (badge.getRequiredRight() == null || view.currentUserHasRight(badge.getRequiredRight())) {
                badgeTypesUserCanSee.add(badge);
            }
        }
        view.showAttendeeDetail(attendee, badgeTypesUserCanSee);
    }

    @Transactional
    private String generateBadgeNumber(OrderView view) {
        User user = userRepository.findOne(view.getCurrentUser().getId());
        StringBuilder output = new StringBuilder();
        output.append(user.getFirstName().charAt(0));
        output.append(user.getLastName().charAt(0));
        output.append(String.format("%1$05d", user.getNextBadgeNumber()));
        userRepository.save(user);
        log.info(String.format("%s generated badge number %s", view.getCurrentUser(), output.toString().toUpperCase()));
        return output.toString().toUpperCase();
    }

    public void saveAuthNumberClicked(OrderView view, String value) {
        Order order = view.getOrder();
        String oldNotes = "";
        if (order.getNotes() != null) { oldNotes = order.getNotes(); }
        order.setNotes("Credit card authorization Number: " + value + "\n" + oldNotes);
        orderComplete(view, order);
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s printing badges for: ", view.getCurrentUser()));
        for (Attendee attendee : attendeeList) {
            sb.append(attendee.getName());
            sb.append("; ");
        }
        log.info(sb.toString());
        view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
        view.showPrintBadgeWindow(attendeeList);
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        BaseView view = printBadgeWindow.getParentView();
        printBadgeWindow.close();
        view.notify("Order Complete");
        view.navigateTo("/");
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s printing badges for: ", printBadgeWindow.getParentView().getCurrentUser()));
        for (Attendee attendee : attendeeList) {
            sb.append(attendee.getName());
            sb.append("; ");
        }
        sb.append(" (Reprint from window)");
        log.info(sb.toString());
        printBadgeWindow.getParentView().notify(
                badgePrintService.printBadgesForAttendees(attendeeList,
                        printBadgeWindow.getParentView().getCurrentClientIPAddress()));
    }
}