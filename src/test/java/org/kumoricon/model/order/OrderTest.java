package org.kumoricon.model.order;

import org.junit.Before;
import org.junit.Test;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderTest {
    Order order;
    @Before
    public void setUp() throws Exception {
        order = createTestOrder();
    }

    @Test
    public void generateOrderIdAreRandom() throws Exception {
        // Make sure generated order IDs are not repeated.
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            ids.add(Order.generateOrderId());
        }
        assertTrue("Duplicates found in 20 generated order IDs", ids.size()==20);
    }

    @Test
    public void paymentCompleteSetsUser() throws Exception {
        User user = UserFactory.newUser("Draco", "Ula");
        order.paymentComplete(user);

        assertEquals(user, order.getPaymentTakenByUser());
    }

    @Test
    public void paymentCompleteSetsPaid() throws Exception {
        User user = UserFactory.newUser("Draco", "Ula");
        order.paymentComplete(user);

        assertTrue(order.getPaid());
    }

    @Test
    public void paymentCompleteSetsAttendeesPaid() throws Exception {
        User user = UserFactory.newUser("Draco", "Ula");
        order.paymentComplete(user);

        for (Attendee a : order.getAttendeeList()) {
            assertTrue(a.getPaid());
        }
    }

    @Test
    public void paymentCompleteSetsSession() throws Exception {
        User user = UserFactory.newUser("Draco", "Ula");
        order.paymentComplete(user);

        assertEquals(user.getSessionNumber(), order.getPaidSession());
    }

    @Test
    public void paymentCompleteSetsPaidAt() throws Exception {
        // Since time could be close but not exact as the test runs, make sure it's within one second
        User user = UserFactory.newUser("Draco", "Ula");
        order.paymentComplete(user);

        assertTrue(order.getPaidAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(order.getPaidAt().isAfter(LocalDateTime.now().minusSeconds(1)));
        assertEquals(user.getSessionNumber(), order.getPaidSession());
    }

    @Test
    public void getTotalPaid() {
        Order o = createTestOrder();
        o.addPayment(createPayment(10));
        o.addPayment(createPayment(10));
        assertEquals(BigDecimal.valueOf(20L), o.getTotalPaid());
    }

    private static Payment createPayment(int amount) {
        Payment p = new Payment();
        p.setAmount(new BigDecimal(amount));
        p.setPaymentType(Payment.PaymentType.CASH);
        return p;
    }

    private static Order createTestOrder() {
        Order order = new Order();
        order.setOrderId(Order.generateOrderId());

        Attendee alice = new Attendee();
        alice.setId(1);
        alice.setPaidAmount(BigDecimal.TEN);
        Attendee bob = new Attendee();
        bob.setPaidAmount(BigDecimal.TEN);
        bob.setId(2);
        Attendee charlie = new Attendee();
        charlie.setPaidAmount(BigDecimal.ZERO);
        charlie.setId(3);
        order.addAttendee(alice);
        order.addAttendee(bob);
        order.addAttendee(charlie);
        return order;
    }

}