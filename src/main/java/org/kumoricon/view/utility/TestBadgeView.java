package org.kumoricon.view.utility;

import com.vaadin.navigator.View;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeFactory;
import org.kumoricon.util.BadgeGenerator;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseView implements View {
    public static final String VIEW_NAME = "testbadge";
    public static final String REQUIRED_RIGHT = null;

    @Autowired
    private AttendeeFactory attendeeFactory;

    @PostConstruct
    public void init() {
        Button display = new Button("Display");
        addComponent(display);

        display.addClickListener((Button.ClickListener) event -> {
            List<Attendee> attendeeList = new ArrayList<>();
            attendeeList.add(attendeeFactory.generateDemoAttendee());
            attendeeList.add(attendeeFactory.generateYouthAttendee());
            attendeeList.add(attendeeFactory.generateChildAttendee());
            StreamResource.StreamSource source = new BadgeGenerator(attendeeList);
            String filename = "badge" + System.currentTimeMillis() + ".pdf";
            StreamResource resource = new StreamResource(source, filename);

            resource.setMIMEType("application/pdf");
            resource.getStream().setParameter("Content-Disposition", "attachment; filename="+filename);

            Window window = new Window();
            window.setWidth(800, Unit.PIXELS);
            window.setHeight(600, Unit.PIXELS);
            window.setModal(true);
            window.center();
            BrowserFrame pdf = new BrowserFrame("test", resource);
            pdf.setSizeFull();

            window.setContent(pdf);
            getUI().addWindow(window);
        });

    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}