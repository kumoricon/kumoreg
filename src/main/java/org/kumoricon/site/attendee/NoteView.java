package org.kumoricon.site.attendee;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.AttendeeHistory;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.search.AttendeeHistoryPresenter;

import javax.annotation.PostConstruct;

public abstract class NoteView extends BaseView implements View {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "attendee_search";

    private TextArea note = new TextArea("Note");
    private TextField timeStamp = new TextField("Timestamp");
    private TextField user = new TextField("User");

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");


    protected AttendeeHistory attendeeHistory;
    protected Integer orderId;
    protected AttendeeHistoryPresenter handler;

    public NoteView(AttendeeHistoryPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        user.setEnabled(false);
        timeStamp.setEnabled(false);
        addComponents(note, user, timeStamp, buildButtons());
    }


    protected VerticalLayout buildButtons() {
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("15%");
        buttons.setMargin(new MarginInfo(false, true, false, true));

        save.addClickListener((Button.ClickListener) clickEvent -> {
            saveClicked(note.getValue().trim());
            close();
        });

        cancel.addClickListener(c -> close());

        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);

        buttons.addComponents(save, cancel);
        return buttons;
    }

    protected abstract void saveClicked(String message);

    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    public void showNote(AttendeeHistory attendeeHistory) {
        this.attendeeHistory = attendeeHistory;
        note.setValue(attendeeHistory.getMessage());
        user.setValue(attendeeHistory.getUser().getUsername());
        timeStamp.setValue(attendeeHistory.getTimestamp().toString());
    }

    protected void showOnlyAddControls(boolean visible) {
        save.setVisible(visible);
        cancel.setVisible(true);

        user.setVisible(!visible);
        timeStamp.setVisible(!visible);
    }

}
