package org.kumoricon.site.tillsession;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.kumoricon.model.session.Session;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.List;


@ViewScope
@SpringView(name = TillSessionView.VIEW_NAME)
public class TillSessionView extends BaseView implements View {
    public static final String VIEW_NAME = "tillSessions";
    public static final String REQUIRED_RIGHT = "manage_till_sessions";

    private final TillSessionPresenter handler;

    private final Grid<Session> sessionTable = new Grid<>();
    private final Button btnShowOpen = new Button("Show Open Sessions");
    private final Button btnShowAll = new Button("Show All");

    @Autowired
    public TillSessionView(TillSessionPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {

        btnShowOpen.addClickListener((Button.ClickListener) clickEvent -> showOpenClicked());
        btnShowAll.addClickListener((Button.ClickListener) clickEvent -> showAllClicked());
        sessionTable.addItemClickListener(itemClickEvent -> sessionClicked(itemClickEvent.getItem()));

        sessionTable.addColumn(Session::getId).setCaption("ID");
        sessionTable.addColumn(session -> session.getUser().getFirstName() + " " + session.getUser().getLastName()).setCaption("User");
        sessionTable.addColumn(Session::getStart).setCaption("Start Time");
        sessionTable.addColumn(Session::getEnd).setCaption("End Time");
        sessionTable.addColumn(Session::isOpen).setCaption("Open");
        sessionTable.addColumn(session -> "Close",
                new ButtonRenderer(clickEvent -> {
                    Session s = (Session)clickEvent.getItem();
                    closeSessionClicked((s.getId()));
                }));

        sessionTable.setWidth("90%");

        addComponent(btnShowOpen);
        addComponent(btnShowAll);
        addComponent(sessionTable);

        handler.showOpenTillSessionList(this);
    }

    private void sessionClicked(Session session) {
        handler.showReportFor(this, session);
    }

    private void showAllClicked() {
        handler.showAllTillSessionList(this);
    }

    private void showOpenClicked() {
        handler.showOpenTillSessionList(this);
    }

    public void afterSuccessfulFetch(List<Session> sessions) {
        sessionTable.setItems(sessions);
    }

    private void closeSessionClicked(Integer sessionId) {
        try {
            handler.closeSession(this, sessionId);
            handler.showOpenTillSessionList(this);
        } catch (RuntimeException ex) {
            notify(ex.getMessage());
        }
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showReport(String report) {
        Window reportWindow = new ReportWindow("Till Report", report);
        showWindow(reportWindow);
    }
}