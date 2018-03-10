package org.kumoricon.site.attendee.form;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.model.user.User;

import java.text.SimpleDateFormat;

/**
 * UI Widget to display an AttendeeHistory record with nicer formatting
 */
public class HistoryEntryLayout extends GridLayout {
    private static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    public HistoryEntryLayout(AttendeeHistory history) {
        configureLayout();
        addUserLabel(history);
        addTimestampLabel(history);
        addMessageLabel(history);
    }

    private void configureLayout() {
        setRows(2);
        setColumns(2);
        setColumnExpandRatio(1, 1);
        setWidth("100%");

        setMargin(false);
        setStyleName(ValoTheme.TABLE_COMPACT);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
    }

    private void addUserLabel(AttendeeHistory history) {
        String userInfo = "";
        if (history.getUser() != null) {
            User user = history.getUser();
            if (user.getFirstName() != null || user.getLastName() != null) {
                userInfo = String.format("%s %s (%s)", user.getFirstName(), user.getLastName(), user.getUsername());
            } else {
                userInfo = user.getUsername();
            }
        }

        Label userLabel = new Label(userInfo);
        userLabel.setStyleName(ValoTheme.LABEL_BOLD);
        userLabel.setStyleName(ValoTheme.LABEL_SMALL);
        userLabel.setSizeUndefined();

        addComponent(userLabel, 0, 0);
        setComponentAlignment(userLabel, Alignment.MIDDLE_LEFT);
    }

    private void addTimestampLabel(AttendeeHistory history) {
        Label timestamp = new Label(format.format(history.getTimestamp()));
        timestamp.setStyleName(ValoTheme.LABEL_SMALL);
        timestamp.setSizeUndefined();
        addComponent(timestamp, 1, 0);
        setComponentAlignment(timestamp, Alignment.MIDDLE_RIGHT);
    }

    private void addMessageLabel(AttendeeHistory history) {
        Label message = new Label(history.getMessage());
        addComponent(message, 0, 1, 1, 1);
    }

}