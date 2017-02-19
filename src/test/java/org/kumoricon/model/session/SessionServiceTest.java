package org.kumoricon.model.session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kumoricon.KumoregApplication;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.order.Payment;
import org.kumoricon.model.order.PaymentRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;
import org.kumoricon.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KumoregApplication.class)
@TestPropertySource(locations="classpath:test.properties")
@WebAppConfiguration
public class SessionServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SessionService sessionService;

    @Autowired
    OrderRepository orderRepository;

    private static boolean setUpIsDone = false;

    @Before
    public void setUp() throws Exception {
        if (setUpIsDone) {
            return;
        }
        List<User> users = buildUsers();
        userRepository.save(users);

        List<Payment> payments = buildPayments(users);
        paymentRepository.save(payments);
        setUpIsDone = true;
    }


    @Test
    public void getCurrentSessionForUser() throws Exception {
        // Sequential calls to getCurrentSessionForUser should return the same session
        User user = userRepository.findOne(2);
        Session s = sessionService.getCurrentSessionForUser(user);
        assertEquals(s, sessionService.getCurrentSessionForUser(user));
    }


    @Test
    public void userHasOpenSession() throws Exception {
        User user = userRepository.findOne(2);
        assertTrue(sessionService.userHasOpenSession(user));
    }

    @Test
    public void userHasOpenSessionAfterClosingCurrentSession() throws Exception {
        User user = userRepository.findOne(2);
        assertTrue(sessionService.userHasOpenSession(user));
        sessionService.closeSessionForUser(user);
        assertFalse(sessionService.userHasOpenSession(user));
    }

    @Test
    public void closeSessionForUser() throws Exception {
        User user = userRepository.findOne(2);
        Session openSession = sessionService.getCurrentSessionForUser(user);
        sessionService.closeSessionForUser(user);
        assertFalse(sessionService.userHasOpenSession(user));
        // Make sure that getting the current session for a user with no current
        // session returns a new session
        assertNotEquals(openSession, sessionService.getCurrentSessionForUser(user));
    }

    @Test
    public void getAllOpenSessions() throws Exception {
        User user = userRepository.findOne(2);
        List<Session> sessions = sessionService.getAllOpenSessions();
        assertEquals(3, sessions.size());
    }

    @Test
    public void getTotalForSession() throws Exception {
        User user = userRepository.findOne(2);
        Session session = sessionService.getCurrentSessionForUser(user);

        assertEquals(0, BigDecimal.valueOf(16F).compareTo(sessionService.getTotalForSession(session)));
    }


    private List<User> buildUsers() {
        List<User> users = new ArrayList<>();
        users.add(UserFactory.newUser("Bob", "Jones"));
        users.add(UserFactory.newUser("John", "Smith"));
        users.add(UserFactory.newUser("Alice", "Alexander"));
        return users;
    }

    private List<Payment> buildPayments(List<User> users) {
        List<Payment> payments = new ArrayList<>();
        for (User user : users) {
            Session s = sessionService.getCurrentSessionForUser(user);
            Payment p = new Payment();
            Order o = new Order();
            o.setOrderId(Order.generateOrderId());
            o = orderRepository.save(o);
            p.setAmount(BigDecimal.TEN);
            p.setSession(s);
            p.setPaymentTakenBy(user);
            p.setPaymentTakenAt(LocalDateTime.now());
            p.setPaymentType(Payment.PaymentType.CASH);
            p.setOrder(o);
            payments.add(p);
            p = new Payment();
            p.setAmount(BigDecimal.ONE);
            p.setSession(s);
            p.setPaymentTakenBy(user);
            p.setPaymentTakenAt(LocalDateTime.now());
            o = new Order();
            o.setOrderId(Order.generateOrderId());
            o = orderRepository.save(o);
            p.setOrder(o);
            p.setAuthNumber("1234");
            p.setPaymentType(Payment.PaymentType.CHECK);
            payments.add(p);
            p = new Payment();
            p.setAmount(BigDecimal.valueOf(5L));
            p.setSession(s);
            p.setPaymentTakenBy(user);
            p.setPaymentTakenAt(LocalDateTime.now());
            p.setAuthNumber("1234");
            o = new Order();
            o.setOrderId(Order.generateOrderId());
            o = orderRepository.save(o);
            p.setOrder(o);
            p.setPaymentType(Payment.PaymentType.CREDIT);
            payments.add(p);
        }

        return payments;
    }



}