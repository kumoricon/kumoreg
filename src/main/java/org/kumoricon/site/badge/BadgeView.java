package org.kumoricon.site.badge;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = BadgeView.VIEW_NAME)
public class BadgeView extends BaseView implements View {
    public static final String VIEW_NAME = "badges";
    public static final String REQUIRED_RIGHT = "manage_pass_types";

    private final BadgePresenter handler;

    private final Table badgeList = new Table("Badges");
    private final Button btnAddNew = new Button("Add");

    private BadgeEditWindow badgeEditWindow;

    @Autowired
    public BadgeView(BadgePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        Layout leftPanel = buildLeftPanel();
        addComponent(leftPanel);
        handler.showBadgeList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            if (badgeEditWindow != null) {
                badgeEditWindow.close();
            }
        } else {
            handler.navigateToBadge(this, viewChangeEvent.getParameters());
        }
    }

    public void afterSuccessfulFetch(List<Badge> badges) {
        Object[] sortBy = {badgeList.getSortContainerPropertyId()};
        boolean[] sortOrder = {badgeList.isSortAscending()};
        badgeList.setContainerDataSource(new BeanItemContainer<>(Badge.class, badges));
        badgeList.setVisibleColumns("name", "badgeTypeBackgroundColor", "badgeType", "requiredRight", "visible");
        badgeList.setColumnHeaders("Name", "Stripe Color", "Badge Type", "Required Security Right", "Visible");
        badgeList.sort(sortBy, sortOrder);
    }

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        badgeList.setCaption("Badge Types");
        badgeList.setNullSelectionAllowed(false);
        badgeList.setMultiSelect(false);
        badgeList.setImmediate(true);
        badgeList.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badgeList.setItemCaptionPropertyId("name");
        leftPanel.addComponent(btnAddNew);
        leftPanel.addComponent(badgeList);

        badgeList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                handler.badgeSelected(this, (Badge)valueChangeEvent.getProperty().getValue()));

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            badgeList.select(null);
            handler.addNewBadge(this);
        });
        return leftPanel;
    }


    public void showBadge(Badge badge) {
        badgeEditWindow = new BadgeEditWindow(this, handler);
        badgeEditWindow.showBadge(badge);
        showWindow(badgeEditWindow);
    }

    public void closeBadgeEditWindow() {
        if (badgeEditWindow != null) {
            badgeEditWindow.close();
            navigateTo(BadgeView.VIEW_NAME);
        }
    }
    public void selectBadge(Badge role) { badgeList.select(role); }
    public void clearSelection() {
        badgeList.select(null);
    }


    public String getRequiredRight() { return REQUIRED_RIGHT; }
}


