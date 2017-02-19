package org.kumoricon.site.utility.closeouttill;

import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.order.Payment;
import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.service.print.ReportPrintService;
import org.kumoricon.site.report.till.TillReportPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
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

    @Autowired
    private SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(CloseOutTillPresenter.class);

    public void closeTill(CloseOutTillView view, User currentUser) {
        if (currentUser != null) {
            if (sessionService.userHasOpenSession(currentUser)) {
                Session currentSession = sessionService.getCurrentSessionForUser(currentUser);
                log.info("{} closing out till, session number {}", currentUser, currentSession);
                currentSession = sessionService.closeSessionForUser(currentUser);

                String output = sessionService.generateReportForSession(currentSession);
                view.showData(output);
                view.notify(reportPrintService.printReport(output, view.getCurrentClientIPAddress()));
            } else {
                log.warn("{} tried to close till but didn't have an open session", view.getCurrentUsername());
                view.notify("No till session open");
            }

        }
    }

    private static String getPaymentType(Integer typeId) {
        Payment.PaymentType[] orderTypes = Payment.PaymentType.values();
        return orderTypes[typeId].toString();
    }
}
