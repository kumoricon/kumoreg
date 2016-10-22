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

    @Query(value = "SELECT users.id, users.username, users.firstName, users.lastName, paidSession, MIN(paidAt) as sessionStart, MAX(paidAt) as sessionEnd, paymentType, COUNT(orders.id) as cnt, SUM(totalAmount) as total FROM orders, users WHERE paymentTakenByUser_id IS NOT NULL AND paymentTakenByUser_id = users.id GROUP BY paymentTakenByUser_id, paidSession, paymentType;", nativeQuery = true)
    List<Object[]> getAllOrderCountsAndTotals();

    @Query(value = "SELECT SUM(totalAmount) as total FROM orders WHERE paymentTakenByUser_id = ?1 AND paidSession = ?2 ;", nativeQuery = true)
    Float getSessionOrderTotal(Integer userId, Integer sessionNumber);

}