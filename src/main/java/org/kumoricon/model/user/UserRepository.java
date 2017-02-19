package org.kumoricon.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByLastNameStartsWithIgnoreCase(String lastName);
    User findOneByUsernameIgnoreCase(String username);
}
