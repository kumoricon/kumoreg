package org.kumoricon.presenter.order;

import org.kumoricon.KumoRegUI;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.view.attendee.AttendeeDetailForm;
import org.kumoricon.view.attendee.PrintBadgeWindow;
import org.kumoricon.view.order.AttendeeWindow;
import org.kumoricon.view.order.CreditCardAuthWindow;
import org.kumoricon.view.order.OrderView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class OrderPresenter implements PrintBadgeHandler {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private UserRepository userRepository;

    private OrderView view;

    private PrintBadgeWindow printBadgeWindow;

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
            view.notifyError("Error: order " + id + " not found.");
        }
    }

    public void cancelOrder() {
        // Todo: Remove from database if order hasn't been saved yet? Make sure to not
        // delete orders that are already paid for. Not sure if this is a good feature
        // or not
        KumoRegUI.getCurrent().getNavigator().navigateTo("");
    }

    public OrderView getView() { return view; }
    public void setView(OrderView view) { this.view = view; }

    public void addNewAttendee() {
        Attendee newAttendee = new Attendee();
        newAttendee.setBadgeNumber(generateBadgeNumber());
        newAttendee.setOrder(view.getOrder());
        selectAttendee(newAttendee);
    }

    public void addAttendeeToOrder(Attendee attendee) {
        Order order = view.getOrder();
        order.addAttendee(attendee);
        order.setTotalAmount(getOrderTotal(order));
        order = orderRepository.save(order);
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

    public void removeAttendeeFromOrder(Attendee attendee) {
        if (attendee != null && !attendee.isCheckedIn()) {
            String name = attendee.getName();
            Order order = view.getOrder();
            order.removeAttendee(attendee);
            attendee.setOrder(null);

            order.setTotalAmount(getOrderTotal(order));
            Order result = orderRepository.save(order);
            view.afterSuccessfulFetch(result);
            attendeeRepository.delete(attendee);
            view.notify(name + " deleted");
            view.afterSuccessfulFetch(order);
        }
    }

    public void takeMoney() {
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
            CreditCardAuthWindow creditCardAuthWindow = new CreditCardAuthWindow(this);
            view.showWindow(creditCardAuthWindow);
        } else {
            orderComplete(currentOrder);
        }

    }

    public void orderComplete(Order currentOrder) {
        currentOrder.paymentComplete(view.getCurrentUser());
        orderRepository.save(currentOrder);
        // Todo: Trigger printing badges
        showAttendeeBadgeWindow(currentOrder.getAttendees());
    }

    public void selectAttendee(Attendee attendee) {
        AttendeeWindow attendeeWindow = new AttendeeWindow(this);
        view.showWindow(attendeeWindow);
        AttendeeDetailForm form = attendeeWindow.getDetailForm();
        List<Badge> badgeTypesUserCanSee = new ArrayList<>();
        for (Badge badge : badgeRepository.findByVisibleTrue()) {
            if (badge.getRequiredRight() == null || view.currentUserHasRight(badge.getRequiredRight())) {
                badgeTypesUserCanSee.add(badge);
            }
        }
        form.setAvailableBadges(badgeTypesUserCanSee);

        form.setManualPriceEnabled(view.currentUserHasRight("attendee_override_price"));

        form.show(attendee);
    }


    private String generateBadgeNumber() {
        User user = userRepository.findOne(view.getCurrentUser().getId());
        StringBuilder output = new StringBuilder();
        output.append(user.getFirstName().charAt(0));
        output.append(user.getLastName().charAt(0));
        output.append(String.format("%1$05d", user.getNextBadgeNumber()));
        userRepository.save(user);
        return output.toString().toUpperCase();
    }

    public void saveAuthNumberClicked(String value) {
        Order order = view.getOrder();
        String oldNotes = "";
        if (order.getNotes() != null) { oldNotes = order.getNotes(); }
        order.setNotes("Credit card authorization Number: " + value + "\n" + oldNotes);
        orderComplete(order);
    }

    @Override
    public void showAttendeeBadgeWindow(List<Attendee> attendeeList) {
        printBadgeWindow = new PrintBadgeWindow(this, attendeeList);
        view.showWindow(printBadgeWindow);
    }

    public void badgePrintSuccess() {
        printBadgeWindow.close();
        view.notify("Order Complete");
        KumoRegUI.getCurrent().getNavigator().navigateTo("/");
    }

    @Override
    public void reprintBadges(List<Attendee> attendeeList) {
        view.notify("Reprinting badge...");
    }


}