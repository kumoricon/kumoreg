package org.kumoricon.site.utility.closeouttill;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.print.ReportPrintService;
import org.kumoricon.site.report.till.TillReportPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("request")
public class CloseOutTillPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReportPrintService reportPrintService;

    private static final Logger log = LoggerFactory.getLogger(CloseOutTillPresenter.class);

    public void closeTill(CloseOutTillView view, User currentUser) {
        if (currentUser != null) {
            Integer sessionNumber = currentUser.getSessionNumber();
            log.info("{} closing out till, session number {}", currentUser, sessionNumber);
            StringBuilder output = new StringBuilder();
            output.append(String.format("User ID: %d (%s)%n", currentUser.getId(), currentUser.getUsername()));
            output.append(String.format("%s %s%n", currentUser.getFirstName(), currentUser.getLastName()));
            output.append(String.format("%s%n", LocalDateTime.now()));
            output.append("--------------------------------------------------------------------------------\n");
            output.append(String.format("Session Number: %d%n%n", currentUser.getSessionNumber()));

            List<Object[]> results = orderRepository.getSessionOrderCountsAndTotals(
                    currentUser.getId(), sessionNumber);
            String report = TillReportPresenter.getTillReportStr(currentUser, sessionNumber, results);
            output.append(report);

                    currentUser.getId(), currentUser.getSessionNumber());
            output.append(String.format("%-40s\t%s\t%s%n", "Payment Type", "Count", "Total"));
            for (Object[] line : results) {
                log.info("{} till session {}: Payment Type: {}, Attendees: {}, Total: ${}",
                        currentUser, sessionNumber, Order.PaymentType.fromInteger((Integer)line[0]).toString(),
                output.append(String.format("%-40s\t%5s\t$%8.2f%n",
                        getPaymentType((Integer)line[0]), line[1], line[2]));
                log.info("{} till session {}: Payment Type: {}, Orders: {}, Total: ${}",
                        currentUser, currentUser.getSessionNumber(), getPaymentType((Integer)line[0]),
                        line[1], line[2]);
            }

            sessionNumber += 1;
            currentUser.setSessionNumber(sessionNumber);
            userRepository.save(currentUser);
            output.append(String.format("Session closed. New session number is: %d", sessionNumber));
            view.showData(output.toString());
            log.info("{} created new till session, number {}.", currentUser, currentUser.getSessionNumber());
            view.notify(reportPrintService.printReport(output.toString(), view.getCurrentClientIPAddress()));
        }
    }
}
