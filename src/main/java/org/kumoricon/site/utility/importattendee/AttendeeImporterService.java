package org.kumoricon.site.utility.importattendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
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
            if (dataArray.length != 19) {
                throw new Exception(String.format("Error: Line %s doesn't have 19 fields. Missing data?", lineNumber));
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
            attendee.setPhoneNumber(cleanPhoneNumber(dataArray[6]));
            attendee.setEmail(dataArray[7]);
            attendee.setBirthDate(LocalDate.parse(dataArray[8], formatter));
            attendee.setEmergencyContactFullName(dataArray[9]);
            attendee.setEmergencyContactPhone(cleanPhoneNumber(dataArray[10]));
            if (dataArray[11].toUpperCase().equals("Y")) {
                attendee.setParentIsEmergencyContact(true);
            } else {
                attendee.setParentIsEmergencyContact(false);
            }
            attendee.setParentFullName(dataArray[12]);
            attendee.setParentPhone(cleanPhoneNumber(dataArray[13]));
            if (dataArray[14].toUpperCase().equals("Y")) {
                attendee.setPaid(true);
            } else {
                attendee.setPaid(false);
            }
            try {
                attendee.setPaidAmount(new BigDecimal(dataArray[15]));
            } catch (NumberFormatException e) {
                System.out.println(dataArray[15]);
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
                currentOrder.setTotalAmount(currentOrder.getTotalAmount().add(attendee.getPaidAmount()));
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
            if (!dataArray[18].isEmpty() && !dataArray[18].toString().trim().isEmpty()) {
                attendee.addHistoryEntry(currentUser, dataArray[18]);
            }
            attendee.setPreRegistered(true);
            attendeesToAdd.add(attendee);
        }
        TSVFile.close();

        log.info("Read " + lineNumber + " lines");

        // Check to make sure all attendees in order have paid and set paid flag accordingly
        for (Order o : ordersToAdd) {
            Boolean isPaid = true;
            for (Attendee a : o.getAttendeeList()) {
                if (a.getPaid() == false) { isPaid = false; }
            }
            o.setPaid(isPaid);
        }

        log.info("{} saving {} orders and {} attendees to database", user, ordersToAdd.size(), attendeesToAdd.size());
        orderRepository.save(ordersToAdd);

        userRepository.save(currentUser);

        log.info("{} done importing data", user);
        return String.format("Imported %s attendees and %s orders", attendeesToAdd.size(), ordersToAdd.size());
    }

    private String generateBadgeNumber(Integer badgeNumber) {
        StringBuilder output = new StringBuilder();
        output.append("ONL");
        output.append(String.format("%1$05d", badgeNumber));
        return output.toString().toUpperCase();
    }

    /**
     * Removes characters from the given string except for [0-9 -] and formats it nicely. If
     * it's a 10 digit number, returns the format 123-456-7890. Otherwise, just returns
     * whatever digits, dashes and spaces exist.
     * @param phoneNumber String
     * @return String
     */
    protected static String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) { return null; }
        String output;
        String p = phoneNumber.replaceAll("[^\\+0-9]", "");
        if (p.matches("\\d{10}")) {
            output = p.substring(0, 3) + "-" + p.substring(3, 6) + "-" + p.substring(6, 10);
        } else {
            output = phoneNumber.replaceAll("[^\\+0-9x -]", "").trim();
            output = output.replaceAll("\\s\\s+", " ");                 // Multiple spaces to single space
        }
        if (!phoneNumber.equals(output)) {
            log.info("While reformatting phone numbers, changed \"{}\" to \"{}\"", phoneNumber, output);
        }
        return output;
    }
}
