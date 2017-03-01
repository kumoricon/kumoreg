package org.kumoricon.service;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.attendee.AttendeeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttendeeSearchService {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeSearchRepository searchRepository;

    @Autowired
    public AttendeeSearchService(AttendeeRepository attendeeRepository, AttendeeSearchRepository searchRepository) {
        this.attendeeRepository = attendeeRepository;
        this.searchRepository = searchRepository;
    }

    /**
     * Search for attendees matching the given string.
     * @param searchString Search string, may contain multiple words separated by space
     * @return Matching Attendees
     */
    public List<Attendee> search(String searchString) {
        if (searchString == null || searchString.trim().equals("")) {
            return new ArrayList<>();
        }
        String searchFor = searchString.trim();
        String[] searchWords = searchFor.split("\\s+");
        List<Attendee> output;

        // If searching for a single word, do an exact match on badge number or Order ID first
        // and only return that if there are any matches
        if (searchWords.length == 1) {
            output = attendeeRepository.findByBadgeNumberOrOrderId(searchFor);
            if (!output.isEmpty()) return output;
        }

        // Otherwise search for all the words in the search string across multiple fields
        output = searchRepository.searchFor(searchWords);
        return output;
    }

}
