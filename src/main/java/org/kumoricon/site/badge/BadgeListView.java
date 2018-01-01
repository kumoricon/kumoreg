package org.kumoricon.site.badge;

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.ItemClickListener;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = BadgeListView.VIEW_NAME)
public class BadgeListView extends BaseView implements View {
    public static final String VIEW_NAME = "badges";
    public static final String REQUIRED_RIGHT = "manage_pass_types";

    private final BadgePresenter handler;

    private final Grid<Badge> badgeGrid = new Grid<>();
    private final Button btnAddNew = new Button("Add New");


    @Autowired
    public BadgeListView(BadgePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        badgeGrid.setWidth("800px");
        badgeGrid.setHeightMode(HeightMode.ROW);
        badgeGrid.setSelectionMode(Grid.SelectionMode.NONE);
        badgeGrid.addStyleName("kumoHandPointer");
        addComponent(badgeGrid);
        addComponent(btnAddNew);

        badgeGrid.addItemClickListener((ItemClickListener<Badge>) itemClick -> navigateTo(BadgeEditView.VIEW_NAME + "/" + itemClick.getItem().getId()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            navigateTo(BadgeEditView.VIEW_NAME + "/");
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.trim().equals("")) {
            handler.showBadgeList(this);
        } else {
            navigateTo(BadgeEditView.VIEW_NAME + "/" + parameters);
        }
    }

    public void afterSuccessfulFetch(List<Badge> badges) {
        List<GridSortOrder<Badge>> sortBy = badgeGrid.getSortOrder();

        badgeGrid.addColumn(Badge::getName).setCaption("Name");
        badgeGrid.addColumn(Badge::getBadgeTypeBackgroundColor).setCaption("Background Color");
        badgeGrid.addColumn(Badge::getBadgeType).setCaption("Type");
        badgeGrid.addColumn(Badge::getRequiredRight).setCaption("Required Right");
        badgeGrid.addColumn(Badge::isVisible).setCaption("Visible");
        badgeGrid.setItems(badges);
        badgeGrid.setHeightByRows(badges.size());
        badgeGrid.setSortOrder(sortBy);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}


