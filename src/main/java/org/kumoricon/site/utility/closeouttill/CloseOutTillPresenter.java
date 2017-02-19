package org.kumoricon.site.utility.closeouttill;

import org.kumoricon.model.order.Payment;
import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.User;
import org.kumoricon.service.print.ReportPrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class CloseOutTillPresenter {

    @Autowired
    private ReportPrintService reportPrintService;

    @Autowired
    private SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(CloseOutTillPresenter.class);

    public void closeTill(CloseOutTillView view, User currentUser) {
        if (currentUser != null) {
            if (sessionService.userHasOpenSession(currentUser)) {
                Session currentSession = sessionService.getCurrentSessionForUser(currentUser);
                log.info("{} closing out till for session {}", currentUser, currentSession);
                currentSession = sessionService.closeSessionForUser(currentUser);

                String output = sessionService.generateHTMLReportForSession(currentSession);
                view.showData(output);
                view.notify(reportPrintService.printHTMLReport(output, view.getCurrentClientIPAddress()));
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
