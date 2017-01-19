package org.kumoricon.site.order;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.viritin.fields.MTable;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ManageOrderView.VIEW_NAME)
public class ManageOrderView extends BaseView implements View {
    public static final String VIEW_NAME = "manageOrders";
    public static final String REQUIRED_RIGHT = "manage_orders";

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ManageOrderPresenter handler;

    private static final int PAGESIZE = 50;

    private MTable<Order> list = new MTable<>(Order.class)
            .withProperties("id", "orderId", "paid", "paymentTakenByUser", "paidAt")
            .withColumnHeaders("id", "Order ID", "Paid", "Payment Taken By", "Paid At")
            .setSortableProperties("id", "orderIdl", "paid", "paymenttakenByUser", "paidAt")
            .withFullWidth()
            .withFullHeight()
            .withRowClickListener(rowClick -> handler.orderSelected(this, (Order)rowClick.getRow()));

    private OrderWindow orderEditWindow;

    @PostConstruct
    public void init() {
        addComponent(buildContent());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        String parameters = viewChangeEvent.getParameters();
        if (parameters == null || parameters.equals("")) {
            closeOrderEditWindow();
        } else {
            handler.navigateToOrder(this, viewChangeEvent.getParameters());
        }

        String likeFilter = "%" + "" + "%";
        list.lazyLoadFrom(
                         (firstRow, asc, sortProperty) -> repository.findByOrderIdLikeIgnoreCaseOrderByPaidAtDesc(
                                 likeFilter,
                                 new PageRequest(
                                         firstRow / PAGESIZE,
                                         PAGESIZE,
                                         asc ? Sort.Direction.ASC : Sort.Direction.DESC,
                                         sortProperty == null ? "id" : sortProperty
                                 )
                         ),
                         () -> (int) repository.countByOrderIdLikeOrderByPaidAtDesc(likeFilter),
                         PAGESIZE);
    }

    public void setHandler(ManageOrderPresenter presenter) {
        this.handler = presenter;
    }

    private VerticalLayout buildContent() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        leftPanel.addComponent(list);

        return leftPanel;
    }


    public void showOrder(Order order) {
        orderEditWindow = new OrderWindow();
        orderEditWindow.setSavedHandler(entity -> handler.saveOrder(this, (Order)entity));
        orderEditWindow.setDeleteHandler(entity -> handler.deleteOrder(this, (Order)entity));
        orderEditWindow.setResetHandler(entity -> handler.cancelOrder(this));
        orderEditWindow.setEntity(order);
        orderEditWindow.openInModalPopup();
    }

    public void closeOrderEditWindow() {
        if (orderEditWindow != null) {
            orderEditWindow.closePopup();
        }
    }

    public void selectOrder(Order order) { list.select(order); }
    public void clearSelection() {
        list.select(null);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    @Override
    public void refresh() {
        list.refreshRows();
    }
}

