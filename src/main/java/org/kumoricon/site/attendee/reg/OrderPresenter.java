package org.kumoricon.site.attendee.reg;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.blacklist.BlacklistService;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.order.Payment;
import org.kumoricon.model.order.PaymentRepository;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.service.validate.AttendeeValidator;
import org.kumoricon.service.validate.PaymentValidator;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.attendee.BadgePrintingPresenter;
import org.kumoricon.site.attendee.PrintBadgeHandler;
import org.kumoricon.site.attendee.PrintBadgeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderPresenter extends BadgePrintingPresenter implements PrintBadgeHandler {
    private final OrderRepository orderRepository;

    private final BadgeRepository badgeRepository;

    private final AttendeeRepository attendeeRepository;

    private final AttendeeValidator attendeeValidator;

    private final UserRepository userRepository;

    private final BlacklistService blacklistService;

    private final PaymentRepository paymentRepository;

    private final SessionService sessionService;

    private final AttendeeHistoryRepository attendeeHistoryRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderPresenter.class);

    @Autowired
    public OrderPresenter(OrderRepository orderRepository, BadgeRepository badgeRepository, AttendeeRepository attendeeRepository, AttendeeValidator attendeeValidator, UserRepository userRepository, BlacklistService blacklistService, PaymentRepository paymentRepository, SessionService sessionService, AttendeeHistoryRepository attendeeHistoryRepository) {
        this.orderRepository = orderRepository;
        this.badgeRepository = badgeRepository;
        this.attendeeRepository = attendeeRepository;
        this.attendeeValidator = attendeeValidator;
        this.userRepository = userRepository;
        this.blacklistService = blacklistService;
        this.paymentRepository = paymentRepository;
        this.sessionService = sessionService;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    public void createNewOrder(OrderView view) {
        Order order = new Order();
        order.setOrderId(Order.generateOrderId());
        order.setOrderTakenByUser(view.getCurrentUser());
        log.info("{} created new order {}", view.getCurrentUser(), order);
        order = orderRepository.save(order);
        view.navigateTo(OrderView.VIEW_NAME + "/" + order.getId());
    }

    public void showOrder(PaymentView view, int id) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            log.info("{} viewed order {}", view.getCurrentUsername(), order);
            view.showOrder(order);
        } else {
            log.error("{} tried to view order {} and it was not found.", view.getCurrentUsername(), id);
            view.notifyError("Error: order " + id + " not found.");
            view.close();
        }
    }

    public void savePayment(PaymentView view, Order order, Payment payment) throws ValidationException {
        if (Payment.PaymentType.PREREG.equals(payment.getPaymentType()) && !view.currentUserHasRight("import_pre_reg_data")) {
            throw new ValueException("Only users with import_pre_reg_data right can select the PreReg payment type");
        }
        PaymentValidator.validate(payment);

        // Only update user, time and location if they're null - otherwise someone could be saving
        // changes to an existing payment
        if (payment.getPaymentTakenBy() == null) {
            payment.setPaymentTakenBy(view.getCurrentUser());
        }
        if (payment.getPaymentTakenAt() == null) {
            payment.setPaymentTakenAt(Instant.now());
        }
        if (payment.getPaymentLocation() == null) {
            payment.setPaymentLocation(view.getCurrentClientIPAddress());
        }
        if (payment.getSession() == null) {
            payment.setSession(sessionService.getCurrentSessionForUser(view.getCurrentUser()));
        }
        log.info("{} saved payment {} to {}", view.getCurrentUsername(), payment, order);
        order.addPayment(payment);
        Order saved = orderRepository.save(order);

        if (saved.getTotalAmount().compareTo(saved.getTotalPaid()) == 0) {
            view.navigateTo(OrderView.VIEW_NAME + "/" + order.getId() + "/print");
        } else {
            view.close();
        }
    }

    public void deletePayment(PaymentView view, int orderId, Payment payment) {
        Order order = orderRepository.findOne(orderId);
        log.info("{} removed payment {} from {}", view.getCurrentUsername(), payment, order);

        order.removePayment(payment);
        payment.setOrder(null);
        Order saved = orderRepository.save(order);
        paymentRepository.deleteById(payment.getId());
    }

    public void cancelOrder(OrderView view) {
        Order order = view.getOrder();
        if (order.getAttendeeList().size() == 0 && !order.getPaid()) {
            log.info("{} canceled empty order {}. It was deleted.", view.getCurrentUsername(), order);
            orderRepository.delete(order);
        }
        view.navigateTo("");
    }

    public void addNewAttendee(AttendeeRegDetailView view, int orderId) {
        log.info("{} created new attendee", view.getCurrentUsername());
        Attendee newAttendee = new Attendee();
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            view.notifyError(String.format("Order %s not found", orderId));
            view.close();
        }

        newAttendee.setBadgeNumber(generateBadgeNumber(view));
        newAttendee.setOrder(order);

        // If the order already has attendees, carry over emergency contact information from the
        // last attendee added
        if (order != null && order.getAttendees() != null && order.getAttendees().size() > 0) {
            Attendee lastAttendee = order.getAttendees().get(order.getAttendees().size() -1);
            newAttendee.setEmergencyContactFullName(lastAttendee.getEmergencyContactFullName());
            newAttendee.setEmergencyContactPhone(lastAttendee.getEmergencyContactPhone());
        }

        List<Badge> badgeTypesUserCanSee = new ArrayList<>();
        for (Badge badge : badgeRepository.findByVisibleTrue()) {
            if (badge.getRequiredRight() == null || view.currentUserHasRight(badge.getRequiredRight())) {
                badgeTypesUserCanSee.add(badge);
            }
        }

        view.showAttendee(newAttendee, badgeTypesUserCanSee);
    }


    public void removeAttendeeFromOrder(AttendeeRegDetailView view, Attendee attendee) {
        if (attendee != null && !attendee.getCheckedIn()) {
            String name = attendee.getName();
            Order order = attendee.getOrder();
            log.info("{} removed attendee {} from order {}. Attendee deleted.", view.getCurrentUsername(), attendee, order);
            order.removeAttendee(attendee);
            attendee.setOrder(null);

            attendeeHistoryRepository.deleteInBatch(attendee.getHistory());

            Order result = orderRepository.save(order);
            attendeeRepository.deleteById(attendee.getId());
            view.notify(name + " deleted");
        } else {
            view.notify("Error: " + attendee.toString() + " is checked in and may not be deleted");
        }
    }

    public void takeMoney(OrderView view) {
        Order currentOrder = view.getOrder();
        if (currentOrder.getAttendeeList().size() == 0) {
            view.notify("Error: No attendees in order");
            return;
        }

        // If the order total is $0 (all badges are free), just set payment type to cash automatically
        if (currentOrder.getTotalAmount().compareTo(currentOrder.getTotalPaid()) < 0) {
            view.notify("Error: money received less than order total");
        } else {
            orderComplete(view, currentOrder);
        }
    }

    public void orderComplete(OrderView view, Order currentOrder) {

        List<Attendee> badgesToPrint = new ArrayList<>();
        for (Attendee a : currentOrder.getAttendees()) {
            if (!a.getCheckedIn()) {
                badgesToPrint.add(a);
            }
        }
        log.info("{} saved order {} with {} badges to print",
                view.getCurrentUsername(), currentOrder, badgesToPrint.size());

        currentOrder.paymentComplete(view.getCurrentUser());

        orderRepository.save(currentOrder);

        if (badgesToPrint.size() > 0) {
            view.navigateTo(OrderPrintView.VIEW_NAME + "/" + currentOrder.getId() + "/" + "print");
//            showAttendeeBadgeWindow(view, badgesToPrint, false);
        } else {
            view.navigateTo("/");
        }
    }

    @Transactional
    String generateBadgeNumber(AttendeeRegDetailView view) {
        User user = userRepository.findOne(view.getCurrentUser().getId());
        String badgeNumber = String.format("%1S%2$05d", user.getBadgePrefix(), user.getNextBadgeNumber());

        log.info("{} generated badge number {}", view.getCurrentUsername(), badgeNumber);
        userRepository.save(user);
        view.setLoggedInUser(user);
        return badgeNumber;
    }

    public void validate(Attendee attendee) throws ValidationException {
        if (attendee.isMinor()) {   // Move parent form received in to attendeeValidator???
            if (attendee.getParentFormReceived() == null || !attendee.getParentFormReceived()) {
                throw new ValidationException("Error: Parental consent form has not been received");
            }
        }
        attendeeValidator.validate(attendee);
    }

    @Override
    public void badgePrintSuccess(OrderPrintView view, List<Attendee> attendees) {
        log.info("{} reported badge(s) printed successfully for {}",
                view.getCurrentUser(), attendees);

        // Attendees registering at-con should not have pre-printed badges, so don't bother
        // resetting attendee.badgePrePrinted here.
        view.notify("Order Complete");
        view.navigateTo("/");

    }


    public void badgePrintSuccess(OrderPrintView view, Order currentOrder) {
        log.info("{} reported badge(s) printed successfully for {}",
                view.getCurrentUser(), currentOrder.getAttendees());
        currentOrder.paymentComplete(view.getCurrentUser());

        orderRepository.save(currentOrder);
        view.notify("Order Complete");
        view.navigateTo("/");
    }

    @Override
    public void reprintBadges(OrderPrintView view, List<Attendee> attendeeList) {
        log.info("{} printing badge(s) for {} (reprint during order)",
                view.getCurrentUser(), attendeeList);
        printBadges(view, attendeeList);
    }

    @Override
    public BadgePrintFormatter getBadgeFormatter(PrintBadgeView printBadgeView, List<Attendee> attendees) {
        return badgePrintService.getCurrentBadgeFormatter(attendees, printBadgeView.getCurrentClientIPAddress());
    }


    public void showPayment(OrderPaymentView view, Integer orderId) {
        log.info("{} viewed payment info for {}",
            view.getCurrentUser(),
            orderId);
        Order order = orderRepository.findOne(orderId);
        view.showOrder(order);
    }

    public void showBadges(OrderPrintView view, Integer orderId) {
        Order order = orderRepository.findOne(orderId);
        printBadges(view, order.getAttendees());
        view.showOrder(order);
    }

    public void saveAttendee(AttendeeRegDetailView view, Attendee attendee) throws ValidationException {
        Order order = attendee.getOrder();
        if (!blacklistService.isOnBlacklist(attendee)) {
            log.info("{} added attendee {} to order {}", view.getCurrentUsername(), attendee, order);
        } else {
            if (view.currentUserHasRight("at_con_registration_blacklist")) {
                log.info("{} added blacklisted attendee {} to order {}", view.getCurrentUsername(), attendee, order);
                view.showBlacklistConfirmationWindow();
            } else {
                view.showBlacklistWarningWindow();
                log.info("{} tried to add {} to {} but attendee is on blacklist. Attendee not added.",
                        view.getCurrentUser(), attendee, order);
                return;
            }
        }

        try {
            attendeeValidator.validate(attendee);
            attendee = attendeeRepository.save(attendee);
            view.notify(String.format("Saved %s %s", attendee.getFirstName(), attendee.getLastName()));
            log.info("{} saved {}", view.getCurrentUsername(), attendee);
        } catch (ValidationException e) {
            log.error("{} tried to save {} and got error {}",
                    view.getCurrentUser(), attendee, e.getMessage());
            throw e;
        }


    }

    public void saveOrderNotes(Integer orderId, String notes) {
        Order fromDatabase = orderRepository.findOne(orderId);
        if (fromDatabase.getNotes() != notes) {
            fromDatabase.setNotes(notes);
            orderRepository.save(fromDatabase);
        }
    }
}
