package org.kumoricon.model.blacklist;

import org.kumoricon.model.attendee.Attendee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlacklistService {
    private final BlacklistRepository repository;

    private static final Logger log = LoggerFactory.getLogger(BlacklistService.class);

    @Autowired
    public BlacklistService(BlacklistRepository repository) {this.repository = repository;}

    /**
     * Check if the given person is on the blacklist. Currently only
     * does simple match on first and last name, must be exact.
     * @param firstName First Name, not null
     * @param lastName Last Name, not null
     * @return match True if there is a match
     */
    public boolean isOnBlacklist(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            log.warn("Checked blacklist for null name");
            return false;
        }
        return repository.numberOfMatches(firstName, lastName) > 0;
    }

    public boolean isOnBlacklist(Attendee attendee) {
        if (attendee != null) {
            return isOnBlacklist(attendee.getFirstName(), attendee.getLastName());
        } else {
            throw new RuntimeException("Blacklist was checked for a null Attendee");
        }
    }
}
