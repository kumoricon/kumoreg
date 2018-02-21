package org.kumoricon.site.attendee.search;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.site.attendee.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AttendeeHistoryPresenter {
    private AttendeeRepository attendeeRepository;

    private AttendeeHistoryRepository attendeeHistoryRepository;

    private static final Logger log = LoggerFactory.getLogger(AttendeeHistoryPresenter.class);

    @Autowired
    public AttendeeHistoryPresenter(AttendeeRepository attendeeRepository,
                                    AttendeeHistoryRepository attendeeHistoryRepository) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }


    public void addNote(NoteView view, Integer attendeeId, String message) {
        Attendee attendee = attendeeRepository.findOne(attendeeId);
        log.info("{} added note \"{}\" to {}",
                view.getCurrentUsername(),
                message.replaceAll("([\\r\\n])+", " "),
                attendee);
        AttendeeHistory ah = new AttendeeHistory(view.getCurrentUser(), attendee, message);
        attendeeHistoryRepository.save(ah);
    }

    public void showNote(NoteView view, Integer noteId) {
        log.info("{} viewed note {}", view.getCurrentUsername(), noteId);
        AttendeeHistory ah = attendeeHistoryRepository.findOne(noteId);
        view.showNote(ah);
    }
}
