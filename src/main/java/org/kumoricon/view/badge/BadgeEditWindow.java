package org.kumoricon.view.badge;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.presenter.badge.BadgePresenter;
import org.kumoricon.util.FieldFactory;

public class BadgeEditWindow extends Window {

    private TextField name = FieldFactory.createTextField("Name");

    private TextField dayText = FieldFactory.createTextField("Day Text");
    private TextField waringMessage = FieldFactory.createTextField("Warning message");
    private TextField requiredRight = FieldFactory.createDisabledTextField("Required Right");
    private CheckBox visible = new CheckBox("Visible");
    private Table tblAgeRanges;

    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");

    private BeanFieldGroup<Badge> badgeBeanFieldGroup = new BeanFieldGroup<>(Badge.class);

    private Label deleteNote = new Label("Note: badges can not be deleted once created. Un-check Visible instead.");

    private BadgePresenter handler;
    private BadgeView parentView;

    public BadgeEditWindow(BadgeView parentView, BadgePresenter badgePresenter) {
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
        setContent(verticalLayout);
    }

    public void clearBadgeForm() {
        name.clear();
        dayText.clear();
        visible.clear();
        tblAgeRanges.removeAllItems();
    }

    public Badge getBadge() {
        BeanItem<Badge> badgeBean = badgeBeanFieldGroup.getItemDataSource();
        return badgeBean.getBean();
    }

    public void showBadge(Badge badge) {
        badgeBeanFieldGroup.setItemDataSource(badge);
        BeanItemContainer<AgeRange> ageRanges = new BeanItemContainer<>(AgeRange.class);
        ageRanges.addAll(badge.getAgeRanges());
        tblAgeRanges.setContainerDataSource(ageRanges);
        tblAgeRanges.setVisibleColumns(new String[] { "name", "minAge", "maxAge", "cost", "stripeColor", "stripeText"});
        tblAgeRanges.setColumnHeaders("Name", "Minimum Age", "Maximum Age", "Cost", "Stripe Color", "Stripe Text");
    }

    public BadgePresenter getHandler() { return handler; }
    public void setHandler(BadgePresenter handler) { this.handler = handler; }
}
