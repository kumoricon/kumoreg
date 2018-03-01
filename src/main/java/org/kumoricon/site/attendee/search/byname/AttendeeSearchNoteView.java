package org.kumoricon.site.attendee.search.byname;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.NoteView;
import org.kumoricon.site.attendee.search.AttendeeHistoryPresenter;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchNoteView.TEMPLATE)
public class AttendeeSearchNoteView extends NoteView implements View {
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "attendee_search";

    public static final String TEMPLATE = "attendeeSearch/{searchString}/{attendeeId}/note/{noteId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected String searchString;
    protected String noteId;

    public AttendeeSearchNoteView(AttendeeHistoryPresenter handler) {
        super(handler);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.searchString = map.get("searchString");
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
            navigateTo(AttendeeSearchView.VIEW_NAME + "/" + searchString + "/" + attendeeId);
        } else {
            navigateTo(AttendeeSearchView.VIEW_NAME + "/" + searchString);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
