package org.kumoricon.site.attendee.search;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.AttendeeDetailView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.springframework.web.util.UriTemplate;
import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchDetailView.TEMPLATE)
public class AttendeeSearchDetailView extends AttendeeDetailView implements View, AttendeePrintView, DetailFormHandler {
    public static final String VIEW_NAME = "attendeeSearch";
    public static final String REQUIRED_RIGHT = "attendee_search";

    public static final String TEMPLATE = "attendeeSearch/{searchString}/{attendeeId}";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected String searchString;


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.searchString = map.get("searchString");
        try {
            this.attendeeId = Integer.parseInt(map.get("attendeeId"));
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            return;
        }

        handler.showAttendee(this, attendeeId);
    }

    @Override
    public void close() {
        navigateTo(AttendeeSearchView.VIEW_NAME + "/" + searchString);
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
