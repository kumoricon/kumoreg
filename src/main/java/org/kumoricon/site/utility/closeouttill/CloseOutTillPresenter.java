package org.kumoricon.site.utility.closeouttill;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.order.Payment;
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
            List<Object[]> results = orderRepository.getSessionOrderCountsAndTotals(
                    currentUser.getId(), sessionNumber);
            String report = TillReportPresenter.getTillReportStr(currentUser, sessionNumber, results);
            output.append(report);
            log.info("Till report:\n" + report);
            sessionNumber += 1;
            currentUser.setSessionNumber(sessionNumber);
            userRepository.save(currentUser);
            output.append(String.format("Session closed. New session number is: %d", sessionNumber));
            view.showData(output.toString());
            log.info("{} created new till session, number {}", currentUser, currentUser.getSessionNumber());
            view.notify(reportPrintService.printReport(output.toString(), view.getCurrentClientIPAddress()));
        }
    }

    private static String getPaymentType(Integer typeId) {
        Payment.PaymentType[] orderTypes = Payment.PaymentType.values();
        return orderTypes[typeId].toString();
    }
}
