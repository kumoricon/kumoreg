package org.kumoricon.site.attendee.reg;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.site.BaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.viritin.fields.MTable;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = OrderListView.VIEW_NAME)
public class OrderListView extends BaseView implements View {
    public static final String VIEW_NAME = "manageOrders";
    public static final String REQUIRED_RIGHT = "manage_orders";

    private static final Logger log = LoggerFactory.getLogger(OrderListView.class);

    // This is a simple view, combining the view and presenter as an experiment
    @Autowired
    private OrderRepository repository;

    private static final int PAGESIZE = 50;

    private MTable<Order> list = new MTable<>(Order.class)
            .withProperties("id", "orderId", "paid", "paymentTakenByUser", "paidAt")
            .withColumnHeaders("id", "Order ID", "Paid", "Payment Taken By", "Paid At")
            .setSortableProperties("id", "orderIdl", "paid", "paymenttakenByUser", "paidAt")
            .withFullWidth()
            .withFullHeight()
            .withRowClickListener(rowClick -> showOrder(rowClick.getRow()));

    @PostConstruct
    public void init() {
        addComponent(buildContent());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        log.info("{} viewed order list", getCurrentUser());

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

    private VerticalLayout buildContent() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setMargin(true);
        leftPanel.setSpacing(true);
        leftPanel.addComponent(list);

        return leftPanel;
    }


    public void showOrder(Order order) {
        navigateTo(OrderView.VIEW_NAME + "/" + order.getId());
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

