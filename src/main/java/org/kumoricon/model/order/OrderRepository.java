package org.kumoricon.model.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByOrderIdStartsWithIgnoreCase(String orderId);
    Order findOneByOrderIdIgnoreCase(String orderId);

    @Query(value = "SELECT paymentType, COUNT(id) as cnt, SUM(totalAmount) as total FROM orders WHERE paymentTakenByUser_id = ?1 AND paidSession = ?2 GROUP BY paymentType;", nativeQuery = true)
    List<Object[]> getSessionOrderCountsAndTotals(Integer userId, Integer sessionNumber);

}