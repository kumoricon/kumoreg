package org.kumoricon.site.order;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.model.order.Order;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewScope
@SpringView(name = ManageOrderView.VIEW_NAME)
public class ManageOrderView extends BaseView implements View {
    public static final String VIEW_NAME = "manageOrders";
    public static final String REQUIRED_RIGHT = "manage_orders";

    @Autowired
    private ManageOrderPresenter handler;

    private ListSelect orderList = new ListSelect("Orders");

    private OrderEditWindow orderEditWindow;

    @PostConstruct
    public void init() {
        Layout leftPanel = buildLeftPanel();
        addComponent(leftPanel);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            closeOrderEditWindow();
            handler.showOrderList(this);
        } else {
            handler.navigateToOrder(this, viewChangeEvent.getParameters());
        }
    }

    public void setHandler(ManageOrderPresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<Order> orders) {
        orderList.setContainerDataSource(new BeanItemContainer<>(Order.class, orders));
        orderList.setRows(orders.size());
    }

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        orderList.setCaption("Orders");
        orderList.setNullSelectionAllowed(false);
        orderList.setWidth(300, Unit.PIXELS);
        orderList.setImmediate(true);
        orderList.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        orderList.setItemCaptionPropertyId("orderId");
        leftPanel.addComponent(orderList);

        orderList.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent ->
                handler.orderSelected(this, (Order)valueChangeEvent.getProperty().getValue()));

        return leftPanel;
    }


    public void showOrder(Order order) {
        orderEditWindow = new OrderEditWindow(this);
        orderEditWindow.showOrder(order);
        showWindow(orderEditWindow);
    }

    public void closeOrderEditWindow() {
        if (orderEditWindow != null) {
            orderEditWindow.close();
        }
    }
    public void selectOrder(Order order) { orderList.select(order); }
    public void clearSelection() {
        orderList.select(null);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}

