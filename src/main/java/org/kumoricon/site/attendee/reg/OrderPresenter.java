package org.kumoricon.site.attendee.reg;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.print.BadgePrintService;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.service.validate.AttendeeValidator;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.PrintBadgeHandler;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
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
    private AttendeeValidator attendeeValidator;

    @Autowired
    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderPresenter.class);

    public OrderPresenter() {
    }

    public void createNewOrder(OrderView view) {
        Order order = new Order();
        order.setOrderId(order.generateOrderId());
        log.info("{} created new order {}", view.getCurrentUser(), order);
        orderRepository.save(order);
        view.navigateTo(view.VIEW_NAME + "/" + order.getId());
    }

    public void showOrder(OrderView view, int id) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            log.info("{} viewed order {}", view.getCurrentUser(), order);
            view.afterSuccessfulFetch(order);
        } else {
            log.error("{} tried to view order {} and it was not found.", view.getCurrentUser(), id);
            view.notifyError("Error: order " + id + " not found.");
        }
    }

    public void cancelOrder(OrderView view) {
        Order order = view.getOrder();
        if (order.getAttendeeList().size() == 0 && !order.getPaid()) {
            log.info("{} canceled empty order {}. It was deleted.", view.getCurrentUser(), order);
            orderRepository.delete(order);
        }
        view.navigateTo("");
    }

    public void addNewAttendee(OrderView view) {
        log.info("{} created new attendee", view.getCurrentUser());
        Attendee newAttendee = new Attendee();
        newAttendee.setBadgeNumber(generateBadgeNumber(view));
        newAttendee.setOrder(view.getOrder());
        selectAttendee(view, newAttendee);
    }

    public void addAttendeeToOrder(OrderView view, Attendee attendee) {
        Order order = view.getOrder();
        log.info("{} added attendee {} to order {}", view.getCurrentUser(), attendee, order);
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

    public void removeAttendeeFromOrder(OrderView view, Attendee attendee) {
        if (attendee != null && !attendee.getCheckedIn()) {
            String name = attendee.getName();
            Order order = view.getOrder();
            log.info("{} removed attendee {} from order {}. Attendee deleted.", view.getCurrentUser(), attendee, order);
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

    public void takeMoney(OrderView view) {
        Order currentOrder = view.getOrder();
        if (currentOrder.getAttendeeList().size() == 0) {
            view.notify("Error: No attendees in order");
            return;
        }

        if (currentOrder.getPaymentType() == null && currentOrder.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            view.notify("Error: Payment type not selected");
            return;
        }

        // If the order total is $0 (all badges are free), just set payment type to cash automatically
        if (currentOrder.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            currentOrder.setPaymentType(Order.PaymentType.CASH);
        }

        if (currentOrder.getPaymentType().equals(Order.PaymentType.CREDIT)) {
            view.showCreditCardAuthWindow();
        } else {
            orderComplete(view, currentOrder);
        }

    }

    public void orderComplete(OrderView view, Order currentOrder) {
        log.info("{} completed order {} and took payment ${}",
                view.getCurrentUser(), currentOrder, currentOrder.getTotalAmount());
        currentOrder.paymentComplete(view.getCurrentUser());

        orderRepository.save(currentOrder);

        showAttendeeBadgeWindow(view, currentOrder.getAttendees());
    }

    public void selectAttendee(OrderView view, Attendee attendee) {
        log.info("{} viewed attendee {}", view.getCurrentUser(), attendee);
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
        log.info("{} generated badge number {}", view.getCurrentUser(), output.toString().toUpperCase());
        userRepository.save(user);
        view.setLoggedInUser(user);
        return output.toString().toUpperCase();
    }

    public void saveAuthNumberClicked(OrderView view, String value) {
        Order order = view.getOrder();
        log.info("{} set credit card authorization number {} for {}", view.getCurrentUser(), value, order);
        String oldNotes = "";
        if (order.getNotes() != null) { oldNotes = order.getNotes(); }
        order.setNotes("Credit card authorization number: " + value + "\n" + oldNotes);
        orderComplete(view, order);
    }

    public Boolean validate(Attendee attendee) throws ValueException {
        if (attendee.isMinor()) {   // Move parent form received in to attendeeValidator???
            if (attendee.getParentFormReceived() == null || !attendee.getParentFormReceived()) {
                throw new ValueException("Error: Parental consent form has not been received");
            }
        }
        return attendeeValidator.validate(attendee);
    }

    @Override
    public void showAttendeeBadgeWindow(AttendeePrintView view, List<Attendee> attendeeList) {
        log.info("{} printing badge(s) for: {}", view.getCurrentUser(), attendeeList);
        view.notify(badgePrintService.printBadgesForAttendees(attendeeList, view.getCurrentClientIPAddress()));
        view.showPrintBadgeWindow(attendeeList);
    }

    @Override
    public void badgePrintSuccess(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        BaseView view = printBadgeWindow.getParentView();
        log.info("{} reported badge(s) printed successfully for {}",
                printBadgeWindow.getParentView().getCurrentUser(), attendees);
        printBadgeWindow.close();
        view.notify("Order Complete");
        view.navigateTo("/");
    }

    @Override
    public void reprintBadges(PrintBadgeWindow printBadgeWindow, List<Attendee> attendeeList) {
        log.info("{} printing badge(s) for {} (reprint during order)",
                printBadgeWindow.getParentView().getCurrentUser(), attendeeList);
        printBadgeWindow.getParentView().notify(
                badgePrintService.printBadgesForAttendees(attendeeList,
                        printBadgeWindow.getParentView().getCurrentClientIPAddress()));
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeWindow printBadgeWindow, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees, printBadgeWindow.getParentView().getCurrentClientIPAddress());
    }

}
