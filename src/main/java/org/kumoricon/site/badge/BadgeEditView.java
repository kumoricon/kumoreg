package org.kumoricon.site.badge;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeType;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ViewScope
@SpringView(name = BadgeEditView.VIEW_NAME)
public class BadgeEditView extends BaseView implements View {
    public static final String VIEW_NAME = "badge";
    public static final String REQUIRED_RIGHT = "manage_pass_types";

    private final TextField name = new TextField("Name");
    private final TextField badgeTypeText = new TextField("Badge Type Text");
    private final TextField badgeTypeBackgroundColor = new TextField("Background Color");
    private final TextField waringMessage = new TextField("Warning message");
    private final TextField requiredRight = new TextField("Required Right");
    private final CheckBox visible = new CheckBox("Visible");
    private final NativeSelect badgeType = new NativeSelect("Badge Type");

    private final Grid<AgeRange> ageRangeGrid = new Grid<>("");

    private final Binder<Badge> binder = new Binder<>();

    private final Button btnSave = new Button("Save");
    private final Button btnCancel = new Button("Cancel");

    private final Label deleteNote = new Label("Note: badges can not be deleted once created. Un-check Visible instead.");

    private final BadgePresenter handler;

    @Autowired
    public BadgeEditView(BadgePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        FormLayout form = new FormLayout();
        form.setWidth("800px");
        form.addComponents(name, badgeType, badgeTypeText, badgeTypeBackgroundColor, waringMessage, requiredRight, visible,
                 deleteNote);
        badgeType.setItems(BadgeType.values());
        badgeType.setEmptySelectionAllowed(false);
        waringMessage.setWidth("400px");
        requiredRight.setWidth("300px");
        ageRangeGrid.setHeightMode(HeightMode.ROW);
        ageRangeGrid.setHeightByRows(4);
        ageRangeGrid.setWidth("800px");
        ageRangeGrid.setSelectionMode(Grid.SelectionMode.NONE);
        ageRangeGrid.getEditor().setEnabled(true);
        ageRangeGrid.addStyleName("kumoHandPointer");

        binder.bind(name, Badge::getName, Badge::setName);
        binder.bind(badgeType, Badge::getBadgeType, Badge::setBadgeType);
        binder.bind(badgeTypeText, Badge::getBadgeTypeText, Badge::setBadgeTypeText);
        binder.bind(badgeTypeBackgroundColor, Badge::getBadgeTypeBackgroundColor, Badge::setBadgeTypeBackgroundColor);
        binder.bind(waringMessage, Badge::getWarningMessage, Badge::setWarningMessage);
        binder.bind(requiredRight, Badge::getRequiredRight, Badge::setRequiredRight);
        binder.bind(visible, Badge::isVisible, Badge::setVisible);
        VerticalLayout buttons = new VerticalLayout();
        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            Badge badge = binder.getBean();
            DataProvider<AgeRange, ?> dataProvider = ageRangeGrid.getDataProvider();

            Stream<AgeRange> streamEntities = dataProvider.fetch(new Query<>());
            List<AgeRange> ageRanges = streamEntities.collect(Collectors.toList());
            badge.setAgeRanges(ageRanges);
            handler.saveBadge(this, badge);
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> navigateTo(BadgeListView.VIEW_NAME));

        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnCancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);

        buttons.addComponents(btnSave, btnCancel);
        buttons.setWidth("150px");
        addComponents(form, buttons, ageRangeGrid);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.trim().equals("")) {
            Integer badgeId;
            try {
                badgeId = Integer.parseInt(parameters);
                handler.showBadge(this, badgeId);
            } catch (NumberFormatException ex) {
                notifyError("Badge ID must be integer: " + parameters);
            }
        } else {
            // Create new badge type
            handler.showBadge(this, null);
        }
    }

    public void afterSuccessfulFetch(Badge badge) {
        binder.setBean(badge);

        Binder<AgeRange> ageRangeBinder = ageRangeGrid.getEditor().getBinder();
        TextField ageRangeCost = new TextField();
        TextField stripeColor = new TextField();
        TextField stripeText = new TextField();


        Binder.Binding<AgeRange, BigDecimal> costBinding = ageRangeBinder.forField(ageRangeCost)
                .withConverter(value -> value.isEmpty() ? new BigDecimal("0.00") : new BigDecimal(value),
                        value -> value == null ? "" : value.toString(),
                        "Price must be in ##.## format")
                .bind(AgeRange::getCost, AgeRange::setCost);

        // Don't allow editing of name, minage, or maxage because there's no way to add/remove
        // age ranges at run time. Lots of things assume that there are four of them:
        // Adult, Youth, Child, 5 or Under
//        Binder.Binding<AgeRange, Integer> minAgeBinding = ageRangeBinder.forField(minAge)
//                .withConverter(value -> value.isEmpty() ? new Integer(0) : new Integer(value),
//                        value -> value == null ? "" : value.toString(),
//                        "Minimum Age must be an integer > 0")
//                .bind(AgeRange::getMinAge, AgeRange::setMinAge);
//
//        Binder.Binding<AgeRange, Integer> maxAgeBinding = ageRangeBinder.forField(maxAge)
//                .withConverter(value -> value.isEmpty() ? new Integer(0) : new Integer(value),
//                        value -> value == null ? "" : value.toString(),
//                        "Maxiumum Age must be an integer <= 255")
//                .bind(AgeRange::getMaxAge, AgeRange::setMaxAge);

        ageRangeGrid.setItems(badge.getAgeRanges());
        ageRangeGrid.addColumn(AgeRange::getName).setCaption("Age Range");
        ageRangeGrid.addColumn(AgeRange::getMinAge).setCaption("Minimum Age");
        ageRangeGrid.addColumn(AgeRange::getMaxAge).setCaption("Maximum Age");
        ageRangeGrid.addColumn(AgeRange::getCost).setCaption("Cost").setEditorBinding(costBinding);
        ageRangeGrid.addColumn(AgeRange::getStripeColor).setCaption("Stripe Color").setEditorComponent(stripeColor, AgeRange::setStripeColor);
        ageRangeGrid.addColumn(AgeRange::getStripeText).setCaption("Stripe Text").setEditorComponent(stripeText, AgeRange::setStripeText);

        name.focus();
    }


    public String getRequiredRight() { return REQUIRED_RIGHT; }
}


