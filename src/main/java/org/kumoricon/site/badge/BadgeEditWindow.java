package org.kumoricon.site.badge;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.*;
import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeType;
import org.kumoricon.site.attendee.FieldFactory;


class BadgeEditWindow extends Window {

    private final TextField name = FieldFactory.createTextField("Name");

    private final TextField badgeTypeText = FieldFactory.createTextField("Badge Type Text");
    private final TextField badgeTypeBackgroundColor = FieldFactory.createTextField("Badge Type Background Color");
    private final TextField waringMessage = FieldFactory.createTextField("Warning message");
    private final TextField requiredRight = FieldFactory.createDisabledTextField("Required Right");
    private final CheckBox visible = new CheckBox("Visible");
    private final NativeSelect badgeType = new NativeSelect("Badge Type");
    private final Table tblAgeRanges;

    private final Button btnSave = new Button("Save");
    private final Button btnCancel = new Button("Cancel");

    private final BeanFieldGroup<Badge> badgeBeanFieldGroup = new BeanFieldGroup<>(Badge.class);

    private final Label deleteNote = new Label("Note: badges can not be deleted once created. Un-check Visible instead.");

    private final BadgePresenter handler;
    private final BadgeView parentView;

    BadgeEditWindow(BadgeView parentView, BadgePresenter badgePresenter) {
        super("Badge");
        this.handler = badgePresenter;
        this.parentView = parentView;
        setIcon(FontAwesome.BARCODE);
        center();
        setModal(true);
        setResizable(false);

        VerticalLayout verticalLayout = new VerticalLayout();

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);
        form.setSizeFull();

        form.addComponent(name);
        form.addComponent(badgeType);
        badgeType.addItems(BadgeType.ATTENDEE, BadgeType.STAFF, BadgeType.OTHER);
        form.addComponent(badgeTypeText);
        form.addComponent(badgeTypeBackgroundColor);
        form.addComponent(waringMessage);
        waringMessage.setWidth(70, Unit.EM);
        waringMessage.setDescription("Displayed when attendee with this badge type checks in");
        form.addComponent(requiredRight);
        requiredRight.setDescription("Only show to users with this security right, empty for all users");
        form.addComponent(visible);
        visible.setDescription("This badge type may be selected when checking in/editing attendees");

        badgeBeanFieldGroup.bind(name, "name");
        badgeBeanFieldGroup.bind(badgeType, "badgeType");
        badgeBeanFieldGroup.bind(badgeTypeText, "badgeTypeText");
        badgeBeanFieldGroup.bind(badgeTypeBackgroundColor, "badgeTypeBackgroundColor");
        badgeBeanFieldGroup.bind(waringMessage, "warningMessage");
        badgeBeanFieldGroup.bind(requiredRight, "requiredRight");
        badgeBeanFieldGroup.bind(visible, "visible");

        tblAgeRanges = new Table();
        tblAgeRanges.setCaption("Age Ranges");
        tblAgeRanges.setEditable(true);
        tblAgeRanges.setNullSelectionAllowed(false);
        tblAgeRanges.setPageLength(4);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                badgeBeanFieldGroup.commit();
                handler.saveBadge(parentView, getBadge());
            } catch (Exception e) {
                parentView.notifyError(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> handler.cancelBadge(parentView));

        form.addComponent(tblAgeRanges);
        form.addComponent(buttons);

        verticalLayout.addComponent(form);
        verticalLayout.addComponent(deleteNote);
        setContent(verticalLayout);

        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    private Badge getBadge() {
        BeanItem<Badge> badgeBean = badgeBeanFieldGroup.getItemDataSource();
        return badgeBean.getBean();
    }

    void showBadge(Badge badge) {
        badgeBeanFieldGroup.setItemDataSource(badge);
        BeanItemContainer<AgeRange> ageRanges = new BeanItemContainer<>(AgeRange.class);
        ageRanges.addAll(badge.getAgeRanges());
        tblAgeRanges.setContainerDataSource(ageRanges);
        tblAgeRanges.setVisibleColumns("name", "minAge", "maxAge", "cost", "stripeColor", "stripeText");
        tblAgeRanges.setColumnHeaders("Name", "Minimum Age", "Maximum Age", "Cost", "Stripe Color", "Stripe Text");
    }
}
