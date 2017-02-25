package org.kumoricon.site.utility.importattendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.order.Payment;
import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.FieldCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

class AttendeeImporterService {
    private SessionService sessionService;

    private OrderRepository orderRepository;

    private BadgeRepository badgeRepository;

    private UserRepository userRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(AttendeeImporterService.class);

    AttendeeImporterService(SessionService sessionService, OrderRepository orderRepository, BadgeRepository badgeRepository, UserRepository userRepository) {
        this.sessionService = sessionService;
        this.orderRepository = orderRepository;
        this.badgeRepository = badgeRepository;
        this.userRepository = userRepository;
    }

    private HashMap<String, Badge> getBadgeHashMap() {
        HashMap<String, Badge> badges = new HashMap<>();
        for (Badge b : badgeRepository.findAll()) {
            badges.put(b.getName(), b);
        }
        return badges;
    }

    private HashMap<String, Order> getOrderHashMap() {
        HashMap<String, Order> orders = new HashMap<>();
        for (Order o : orderRepository.findAll()) {
            orders.put(o.getOrderId(), o);
        }
        return orders;
    }

    String importFromTSV(Reader inputReader, User user) throws Exception {
        log.info("{} starting data import", user);
        BufferedReader TSVFile = new BufferedReader(inputReader);
        String dataRow = TSVFile.readLine(); // Skip the header row
        Integer lineNumber = 1;
        List<Attendee> attendeesToAdd = new ArrayList<>();
        List<Order> ordersToAdd = new ArrayList<>();

        HashMap<String, Badge> badges = getBadgeHashMap();
        HashMap<String, Order> orders = getOrderHashMap();
        User currentUser = userRepository.findOne(user.getId());

        while (dataRow != null){
            if (lineNumber % 1000 == 0) { log.info("Read " + lineNumber + " lines"); }
            lineNumber++;
            dataRow = TSVFile.readLine();
            if (dataRow == null || dataRow.trim().length() == 0) { continue; }  // Skip blank lines
            String[] dataArray = dataRow.split("\t");
            if (dataArray.length < 20 || dataArray.length > 21) {
                throw new Exception(String.format("Error: Line %s doesn't have 20 or 21 fields. Missing data?", lineNumber));
            }
            Attendee attendee = new Attendee();
            attendee.setFirstName(dataArray[0]);
            attendee.setLastName(dataArray[1]);
            attendee.setLegalFirstName(dataArray[2]);
            attendee.setLegalLastName(dataArray[3]);
            attendee.setBadgeName(dataArray[4]);
            if (dataArray[5].trim().equals("")) {
                attendee.setBadgeNumber(generateBadgeNumber(currentUser.getNextBadgeNumber()));
            } else {
                attendee.setBadgeNumber(dataArray[5]);
            }
            attendee.setZip(dataArray[6]);
            attendee.setCountry(dataArray[7]);
            attendee.setPhoneNumber(FieldCleaner.cleanPhoneNumber(dataArray[8]));
            attendee.setEmail(dataArray[9]);
            attendee.setBirthDate(LocalDate.parse(dataArray[10], formatter));
            attendee.setEmergencyContactFullName(dataArray[11]);
            attendee.setEmergencyContactPhone(FieldCleaner.cleanPhoneNumber(dataArray[12]));
            if (dataArray[13].toUpperCase().equals("Y")) {
                attendee.setParentIsEmergencyContact(true);
            } else {
                attendee.setParentIsEmergencyContact(false);
            }
            attendee.setParentFullName(dataArray[14]);
            attendee.setParentPhone(FieldCleaner.cleanPhoneNumber(dataArray[15]));
            if (dataArray[16].toUpperCase().equals("Y")) {
                attendee.setPaid(true);
            } else {
                attendee.setPaid(false);
            }
            try {
                attendee.setPaidAmount(new BigDecimal(dataArray[17]));
            } catch (NumberFormatException e) {
                attendee.setPaidAmount(BigDecimal.ZERO);
            }
            if (badges.containsKey(dataArray[18])) {
                attendee.setBadge(badges.get(dataArray[18]));
            } else {
                log.error("Badge type " + dataArray[18] + " not found on line " + lineNumber);
                throw new Exception("Badge type " + dataArray[18] + " not found on line " + lineNumber);
            }

            if (orders.containsKey(dataArray[19])) {
                Order currentOrder = orders.get(dataArray[19]);
                attendee.setOrder(currentOrder);
                currentOrder.addAttendee(attendee);
            } else {
                Order o = new Order();
                o.setOrderId(dataArray[19]);
                o.addAttendee(attendee);
                orders.put(o.getOrderId(), o);
                ordersToAdd.add(o);
                attendee.setOrder(o);
            }
            if (dataArray.length == 21 && !dataArray[20].isEmpty() && !dataArray[20].trim().isEmpty()) {
                attendee.addHistoryEntry(currentUser, dataArray[20]);
            }
            attendee.setPreRegistered(true);
            attendeesToAdd.add(attendee);
        }
        TSVFile.close();

        log.info("Read " + lineNumber + " lines");
        log.info("Setting paid/unpaid status in {} orders", ordersToAdd.size());

        if (sessionService.userHasOpenSession(currentUser)) {
            log.info("{} closed open session {} before import",
                    currentUser, sessionService.getCurrentSessionForUser(currentUser));
        }
        Session session = sessionService.getCurrentSessionForUser(currentUser);
        for (Order o : ordersToAdd) {
            validatePaidStatus(o);
            if (o.getPaid()) {
                Payment p = new Payment();
                p.setAmount(o.getTotalAmount());
                p.setPaymentType(Payment.PaymentType.PREREG);
                p.setPaymentTakenAt(LocalDateTime.now());
                p.setPaymentLocation("kumoricon.org");
                p.setPaymentTakenBy(currentUser);
                p.setSession(session);
                p.setOrder(o);
                o.addPayment(p);
            }
        }


        log.info("{} saving {} orders and {} attendees to database", user, ordersToAdd.size(), attendeesToAdd.size());
        orderRepository.save(ordersToAdd);

        userRepository.save(currentUser);

        log.info("{} closing session used during import");
        sessionService.closeSessionForUser(currentUser);

        log.info("{} done importing data", user);
        return String.format("Imported %s attendees and %s orders", attendeesToAdd.size(), ordersToAdd.size());
    }

    /**
     * Validate and set paid status. Modifies items in place.
     * Makes sure that all attendees in the order have paid, or all have not paid, and sets the
     * order as paid accordingly.
     * @param order Orders with attendees to validate
     * @throws Exception Raises exception if any order has no attendees in it.
     */
    static void validatePaidStatus(Order order) throws Exception {
        if (order == null) { return; }
        Set<Attendee> attendees = order.getAttendeeList();
        if (attendees.size() == 0) {
            log.error("Error: Order {} has 0 attendees. This shouldn't happen!", order);
            throw new Exception("Error: Order " + order + " has 0 attendees. This shouldn't happen!");
        }
        Boolean isPaid = attendees.iterator().next().getPaid();
        for (Attendee a : attendees) {
            if (a.getPaid() != isPaid) {
                log.error("Error: Order {} has both paid and unpaid. This shouldn't happen!", order);
                throw new Exception("Error: Order " + order + " has both paid and unpaid attendees");
            }
        }
        order.setPaid(isPaid);
    }

    private String generateBadgeNumber(Integer badgeNumber) {
        return String.format("ONL%1$05d", badgeNumber);
    }


}
