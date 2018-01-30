package org.kumoricon.site.attendee.search;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeeDetailView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.DetailFormHandler;
import org.kumoricon.site.attendee.form.AttendeeDetailForm;
import org.kumoricon.site.attendee.window.OverrideRequiredForEditWindow;
import org.kumoricon.site.attendee.window.OverrideRequiredWindow;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.site.attendee.window.ViewNoteWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
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
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    @Override
    public void close() {
        navigateTo(AttendeeSearchByBadgeView.VIEW_NAME + "/" + searchString);
    }
}
