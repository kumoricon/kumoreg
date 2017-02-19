package org.kumoricon.model.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByOrderIdStartsWithIgnoreCase(String orderId);
    Order findOneByOrderIdIgnoreCase(String orderId);

//    @Query(value = "SELECT paymentType, COUNT(id) as cnt, FROM orders WHERE paymentTakenByUser_id = ?1 AND paidSession = ?2 GROUP BY paymentType;", nativeQuery = true)
//    List<Object[]> getSessionOrderCountsAndTotals(Integer userId, Integer sessionNumber);
//
//    @Query(value = "SELECT sessions.uid as userid, username, firstName, lastName, sessions.paidSession, start, finish, paymentType, ordercnt, total FROM\n" +
//            "  (\n" +
//            "    SELECT users.id as uid, orders.paidSession, MIN(paidAt) as start, MAX(paidAt) as finish\n" +
//            "    from orders JOIN users ON orders.paymentTakenByUser_id = users.id\n" +
//            "    WHERE paid = TRUE AND\n" +
//            "          users.sessionNumber != orders.paidSession\n" +
//            "    GROUP BY paymentTakenByUser_id, paidSession\n" +
//            "  ) as sessions\n" +
//            "JOIN\n" +
//            "  (\n" +
//            "    SELECT users.id as uid, users.username, users.firstName, users.lastName, paidSession, paymentType, COUNT(orders.id) as ordercnt, SUM(totalAmount) as total\n" +
//            "    FROM orders, users\n" +
//            "    WHERE paymentTakenByUser_id IS NOT NULL\n" +
//            "    AND paymentTakenByUser_id = users.id\n" +
//            "    GROUP BY paymentTakenByUser_id, paidSession, paymentType\n" +
//            "  ) as totals\n" +
//            "ON\n" +
//            "    sessions.uid = totals.uid AND sessions.paidSession = totals.paidSession\n" +
//            "ORDER BY start, userid, paymentType;", nativeQuery = true)
//    List<Object[]> getAllOrderCountsAndTotals();
//
//    @Query(value = "SELECT SUM(totalAmount) as total FROM orders WHERE paymentTakenByUser_id = ?1 AND paidSession = ?2 ;", nativeQuery = true)
//    Float getSessionOrderTotal(Integer userId, Integer sessionNumber);

    List<Order> findAllBy(Pageable pageable);

    List<Order> findByOrderIdLikeIgnoreCaseOrderByIdDesc(String orderIdFilter);

    List<Order> findByOrderIdLikeIgnoreCaseOrderByIdDesc(String orderIdFilter, Pageable pageable);

    long countByOrderIdLikeOrderByIdDesc(String orderIdFilter);

}