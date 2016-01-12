package org.kumoricon.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.KumoRegUI;
import org.kumoricon.view.attendee.PreRegSearchView;
import org.kumoricon.view.attendee.SearchView;
import org.kumoricon.view.badge.BadgeView;
import org.kumoricon.view.report.AttendeeReportView;
import org.kumoricon.view.report.StaffReportView;
import org.kumoricon.view.role.RoleView;
import org.kumoricon.view.user.UserView;

@SpringComponent
@UIScope
public class SiteMenu extends VerticalLayout {
    Accordion menu = new Accordion();

    public SiteMenu() {
        setSizeFull();
        setMargin(false);
        setSpacing(false);
        setSizeFull();

        addComponent(buttonFactory("Home", FontAwesome.HOME, ""));

        Layout reg = new VerticalLayout();
        reg.setCaption("Registration");
        reg.setIcon(FontAwesome.USERS);
        reg.addComponent(buttonFactory("At-Con Registration", FontAwesome.USER, "atcon"));
        reg.addComponent(buttonFactory("Pre-Reg Check In", FontAwesome.USER, PreRegSearchView.VIEW_NAME));
        reg.addComponent(buttonFactory("Attendee Search", FontAwesome.SEARCH, SearchView.VIEW_NAME));
        menu.addComponent(reg);

        Layout tab1 = new VerticalLayout();
        tab1.setIcon(FontAwesome.GEARS);
        tab1.setCaption("Administration");
        tab1.addComponent(buttonFactory("Users", FontAwesome.USER, UserView.VIEW_NAME));
        tab1.addComponent(buttonFactory("Roles", FontAwesome.GROUP, RoleView.VIEW_NAME));
        tab1.addComponent(buttonFactory("Badge Types", FontAwesome.BARCODE, BadgeView.VIEW_NAME));
        tab1.addComponent(buttonFactory("Computers", FontAwesome.DESKTOP, "computers"));
        tab1.addComponent(buttonFactory("Import Attendees", FontAwesome.UPLOAD, "importAttendees"));

        menu.addComponent(tab1);

        Layout tab2 = new VerticalLayout();
        tab2.setCaption("Reports");
        tab2.setIcon(FontAwesome.FILE_TEXT);
        tab2.addComponent(buttonFactory("Attendance", FontAwesome.FILE_TEXT_O, AttendeeReportView.VIEW_NAME));
        tab2.addComponent(buttonFactory("Staff", FontAwesome.USERS, StaffReportView.VIEW_NAME));
        menu.addComponent(tab2);

        Layout tab3 = new VerticalLayout();
        tab3.setCaption("Utilities");
        tab3.addComponent(buttonFactory("Print Test Badge", FontAwesome.PRINT, "testbadge"));

        menu.addComponent(tab3);

        addComponent(menu);
        setExpandRatio(menu, 1.0f);

    }


    private Button buttonFactory(String name) {
        return buttonFactory(name, null, name.toLowerCase());
    }

    private Button buttonFactory(String name, FontAwesome icon) {
        return buttonFactory(name, icon, name.toLowerCase());
    }

    private Button buttonFactory(String name, FontAwesome icon, String action) {
        final String navTo = action;
        Button b = new Button();
        b.setWidth(100, Unit.PERCENTAGE);
        b.setCaption(name);
        if (icon != null) {
            b.setIcon(icon);
        }
        b.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                KumoRegUI.getCurrent().getNavigator().navigateTo(navTo);
            }
        });

        return b;
    }

}
