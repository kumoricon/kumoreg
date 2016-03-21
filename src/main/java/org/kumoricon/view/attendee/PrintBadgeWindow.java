package org.kumoricon.view.attendee;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.attendee.PrintBadgeHandler;
import org.kumoricon.util.BadgeGenerator;
import org.kumoricon.view.BaseView;

import java.util.ArrayList;
import java.util.List;

public class PrintBadgeWindow extends Window {

    Button printedSuccessfully = new Button("Printed Successfully");
    Button reprint = new Button("Reprint Selected");
    Button showBadgeInBrowser = new Button("Show Selected In Browser");

    Grid attendeeGrid;
    BeanItemContainer<Attendee> container;

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

        container =  new BeanItemContainer<>(Attendee.class, attendeeList);
        attendeeGrid = new Grid(container);
        attendeeGrid.removeAllColumns();
        attendeeGrid.addColumn("firstName");
        attendeeGrid.addColumn("lastName");
        attendeeGrid.addColumn("age");
        attendeeGrid.addColumn("badge");
        attendeeGrid.setSizeFull();
        attendeeGrid.setColumnOrder("firstName", "lastName", "age");
        attendeeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        verticalLayout.addComponent(attendeeGrid);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(reprint);
        horizontalLayout.addComponent(showBadgeInBrowser);
        horizontalLayout.addComponent(printedSuccessfully);
        printedSuccessfully.focus();

        reprint.addClickListener((Button.ClickListener) clickEvent -> {
            List<Attendee> selectedAttendees = new ArrayList<>();
            for (Object sel : attendeeGrid.getSelectedRows()) {
                selectedAttendees.add((Attendee) sel);
            }
            handler.reprintBadges(this, selectedAttendees);
        });
        showBadgeInBrowser.addClickListener((Button.ClickListener) clickEvent -> {
            List<Attendee> selectedAttendees = new ArrayList<>();
            for (Object sel : attendeeGrid.getSelectedRows()) {
                selectedAttendees.add((Attendee) sel);
            }
            showBadgesInBrowser(selectedAttendees);
        });
        printedSuccessfully.addClickListener((Button.ClickListener) clickEvent ->
                handler.badgePrintSuccess(this, attendeeList));
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);
    }

    public void showBadgesInBrowser(List<Attendee> attendeeList) {
        if (attendeeList.size() > 0) {
            StreamResource.StreamSource source = new BadgeGenerator(attendeeList);
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
    public void setParentView(BaseView parentView) { this.parentView = parentView; }
}
