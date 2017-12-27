package org.kumoricon.site;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ServiceException;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.NumberRenderer;
import org.kumoricon.model.badge.Badge;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends BaseView implements View {
    public static final String VIEW_NAME = "";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private HomePresenter handler;

    private Label welcome = new Label("Welcome to Kumoricon!");
    private Grid passTypesTable = new Grid("");

    @PostConstruct
    void init() {
        addComponent(welcome);

        passTypesTable.setWidth(750, Unit.PIXELS);
        passTypesTable.setHeightMode(HeightMode.ROW);
        passTypesTable.addColumn("Badge Type", String.class);
        passTypesTable.addColumn("adult", BigDecimal.class);
        passTypesTable.addColumn("youth", BigDecimal.class);
        passTypesTable.addColumn("child", BigDecimal.class);
        passTypesTable.addColumn("under5", BigDecimal.class);

        Grid.Column adult = passTypesTable.getColumn("adult");
        Grid.Column youth = passTypesTable.getColumn("youth");
        Grid.Column child = passTypesTable.getColumn("child");
        Grid.Column under5 = passTypesTable.getColumn("under5");

        adult.setRenderer(new NumberRenderer("$%.2f", Locale.ENGLISH));
        adult.setHeaderCaption("Adult (18+)");

        youth.setRenderer(new NumberRenderer("$%.2f", Locale.ENGLISH));
        youth.setHeaderCaption("Youth (13 - 17)");
        child.setRenderer(new NumberRenderer("$%.2f", Locale.ENGLISH));
        child.setHeaderCaption("Child (6 - 12)");
        under5.setRenderer(new NumberRenderer("$%.2f", Locale.ENGLISH));
        under5.setHeaderCaption("5 and under");

        passTypesTable.setEnabled(false);
        passTypesTable.setSelectionMode(Grid.SelectionMode.NONE);

        addComponent(passTypesTable);
//        setExpandRatio(passTypesTable, 1.0f);
        handler.showBadges(this);
    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showBadges(List<Badge> badges) {
        for (Badge badge : badges) {
            try {
                passTypesTable.addRow(badge.getName(),
                        badge.getCostForAge(35L),
                        badge.getCostForAge(17L),
                        badge.getCostForAge(11L),
                        badge.getCostForAge(4L));
            } catch (ServiceException e) {
                notifyError("Error getting age ranges for badge " + badge.getName());
            }
        }
        passTypesTable.setHeightByRows(badges.size());
    }
}