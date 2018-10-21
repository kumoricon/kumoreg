package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.NoteView;
import org.kumoricon.site.attendee.search.AttendeeHistoryPresenter;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchByBadgeNoteView.TEMPLATE)
public class AttendeeSearchByBadgeNoteView extends NoteView implements View {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "attendee_search";

    public static final String TEMPLATE = "attendeeSearchByBadge/{badgeType}/{attendeeId}/note/{noteId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected String badgeType;
    protected String noteId;

    public AttendeeSearchByBadgeNoteView(AttendeeHistoryPresenter handler) {
        super(handler);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.badgeType = map.get("badgeType");
        try {
            attendeeId = Integer.parseInt(map.get("attendeeId"));
            noteId = map.get("noteId");
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            close();
        }
        if (noteId == null || noteId.trim().equals("") || noteId.toLowerCase().equals("new")) {
            showOnlyAddControls(true);
        } else {
            showOnlyAddControls(false);
            try {
                Integer noteIdNumber = Integer.parseInt(noteId);
                handler.showNote(this, noteIdNumber);
            } catch (NumberFormatException ex) {
                notifyError("Bad Note id: must be integer");
            }
        }
    }

    @Override
    protected void saveClicked(String message) {
        handler.addNote(this, attendeeId, message);
        close();
    }

    @Override
    public void close() {
        if (attendeeId != null) {
            navigateTo(AttendeeSearchByBadgeDetailView.VIEW_NAME + "/" + badgeType + "/" + attendeeId);
        } else {
            navigateTo(AttendeeSearchByBadgeDetailView.VIEW_NAME + "/" + badgeType);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
