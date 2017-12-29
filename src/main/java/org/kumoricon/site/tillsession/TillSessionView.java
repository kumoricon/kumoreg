package org.kumoricon.site.tillsession;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.v7.ui.*;
import org.kumoricon.model.session.Session;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.fieldconverter.StringToLocalDateTimeConverter;
import org.kumoricon.site.fieldconverter.UserToStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.List;


@ViewScope
@SpringView(name = TillSessionView.VIEW_NAME)
public class TillSessionView extends BaseView implements View {
    public static final String VIEW_NAME = "tillSessions";
    public static final String REQUIRED_RIGHT = "manage_till_sessions";

    private final TillSessionPresenter handler;

    private final Table sessionTable = new Table();
    private final Button btnShowOpen = new Button("Show Open Sessions");
    private final Button btnShowAll = new Button("Show All");

    @Autowired
    public TillSessionView(TillSessionPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        VerticalLayout pageLayout = new VerticalLayout();
        pageLayout.setSizeFull();
        pageLayout.setSpacing(true);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setMargin(false);
        buttons.addComponent(btnShowOpen);
        buttons.addComponent(btnShowAll);
        pageLayout.addComponent(buttons);

        btnShowOpen.addClickListener((Button.ClickListener) clickEvent -> showOpenClicked());
        btnShowAll.addClickListener((Button.ClickListener) clickEvent -> showAllClicked());
        sessionTable.addItemClickListener((ItemClickEvent.ItemClickListener) itemClickEvent -> {
                    BeanItem b = (BeanItem)itemClickEvent.getItem();
                    sessionClicked((Session)b.getBean());
                });

        pageLayout.addComponent(sessionTable);

        sessionTable.setWidth("90%");
        sessionTable.setNullSelectionAllowed(false);
        sessionTable.setImmediate(true);
        sessionTable.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        sessionTable.addGeneratedColumn("manage", new CloseButtonColumnGenerator());

        addComponent(pageLayout);

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
        Object[] sortBy = {sessionTable.getSortContainerPropertyId()};
        boolean[] sortOrder = {sessionTable.isSortAscending()};
        sessionTable.setContainerDataSource(new BeanItemContainer<>(Session.class, sessions));
        sessionTable.setVisibleColumns("id", "user", "start", "end", "open", "manage");
        sessionTable.setColumnHeaders("Id", "User", "Start Time", "End Time", "Open", "");
        sessionTable.setConverter("user", new UserToStringConverter());
        sessionTable.setConverter("start", new StringToLocalDateTimeConverter());
        sessionTable.setConverter("end", new StringToLocalDateTimeConverter());
        sessionTable.sort(sortBy, sortOrder);
    }

    class CloseButtonColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object item, Object columnId) {
            Session session = (Session)item;

            if (session.isOpen()) {
                Button button = new Button("Close Session");
                button.setData(session.getId());
                button.addClickListener((Button.ClickListener) clickEvent -> {
                    Integer sessionId = (Integer)clickEvent.getButton().getData();
                    closeSessionClicked(sessionId);
                });
                return button;
            }
            return null;
        }
    }

    private void closeSessionClicked(Integer sessionId) {
        handler.closeSession(this, sessionId);
        handler.showOpenTillSessionList(this);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showReport(String report) {
        Window reportWindow = new ReportWindow("Till Report", report);
        showWindow(reportWindow);
    }
}