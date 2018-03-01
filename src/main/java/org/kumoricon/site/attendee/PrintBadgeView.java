package org.kumoricon.site.attendee;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.search.PrintBadgePresenter;

import javax.annotation.PostConstruct;
import java.util.Arrays;

public abstract class PrintBadgeView extends BaseView implements View {
    public static final String VIEW_NAME = "order";
    public static final String REQUIRED_RIGHT = "print_badge";

    private Button btnPrintedSuccessfully = new Button("Printed Successfully?");
    private Button btnReprint = new Button("Reprint Selected");

    protected PrintBadgePresenter handler;
    protected Attendee attendee;

    protected BrowserFrame pdf;

    public PrintBadgeView(PrintBadgePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
    }

    protected void showBadge(Attendee attendee) {
        this.attendee = attendee;
        StreamResource.StreamSource source = handler.getBadgeFormatter(this, Arrays.asList(attendee));
        String filename = "testbadge" + System.currentTimeMillis() + ".pdf";
        StreamResource resource = new StreamResource(source, filename);
        pdf = new BrowserFrame("", resource);

        resource.setMIMEType("application/pdf");
        resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);

        pdf.setWidth("700px");
        pdf.setHeight("500px");
        addComponents(pdf, buildButtons());
    }

    protected VerticalLayout buildButtons() {
        VerticalLayout layout = new VerticalLayout();

        layout.addComponent(btnReprint);
        layout.addComponent(btnPrintedSuccessfully);
        btnReprint.addClickListener((Button.ClickListener) clickEvent -> {
            handler.reprintBadges(this, Arrays.asList(attendee));
        });
        btnPrintedSuccessfully.addClickListener((Button.ClickListener) clickEvent -> {
            handler.badgePrintSuccess(this, Arrays.asList(attendee));
            close();
        });

        btnPrintedSuccessfully.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnPrintedSuccessfully.addStyleName(ValoTheme.BUTTON_PRIMARY);

        return layout;
    }



    @Override
    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }

    public void showAttendee(Attendee attendee) {
        if (!currentUserHasRight("pre_print_badges") && !attendee.getCheckedIn()) {
            notifyError("Error: This attendee hasn't checked in yet");
            close();
        }
        showBadge(attendee);
    }
}
