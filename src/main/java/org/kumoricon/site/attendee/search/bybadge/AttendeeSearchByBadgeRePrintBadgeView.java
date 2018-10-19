package org.kumoricon.site.attendee.search.bybadge;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.user.User;
import org.kumoricon.site.attendee.OverrideHandler;
import org.kumoricon.site.attendee.PrintBadgeView;
import org.kumoricon.site.attendee.search.PrintBadgePresenter;
import org.kumoricon.site.attendee.search.byname.SearchByNameView;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ViewScope
@SpringView(name = AttendeeSearchByBadgeRePrintBadgeView.TEMPLATE)
public class AttendeeSearchByBadgeRePrintBadgeView extends PrintBadgeView implements View, OverrideHandler {
    public static final String VIEW_NAME = "attendeeSearchByBadge";
    public static final String REQUIRED_RIGHT = "reprint_badge";

    public static final Logger log = LoggerFactory.getLogger(AttendeeSearchByBadgeRePrintBadgeView.class);

    public static final String TEMPLATE = "attendeeSearchByBadge/{badgeType}/{attendeeId}/reprint";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(TEMPLATE);
    private User overrideUser;

    protected Integer attendeeId;
    protected String searchString;

    @Autowired
    public AttendeeSearchByBadgeRePrintBadgeView(PrintBadgePresenter handler) {
        super(handler);
    }

    @Override
    protected void printedSuccessfullyClicked() {
        if (attendee.getOrder() != null) {
            navigateTo(SearchByNameView.VIEW_NAME + "/" + attendee.getOrder().getOrderId());
        } else {
            navigateTo(SearchByNameView.VIEW_NAME);
        }
    }

    @Override
    protected void reprintClicked() {
        handler.reprintBadge(this, attendeeId);
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
        handler.showAttendeeWithoutPrinting(this, attendeeId);
        checkForPermissionsAndReprint();
    }

    private void checkForPermissionsAndReprint() {
        if (overrideUser == null) {
            if (currentUserHasRight("reprint_badge")) {
                handler.reprintLostBadge(this, attendeeId, null);
            } else {
                showOverrideRequiredWindow(this, Arrays.asList(attendee));
            }
        } else {
            if (overrideUser.hasRight("reprint_badge")) {
                handler.reprintLostBadge(this, attendeeId, overrideUser);

            } else {
                notifyError("Override user does not have the required right");
                log.error("{} requested an override to reprint a badge for {} but {} did not have the reprint_badge right",
                        getCurrentUsername(), attendee, overrideUser);
                showOverrideRequiredWindow(this, Arrays.asList(attendee));
            }
        }
    }

    private void showOverrideRequiredWindow(OverrideHandler presenter, List<Attendee> attendeeList)
    {
        OverrideRequiredWindow overrideRequiredWindow = new OverrideRequiredWindow(presenter, "reprint_badge", attendeeList);
        showWindow(overrideRequiredWindow);
    }

    @Override
    public void overrideLogin(OverrideRequiredWindow window, String username, String password, List<Attendee> targets) {
        User override = handler.findUser(username);
        if (override == null || !override.checkPassword(password)) {
            notifyError("Bad username or password");
        } else {
            this.overrideUser = override;
            window.close();
            handler.reprintLostBadge(this, targets.get(0).getId(), overrideUser);
        }
    }

    @Override
    public void close() {
        if (attendeeId != null) {
            navigateTo(SearchByBadgeView.VIEW_NAME + "/" + searchString + "/" + attendeeId);
        } else {
            navigateTo(SearchByBadgeView.VIEW_NAME + "/" + searchString);
        }
    }

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
