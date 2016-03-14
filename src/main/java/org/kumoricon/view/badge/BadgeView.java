package org.kumoricon.view.badge;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.presenter.badge.BadgePresenter;
import org.kumoricon.util.FieldFactory;
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

    private TextField name = FieldFactory.createTextField("Name");

    private TextField dayText = FieldFactory.createTextField("Day Text");
    private TextField waringMessage = FieldFactory.createTextField("Warning message");
    private TextField requiredRight = FieldFactory.createDisabledTextField("Required Right");
    private CheckBox visible = new CheckBox("Visible");
    private Table tblAgeRanges;

    private Button btnAddNew = new Button("Add");
    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");

    private ListSelect badgeList = new ListSelect("Badges");

    private BeanFieldGroup<Badge> badgeBeanFieldGroup = new BeanFieldGroup<>(Badge.class);

    private Layout leftPanel;
    private Layout rightPanel;

    @PostConstruct
    public void init() {
        leftPanel = buildLeftPanel();
        rightPanel = buildRightPanel();
        addComponent(leftPanel);
        addComponent(rightPanel);

        handler.showBadgeList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            hideBadgeForm();
            clearBadgeForm();
        } else {
            handler.navigateToRole(this, viewChangeEvent.getParameters());
        }
    }

    public void setHandler(BadgePresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<Badge> badges) {
        badgeList.setContainerDataSource(new BeanItemContainer<>(Badge.class, badges));
    }

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        badgeList.setCaption("Roles");
        badgeList.setNullSelectionAllowed(false);
        badgeList.setWidth(300, Unit.PIXELS);
        badgeList.setHeight(150, Unit.PIXELS);
        badgeList.setImmediate(true);
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

    private FormLayout buildRightPanel() {
        FormLayout form = new FormLayout();
        form.setVisible(false);
        form.setMargin(true);
        form.setSpacing(true);

        form.addComponent(name);
        form.addComponent(dayText);
        form.addComponent(waringMessage);
        waringMessage.setDescription("Displayed when attendee with this badge type checks in");
        form.addComponent(requiredRight);
        requiredRight.setDescription("Only show to users with this security right, empty for all users");
        form.addComponent(visible);
        visible.setDescription("This badge type may be selected when checking in/editing attendees");

        badgeBeanFieldGroup.bind(name, "name");
        badgeBeanFieldGroup.bind(dayText, "dayText");
        badgeBeanFieldGroup.bind(waringMessage, "warningMessage");
        badgeBeanFieldGroup.bind(requiredRight, "requiredRight");
        badgeBeanFieldGroup.bind(visible, "visible");

        tblAgeRanges = new Table();
        tblAgeRanges.setCaption("Age Ranges");
        tblAgeRanges.setEditable(true);
        tblAgeRanges.setNullSelectionAllowed(false);
        tblAgeRanges.setPageLength(4);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                badgeBeanFieldGroup.commit();
                handler.saveBadge(this);
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelBadge(this));

        form.addComponent(tblAgeRanges);
        form.addComponent(buttons);

        return form;
    }

    public void clearBadgeForm() {
        name.clear();
        dayText.clear();
        visible.clear();
        tblAgeRanges.removeAllItems();
    }

    public void showBadge(Badge badge) {
        clearBadgeForm();
        showBadgeForm();
        badgeBeanFieldGroup.setItemDataSource(badge);
        BeanItemContainer<AgeRange> ageRanges = new BeanItemContainer<>(AgeRange.class);
        ageRanges.addAll(badge.getAgeRanges());
        tblAgeRanges.setContainerDataSource(ageRanges);
        tblAgeRanges.setVisibleColumns(new String[] { "name", "minAge", "maxAge", "cost", "stripeColor", "stripeText"});
        tblAgeRanges.setColumnHeaders("Name", "Minimum Age", "Maximum Age", "Cost", "Stripe Color", "Stripe Text");
    }

    public void hideBadgeForm() { rightPanel.setVisible(false); }
    public void showBadgeForm() { rightPanel.setVisible(true); }
    public void selectBadge(Badge role) { badgeList.select(role); }
    public void clearSelection() {
        badgeList.select(null);
    }

    public Badge getBadge() {
        BeanItem<Badge> badgeBean = badgeBeanFieldGroup.getItemDataSource();
        return badgeBean.getBean();
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}


