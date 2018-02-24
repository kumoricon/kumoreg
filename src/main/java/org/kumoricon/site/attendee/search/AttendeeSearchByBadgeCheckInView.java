package org.kumoricon.site.attendee.search;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.CheckInView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchByBadgeCheckInView.TEMPLATE)
public class AttendeeSearchByBadgeCheckInView extends CheckInView implements View {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "pre_reg_check_in";

    public static final String TEMPLATE = "attendeeSearchByBadge/{badgeType}/{attendeeId}/checkin";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected String searchString;

    @Autowired
    public AttendeeSearchByBadgeCheckInView(AttendeeSearchPresenter handler) {
        super(handler);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.searchString = map.get("badgeType");
        try {
            attendeeId = Integer.parseInt(map.get("attendeeId"));
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            close();
        }
        handler.showAttendee(this, attendeeId);
    }

    @Override
    protected void btnCheckInClicked() {
        handler.checkInAttendee(this, attendee);
        navigateTo(AttendeeSearchByBadgeView.VIEW_NAME + "/" + searchString);
    }


    @Override
    public void close() {
        if (attendeeId != null) {
            navigateTo(VIEW_NAME + "/" + searchString + "/" + attendeeId);
        } else {
            navigateTo(VIEW_NAME + "/" + searchString);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
