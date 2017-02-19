package org.kumoricon.site.tillsession;

import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerRepository;
import org.kumoricon.model.printer.Printer;
import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.site.computer.window.AddPrinterWindow;
import org.kumoricon.site.computer.window.PrinterWindowCallback;
import org.kumoricon.site.computer.window.ViewInstructionsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TillSessionPresenter {
    @Autowired
    private SessionService sessionService;
    private static final Logger log = LoggerFactory.getLogger(TillSessionPresenter.class);

    public TillSessionPresenter() {
    }

    public void showOpenTillSessionList(TillSessionView view) {
        log.info("{} viewed open till session list", view.getCurrentUsername());
        List<Session> sessions = sessionService.getAllOpenSessions();
        view.afterSuccessfulFetch(sessions);
    }

    public void showAllTillSessionList(TillSessionView view) {
        log.info("{} viewed all till session list", view.getCurrentUsername());
        List<Session> sessions = sessionService.getAllSessions();
        view.afterSuccessfulFetch(sessions);
    }

    public void showReportFor(TillSessionView view, Session session) {
        log.info("{} viewed till session report for {}", view.getCurrentUsername(), session);
        String report = sessionService.generateReportForSession(session);
        view.showReport(report);
    }
}
