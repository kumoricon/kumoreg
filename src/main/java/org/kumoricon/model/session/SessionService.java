package org.kumoricon.model.session;

import org.kumoricon.model.order.Payment;
import org.kumoricon.model.order.PaymentRepository;
import org.kumoricon.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class SessionService {
    private SessionRepository repository;
    private PaymentRepository paymentRepository;

    @Autowired
    public SessionService(SessionRepository repository, PaymentRepository paymentRepository) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
    }

    public Session getCurrentSessionForUser(User user) {
        if (user == null) { return null; }
        Session session = repository.getOpenSessionForUserId(user);
        if (session == null) {
            session = repository.save(new Session(user));
            System.out.println("created new session " + session);
            System.out.println(session.getUuid());
        }
        return session;
    }

    public boolean userHasOpenSession(User user) {
        if (user == null) { return false; }
        Session session = repository.getOpenSessionForUserId(user);
        return session != null;
    }

    public Session closeSessionForUser(User user) {
        Session session = repository.getOpenSessionForUserId(user);
        if (session != null) {
            session.setEnd(LocalDateTime.now());
            session.setOpen(false);
            repository.save(session);
        }
        return session;
    }

    public List<Session> getAllOpenSessions() {
        return repository.findAllOpenSessions();
    }

    public BigDecimal getTotalForSession(Session s) {
        BigDecimal total = BigDecimal.ZERO;
        Set<Payment> payments = paymentRepository.findAllInSession(s);

        for (Payment p : payments) {
            total = total.add(p.getAmount());
        }
        return total;
    }

    public String generateReportForSession(Session session) {
        if (session == null) { return ""; }
        final User user = session.getUser();
        if (user == null) { return "User not found for session " + session; }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        final StringBuilder output = new StringBuilder();

        // Title
        output.append("Till Report\n\n");

        // Name
        output.append("User: ")
              .append(user.getFirstName())
              .append(" ")
              .append(user.getLastName())
              .append(" (id: ")
              .append(user.getId())
              .append(")\n");

        // Date range
        output.append(session.getStart().format(formatter));
        output.append(" - ");
        if (session.getEnd() != null) {
            output.append(session.getEnd().format(formatter));
        } else {
            output.append("now");
        }
        output.append("\n");

        // Total per payment type
        for (Payment.PaymentType pt : Payment.PaymentType.values()) {
            BigDecimal amount = paymentRepository.getTotalByPaymentTypeForSessionId(session.getId(), pt.getValue());
            if (amount != null) {
                output.append(String.format("%1$-20s:    $%2$10s\n", pt.toString(), amount));
            }
        }

        output.append("\n\n");
        output.append("Report generated at ");
        output.append(LocalDateTime.now().format(formatter));

        return output.toString();
    }


}
