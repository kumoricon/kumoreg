package org.kumoricon.site.tillsession;

import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.service.print.ReportPrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TillSessionPresenter {
    private final SessionService sessionService;
    private final ReportPrintService reportPrintService;

    private static final Logger log = LoggerFactory.getLogger(TillSessionPresenter.class);

    @Autowired
    public TillSessionPresenter(SessionService sessionService, ReportPrintService reportPrintService) {
        this.reportPrintService = reportPrintService;
        this.sessionService = sessionService;
    }

    void showOpenTillSessionList(TillSessionView view) {
        log.info("{} viewed open till session list", view.getCurrentUsername());
        List<Session> sessions = sessionService.getAllOpenSessions();
        view.afterSuccessfulFetch(sessions);
    }

    void showAllTillSessionList(TillSessionView view) {
        log.info("{} viewed all till session list", view.getCurrentUsername());
        List<Session> sessions = sessionService.getAllSessions();
        view.afterSuccessfulFetch(sessions);
    }

    void showReportFor(TillSessionView view, Session session) {
        log.info("{} viewed till session report for {}", view.getCurrentUsername(), session);
        String report = sessionService.generateHTMLReportForSession(session);
        view.showReport(report);
    }

    void closeSession(TillSessionView view, Integer sessionId) {
        Session session = sessionService.closeSession(sessionId);
        view.notify(reportPrintService.printReport(
                sessionService.generateTextReportForSession(session),
                view.getCurrentClientIPAddress()));

        log.info("{} closed till session {} for user {}", view.getCurrentUsername(), session, session.getUser());
    }
}
