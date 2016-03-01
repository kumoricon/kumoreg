package org.kumoricon.model.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByOrderIdStartsWithIgnoreCase(String orderId);
    Order findOneByOrderIdIgnoreCase(String orderId);

    /* Todo: get order totals by type for a given session and user ID. Something like:
    SELECT payment_type, SUM(total_amount) as total FROM orders
    WHERE paid_session = 1 AND payment_taken_by_user_id = 1
    GROUP BY payment_type;
     */
}