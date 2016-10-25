package org.kumoricon.site.utility.importattendee;

import org.junit.Before;
import org.junit.Test;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.order.Order;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttendeeImporterServiceTest {
    private Order order;
    private Attendee alice;
    private Attendee bob;

    @Before
    public void setUp() throws Exception {
        order = new Order();
        alice = new Attendee();
        alice.setFirstName("Alice");
        bob = new Attendee();
        bob.setFirstName("Bob");
        order.addAttendee(alice);
        order.addAttendee(bob);
    }

    @Test
    public void validatePaidStatusTrue() throws Exception {
        alice.setPaid(true);
        bob.setPaid(true);

        AttendeeImporterService.validatePaidStatus(order);

        assertTrue(order.getPaid());
        for (Attendee attendee : order.getAttendeeList()) {
            assertTrue(attendee.getPaid());
        }
    }

    @Test
    public void validatePaidStatusFalse() throws Exception {
        alice.setPaid(false);
        bob.setPaid(false);

        AttendeeImporterService.validatePaidStatus(order);

        assertFalse(order.getPaid());
        for (Attendee attendee : order.getAttendeeList()) {
            assertFalse(attendee.getPaid());
        }
    }

    @Test(expected=Exception.class)
    public void validatePaidStatusMustBeSameForAllAttendees() throws Exception {
        alice.setPaid(true);
        bob.setPaid(false);

        AttendeeImporterService.validatePaidStatus(order);
    }


}