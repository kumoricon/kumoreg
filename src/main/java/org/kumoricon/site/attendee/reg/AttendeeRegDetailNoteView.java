package org.kumoricon.site.attendee.reg;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.NoteView;
import org.kumoricon.site.attendee.search.AttendeeHistoryPresenter;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeRegDetailNoteView.TEMPLATE)
public class AttendeeRegDetailNoteView extends NoteView implements View {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "attendee_search";

    public static final String TEMPLATE = "order/{orderId}/{attendeeId}/note/{noteId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected Integer orderId;
    protected String noteId;

    public AttendeeRegDetailNoteView(AttendeeHistoryPresenter handler) {
        super(handler);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        try {
            orderId = Integer.parseInt(map.get("orderId"));
            attendeeId = Integer.parseInt(map.get("attendeeId"));
            noteId = map.get("noteId");
        } catch (NumberFormatException ex) {
            notifyError("Bad order or attendee id: must be an integer");
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
            navigateTo(VIEW_NAME + "/" + orderId + "/" + attendeeId);
        } else {
            navigateTo(VIEW_NAME + "/" + orderId);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
