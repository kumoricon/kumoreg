package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.site.attendee.PrintBadgeView;
import org.kumoricon.site.attendee.search.byname.SearchByNameView;
import org.kumoricon.site.attendee.search.PrintBadgePresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchByBadgePrintBadgeView.TEMPLATE)
public class AttendeeSearchByBadgePrintBadgeView extends PrintBadgeView implements View {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "print_badge";

    public static final String TEMPLATE = "attendeeSearchByBadge/{badgeType}/{attendeeId}/badge";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);

    protected Integer attendeeId;
    protected String badgeType;

    @Autowired
    public AttendeeSearchByBadgePrintBadgeView(PrintBadgePresenter handler) {
        super(handler);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        Map<String, String> map = URI_TEMPLATE.match(viewChangeEvent.getViewName());

        this.badgeType = map.get("badgeType");
        try {
            attendeeId = Integer.parseInt(map.get("attendeeId"));
        } catch (NumberFormatException ex) {
            notifyError("Bad attendee id: must be an integer");
            close();
        }
        handler.showAttendee(this, attendeeId);
    }



    @Override
    public void close() {
        if (attendeeId != null) {
            navigateTo(SearchByNameView.VIEW_NAME + "/" + badgeType + "/" + attendeeId);
        } else {
            navigateTo(SearchByNameView.VIEW_NAME + "/" + badgeType);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
