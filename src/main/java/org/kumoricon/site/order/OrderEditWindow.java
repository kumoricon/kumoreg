package org.kumoricon.site.order;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.order.Order;


public class OrderEditWindow extends Window {


    private Button btnSave = new Button("Save");
    private Button btnCancel = new Button("Cancel");

    private BeanFieldGroup<Order> orderBeanFieldGroup = new BeanFieldGroup<>(Order.class);

    private ManageOrderView parentView;

    public OrderEditWindow(ManageOrderView parentView) {
        super("Order");
        this.parentView = parentView;
        setIcon(FontAwesome.REORDER);
        center();
        setModal(true);
        setResizable(false);

        VerticalLayout verticalLayout = new VerticalLayout();


        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnSave);
        buttons.addComponent(btnCancel);

        btnSave.addClickListener((Button.ClickListener) clickEvent -> {
            try {
                orderBeanFieldGroup.commit();
//                handler.saveOrder(parentView, getBadge());
            } catch (Exception e) {
                parentView.notifyError(e.getMessage());
            }
        });

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> close());


        verticalLayout.addComponent(buttons);
        setContent(verticalLayout);

        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public Order getOrder() {
        BeanItem<Order> orderBean = orderBeanFieldGroup.getItemDataSource();
        return orderBean.getBean();
    }

    public void showOrder(Order order) {
//        badgeBeanFieldGroup.setItemDataSource(badge);
//        BeanItemContainer<AgeRange> ageRanges = new BeanItemContainer<>(AgeRange.class);
//        ageRanges.addAll(badge.getAgeRanges());
//        tblAgeRanges.setContainerDataSource(ageRanges);
//        tblAgeRanges.setVisibleColumns("name", "minAge", "maxAge", "cost", "stripeColor", "stripeText");
//        tblAgeRanges.setColumnHeaders("Name", "Minimum Age", "Maximum Age", "Cost", "Stripe Color", "Stripe Text");
    }
}
