package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.service.validate.ValidationException;
import org.kumoricon.site.attendee.AttendeeDetailView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchByBadgeDetailView.TEMPLATE)
public class AttendeeSearchByBadgeDetailView extends AttendeeDetailView implements View, AttendeePrintView, DetailFormHandler {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "attendee_search";

    public static final String TEMPLATE = "attendeeSearchByBadge/{badgeType}/{attendeeId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected String searchString;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.searchString = map.get("badgeType");
        try {
            this.attendeeId = Integer.parseInt(map.get("attendeeId"));
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            return;
        }

        handler.showAttendee(this, attendeeId);
    }

    @Override
    protected void reprintClicked() {
        handler.saveAttendeeAndReprintBadge(this, form.getAttendee(), null);
        navigateTo(AttendeeSearchByBadgeRePrintBadgeView.VIEW_NAME + "/" + searchString + "/" + attendeeId + "/reprint");
    }
    
    @Override
    public void showAddNoteWindow() {
        Attendee attendee = form.getAttendee();
        if (handler.attendeeHasChanged(attendee)) {
            try {
                handler.saveAttendee(this, attendee);
                navigateTo(VIEW_NAME + "/" + searchString + "/" + attendeeId + "/note/new");
            } catch (ValidationException ex) {
                notifyError(ex.getMessage());
            }
        } else {
            if (attendee != null && attendee.getOrder() != null) {
                navigateTo(VIEW_NAME + "/" + searchString + "/" + attendeeId + "/note/new");
            }
        }

        navigateTo(VIEW_NAME + "/" + searchString + "/" + attendeeId + "/note/new");
    }

    @Override
    protected void showCheckInWindow() {
        try {
            handler.saveAttendee(this, form.getAttendee());
            navigateTo(VIEW_NAME + "/" + searchString + "/" + attendeeId + "/checkin");
        } catch (ValidationException ex) {
            notifyError(ex.getMessage());
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    @Override
    public void close() {
        navigateTo(SearchByBadgeView.VIEW_NAME + "/" + searchString);
    }
}
