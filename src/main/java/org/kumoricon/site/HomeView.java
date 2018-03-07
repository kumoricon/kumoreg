package org.kumoricon.site;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ServiceException;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.badge.Badge;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends BaseGridView implements View {
    public static final String VIEW_NAME = "";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private HomePresenter handler;

    private Label welcome = new Label("Welcome to Kumoricon!");
    private Label priceList = new Label("");

    @PostConstruct
    void init() {
        setColumns(3);
        setRows(2);

        addComponent(welcome, 0, 0);
        addComponent(priceList, 1, 1);
        priceList.setContentMode(ContentMode.HTML);

        setColumnExpandRatio(0, 1);
        setColumnExpandRatio(1, 3);
        setColumnExpandRatio(2, 1);
        handler.showBadges(this);
    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showBadges(List<Badge> badges) {
        /* TODO: Make this look better. Border on the table, bold header row, right-align numbers, etc
          Add any CSS classes to styles.scss. */
        StringBuilder output = new StringBuilder();
        output.append("<table>");
        output.append("<tr>");
        output.append("<td>Badge Type</td>");
        output.append("<td>Adult (18+)</td>");
        output.append("<td>Youth (13 - 17)</td>");
        output.append("<td>Child (6 - 12)</td>");
        output.append("<td>5 and Under</td>");
        output.append("</tr>");
        for (Badge badge : badges) {
            try {
                output.append("<tr>");
                output.append("<td>" + badge.getName() + "</td>");
                output.append("<td>$" + badge.getCostForAge(35L) + "</td>");
                output.append("<td>$" + badge.getCostForAge(17L) + "</td>");
                output.append("<td>$" + badge.getCostForAge(11L) + "</td>");
                output.append("<td>$" + badge.getCostForAge(4L) + "</td>");
                output.append("</tr>");
            } catch (ServiceException e) {
                notifyError("Error getting age ranges for badge " + badge.getName());
            }
        }
        output.append("</table>");
        priceList.setValue(output.toString());
    }
}