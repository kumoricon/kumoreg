package org.kumoricon.site.utility.importattendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.FieldCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AttendeeImporterService {
    private AttendeeRepository attendeeRepository;

    private OrderRepository orderRepository;

    private BadgeRepository badgeRepository;

    private UserRepository userRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(AttendeeImporterService.class);

    public AttendeeImporterService(AttendeeRepository attendeeRepository, OrderRepository orderRepository, BadgeRepository badgeRepository, UserRepository userRepository) {
        this.attendeeRepository = attendeeRepository;
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

    public String importFromTSV(Reader inputReader, User user) throws Exception {
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
            if (dataArray.length < 18 || dataArray.length > 19) {
                throw new Exception(String.format("Error: Line %s doesn't have 18 or 19 fields. Missing data?", lineNumber));
            }
            Attendee attendee = new Attendee();
            attendee.setFirstName(dataArray[0]);
            attendee.setLastName(dataArray[1]);
            attendee.setBadgeName(dataArray[2]);
            if (dataArray[3].trim().equals("")) {
                attendee.setBadgeNumber(generateBadgeNumber(currentUser.getNextBadgeNumber()));
            } else {
                attendee.setBadgeNumber(dataArray[3]);
            }
            attendee.setZip(dataArray[4]);
            attendee.setCountry(dataArray[5]);
            attendee.setPhoneNumber(FieldCleaner.cleanPhoneNumber(dataArray[6]));
            attendee.setEmail(dataArray[7]);
            attendee.setBirthDate(LocalDate.parse(dataArray[8], formatter));
            attendee.setEmergencyContactFullName(dataArray[9]);
            attendee.setEmergencyContactPhone(FieldCleaner.cleanPhoneNumber(dataArray[10]));
            if (dataArray[11].toUpperCase().equals("Y")) {
                attendee.setParentIsEmergencyContact(true);
            } else {
                attendee.setParentIsEmergencyContact(false);
            }
            attendee.setParentFullName(dataArray[12]);
            attendee.setParentPhone(FieldCleaner.cleanPhoneNumber(dataArray[13]));
            if (dataArray[14].toUpperCase().equals("Y")) {
                attendee.setPaid(true);
            } else {
                attendee.setPaid(false);
            }
            try {
                attendee.setPaidAmount(new BigDecimal(dataArray[15]));
            } catch (NumberFormatException e) {
                attendee.setPaidAmount(BigDecimal.ZERO);
            }
            if (badges.containsKey(dataArray[16])) {
                attendee.setBadge(badges.get(dataArray[16]));
            } else {
                log.error("Badge type " + dataArray[16] + " not found on line " + lineNumber);
                throw new Exception("Badge type " + dataArray[16] + " not found on line " + lineNumber);
            }

            if (orders.containsKey(dataArray[17])) {
                Order currentOrder = orders.get(dataArray[17]);
                attendee.setOrder(currentOrder);
                currentOrder.addAttendee(attendee);
                if (attendee.getPaid()) {
                    currentOrder.setTotalAmount(currentOrder.getTotalAmount().add(attendee.getPaidAmount()));
                }
            } else {
                Order o = new Order();
                o.setOrderId(dataArray[17]);
                o.addAttendee(attendee);
                o.setTotalAmount(attendee.getPaidAmount());
                o.setPaymentType(Order.PaymentType.CREDIT);
                orders.put(o.getOrderId(), o);
                ordersToAdd.add(o);
                attendee.setOrder(o);
            }
            if (dataArray.length == 19 && !dataArray[18].isEmpty() && !dataArray[18].trim().isEmpty()) {
                attendee.addHistoryEntry(currentUser, dataArray[18]);
            }
            attendee.setPreRegistered(true);
            attendeesToAdd.add(attendee);
        }
        TSVFile.close();

        log.info("Read " + lineNumber + " lines");
        log.info("Setting paid/unpaid status in {} orders", ordersToAdd.size());
        for (Order o : ordersToAdd) {
            validatePaidStatus(o);
        }


        log.info("{} saving {} orders and {} attendees to database", user, ordersToAdd.size(), attendeesToAdd.size());
        orderRepository.save(ordersToAdd);

        userRepository.save(currentUser);

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
