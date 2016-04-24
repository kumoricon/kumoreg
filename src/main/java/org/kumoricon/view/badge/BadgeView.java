package org.kumoricon.view.badge;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.presenter.badge.BadgePresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = BadgeView.VIEW_NAME)
public class BadgeView extends BaseView implements View {
    public static final String VIEW_NAME = "badges";
    public static final String REQUIRED_RIGHT = "manage_pass_types";

    @Autowired
    private BadgePresenter handler;

    private ListSelect badgeList = new ListSelect("Badges");
    private Button btnAddNew = new Button("Add");

    private BadgeEditWindow badgeEditWindow;

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
            closeBadgeEditWindow();
        } else {
            handler.navigateToRole(this, viewChangeEvent.getParameters());
        }
    }

    public void setHandler(BadgePresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<Badge> badges) {
        badgeList.setContainerDataSource(new BeanItemContainer<>(Badge.class, badges));
        badgeList.setRows(badges.size());
    }

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        badgeList.setCaption("Roles");
        badgeList.setNullSelectionAllowed(false);
        badgeList.setWidth(300, Unit.PIXELS);
        badgeList.setImmediate(true);
        badgeList.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        badgeList.setItemCaptionPropertyId("name");
        leftPanel.addComponent(badgeList);
        leftPanel.addComponent(btnAddNew);

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
        }
    }
    public void selectBadge(Badge role) { badgeList.select(role); }
    public void clearSelection() {
        badgeList.select(null);
    }


    public String getRequiredRight() { return REQUIRED_RIGHT; }
}


