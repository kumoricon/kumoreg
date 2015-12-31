package org.kumoricon.model.attendee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
    List<Attendee> findByLastNameStartsWithIgnoreCase(String lastName);
}