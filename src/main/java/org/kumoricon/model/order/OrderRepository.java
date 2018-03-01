package org.kumoricon.model.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByOrderIdStartsWithIgnoreCase(String orderId);
    Order findOneByOrderIdIgnoreCase(String orderId);

    List<Order> findAllBy(Pageable pageable);

    List<Order> findByOrderIdLikeIgnoreCaseOrderByIdDesc(String orderIdFilter);

    List<Order> findByOrderIdLikeIgnoreCaseOrderByIdDesc(String orderIdFilter, Pageable pageable);

    long countByOrderIdLikeOrderByIdDesc(String orderIdFilter);
}