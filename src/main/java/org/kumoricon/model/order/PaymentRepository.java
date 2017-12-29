package org.kumoricon.model.order;

import org.kumoricon.model.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query(value = "select p from Payment p where p.session = ?1")
    Set<Payment> findAllInSession(Session session);

    @Query(value = "select sum(amount) from payments where session_id = ?1", nativeQuery = true)
    BigDecimal getTotalForSessionId(Integer id);

    @Query(value = "select sum(amount) from payments WHERE session_id = ?1 AND payment_type = ?2", nativeQuery=true)
    BigDecimal getTotalByPaymentTypeForSessionId(Integer id, Integer paymentType);

    List<Payment> findBySessionAndPaymentType(Session session, Payment.PaymentType paymentType);
}