package org.kumoricon.model.session;

import org.kumoricon.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Unlike most repositories, Session access should only happen through SessionService
 */
interface SessionRepository extends JpaRepository<Session, Integer> {

    @Query(value = "select s from Session s where s.user = ?1 AND s.open = true")
    Session getOpenSessionForUser(User user);

    @Query(value = "select s from Session s where s.open = true")
    List<Session> findAllOpenSessions();

    @Query(value = "select s from Session s ORDER BY s.end desc")
    List<Session> findAllOrderByEnd();
}
