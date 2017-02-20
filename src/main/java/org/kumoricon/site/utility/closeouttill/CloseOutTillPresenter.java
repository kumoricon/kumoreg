package org.kumoricon.site.utility.closeouttill;

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
    private final ReportPrintService reportPrintService;
    private final SessionService sessionService;
    private static final Logger log = LoggerFactory.getLogger(CloseOutTillPresenter.class);

    @Autowired
    public CloseOutTillPresenter(ReportPrintService reportPrintService, SessionService sessionService) {
        this.reportPrintService = reportPrintService;
        this.sessionService = sessionService;
    }

    void closeTill(CloseOutTillView view, User currentUser) {
        if (currentUser != null) {
            if (sessionService.userHasOpenSession(currentUser)) {
                Session currentSession = sessionService.getCurrentSessionForUser(currentUser);
                log.info("{} closing out till for session {}", currentUser, currentSession);
                currentSession = sessionService.closeSessionForUser(currentUser);

                // Note: Currently this displays the report in HTML format, then generates it
                // again for printing. Could be a performance problem down the road, not clear
                // yet.
                view.showData(sessionService.generateHTMLReportForSession(currentSession));
                view.notify(reportPrintService.printReport(
                        sessionService.generateTextReportForSession(currentSession),
                        view.getCurrentClientIPAddress()));
            } else {
                log.warn("{} tried to close till but didn't have an open session", view.getCurrentUsername());
                view.notify("No till session open");
            }

        }
    }
}
