package org.kumoricon.site.attendee.window;

import com.vaadin.ui.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.PrintBadgeHandler;

import java.util.ArrayList;
import java.util.List;

public class PrintBadgeWindow extends Window {

    private Button printedSuccessfully = new Button("Printed Successfully?");
    private Button reprint = new Button("Reprint Selected");
    private Button showBadgeInBrowser = new Button("Show Selected In Browser");

    Grid<Attendee> attendeeGrid;

    private PrintBadgeHandler handler;
    private BaseView parentView;

    public PrintBadgeWindow(BaseView view, PrintBadgeHandler presenter, List<Attendee> attendeeList) {
        super("Reprint Badges");
        this.handler = presenter;
        this.parentView = view;
        setIcon(FontAwesome.PRINT);
        setModal(true);
        setClosable(false);
        center();
        setWidth(700, Unit.PIXELS);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        attendeeGrid = new Grid<>();
        attendeeGrid.addColumn(Attendee::getFirstName).setCaption("First Name");
        attendeeGrid.addColumn(Attendee::getLastName).setCaption("Last Name");
        attendeeGrid.addColumn(Attendee::getAge).setCaption("Age");
        attendeeGrid.addColumn(attendee -> attendee.getBadge().getName()).setCaption("Badge");
        attendeeGrid.setSizeFull();
        attendeeGrid.addStyleName("kumoHeaderOnlyHandPointer");
        attendeeGrid.setColumnOrder("firstName", "lastName", "age");
        attendeeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        attendeeGrid.setItems(attendeeList);
        for (Attendee a : attendeeList) {
            attendeeGrid.select(a);
        }

        verticalLayout.addComponent(attendeeGrid);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(reprint);
        horizontalLayout.addComponent(showBadgeInBrowser);
        horizontalLayout.addComponent(printedSuccessfully);
        printedSuccessfully.focus();

        reprint.addClickListener((Button.ClickListener) clickEvent -> {
            List<Attendee> selectedAttendees = new ArrayList<>(attendeeGrid.getSelectedItems());
            handler.reprintBadges(this, selectedAttendees);
        });

        showBadgeInBrowser.addClickListener((Button.ClickListener) clickEvent -> {
            List<Attendee> selectedAttendees = new ArrayList<>(attendeeGrid.getSelectedItems());
            showBadgesInBrowser(selectedAttendees);
        });
        printedSuccessfully.addClickListener((Button.ClickListener) clickEvent ->
                handler.badgePrintSuccess(this, attendeeList));
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);

        printedSuccessfully.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        printedSuccessfully.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public void showBadgesInBrowser(List<Attendee> attendeeList) {
        if (attendeeList.size() > 0) {
            StreamResource.StreamSource source = handler.getBadgeFormatter(this, attendeeList);
            String filename = "testbadge" + System.currentTimeMillis() + ".pdf";
            StreamResource resource = new StreamResource(source, filename);

            resource.setMIMEType("application/pdf");
            resource.getStream().setParameter("Content-Disposition", "attachment; filename="+filename);

            Window window = new Window();
            window.setWidth(800, Sizeable.Unit.PIXELS);
            window.setHeight(600, Sizeable.Unit.PIXELS);
            window.setModal(true);
            window.center();
            BrowserFrame pdf = new BrowserFrame("test", resource);
            pdf.setSizeFull();

            window.setContent(pdf);
            getUI().addWindow(window);
        } else {
            Notification.show("No attendees selected");
        }
    }


    public PrintBadgeHandler getHandler() { return handler; }
    public void setHandler(PrintBadgeHandler handler) { this.handler = handler; }

    public BaseView getParentView() { return parentView; }
}
