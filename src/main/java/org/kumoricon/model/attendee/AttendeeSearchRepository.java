package org.kumoricon.model.attendee;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AttendeeSearchRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Searches for Attendees that contain all the given words in the firstName,
     * lastName, or BadgeName fields, case insensitive
     * @param searchWords Words to search for
     * @return Matching Attendees
     */
    public List<Attendee> searchFor(String[] searchWords) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Attendee> query = builder.createQuery(Attendee.class);
        Root<Attendee> root = query.from(Attendee.class);

        List<Predicate> predicates = new ArrayList<>();

        for (String word : searchWords) {
            predicates.add(buildOrPredicatesForWord(builder, root, word));
        }

        Predicate allPredicates = builder.and(predicates.toArray(new Predicate[predicates.size()]));
        query.where(allPredicates);
        return em.createQuery(query.select(root)).getResultList();
    }

    /**
     * Creates a query predicate to search for the given word
     * @param builder Current CriteriaBuilder
     * @param root Root Attendee class
     * @param word Word to search for
     * @return Predicate word in firstName OR lastName OR badgeName
     */
    private Predicate buildOrPredicatesForWord(CriteriaBuilder builder, Root<Attendee> root, String word) {
        Predicate hasFirstName = builder.like(root.get("firstName"), "%" + word + "%");
        Predicate hasLastName = builder.like(root.get("lastName"), "%" + word + "%");
        Predicate hasBadgeName = builder.like(root.get("badgeName"), "%" + word + "%");
        Predicate row = builder.or(hasFirstName, hasLastName, hasBadgeName);
        return row;
    }
}
