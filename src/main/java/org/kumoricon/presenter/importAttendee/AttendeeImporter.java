package org.kumoricon.presenter.importAttendee;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttendeeImporter {
    private AttendeeRepository attendeeRepository;

    private OrderRepository orderRepository;

    private BadgeRepository badgeRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AttendeeImporter(AttendeeRepository attendeeRepository, OrderRepository orderRepository, BadgeRepository badgeRepository) {
        this.attendeeRepository = attendeeRepository;
        this.orderRepository = orderRepository;
        this.badgeRepository = badgeRepository;
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

    public String importFromTSV(Reader inputReader) throws Exception {
        BufferedReader TSVFile = new BufferedReader(inputReader);

        String dataRow = TSVFile.readLine(); // Skip the header row
        Integer lineNumber = 1;
        List<Attendee> attendeesToAdd = new ArrayList<>();
        List<Order> ordersToAdd = new ArrayList<>();

        HashMap<String, Badge> badges = getBadgeHashMap();
        HashMap<String, Order> orders = getOrderHashMap();

        while (dataRow != null){
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
            attendee.setBadgeNumber(dataArray[3]);
            attendee.setZip(dataArray[4]);
            attendee.setCountry(dataArray[5]);
            attendee.setPhoneNumber(dataArray[6]);
            attendee.setEmail(dataArray[7]);
            attendee.setBirthDate(LocalDate.parse(dataArray[8], formatter));
            attendee.setEmergencyContactFullName(dataArray[9]);
            attendee.setEmergencyContactPhone(dataArray[10]);
            if (dataArray[11].toUpperCase().equals("Y")) {
                attendee.setParentIsEmergencyContact(true);
            } else {
                attendee.setParentIsEmergencyContact(false);
            }
            attendee.setParentFullName(dataArray[12]);
            attendee.setParentPhone(dataArray[13]);
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
                throw new Exception("Badge type " + dataArray[16] + " not found on line " + lineNumber);
            }

            if (orders.containsKey(dataArray[17])) {
                attendee.setOrder(orders.get(dataArray[17]));
            } else {
                Order o = new Order();
                o.setOrderId(dataArray[17]);
                orders.put(o.getOrderId(), o);
                ordersToAdd.add(o);
                attendee.setOrder(o);
            }
            attendee.setNotes(dataArray[18]);
            attendee.setPreRegistered(true);
            attendeesToAdd.add(attendee);
        }
        TSVFile.close();

        orderRepository.save(ordersToAdd);
        attendeeRepository.save(attendeesToAdd);

        return String.format("%s attendees and %s orders imported.", attendeesToAdd.size(), ordersToAdd.size());
    }

}
