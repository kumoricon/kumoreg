package org.kumoricon.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.user.User;
import org.kumoricon.view.LogoutView;
import org.kumoricon.view.attendee.PreRegView;
import org.kumoricon.view.attendee.SearchView;
import org.kumoricon.view.importAttendee.ImportAttendeeView;
import org.kumoricon.view.order.OrderView;
import org.kumoricon.view.report.AttendeeReportView;
import org.kumoricon.view.report.CheckInByHourReportView;
import org.kumoricon.view.report.RoleReportView;
import org.kumoricon.view.report.StaffReportView;
import org.kumoricon.view.role.RoleView;
import org.kumoricon.view.user.UserView;
import org.kumoricon.view.utility.CloseOutTillView;
import org.kumoricon.view.utility.LoadBaseDataView;
import org.kumoricon.view.utility.TestBadgeView;

@SpringComponent
@UIScope
public class SiteMenu extends VerticalLayout {
    Accordion menu = new Accordion();

    User loggedInUser;

    public SiteMenu(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        setSizeFull();
        setMargin(false);
        setSpacing(false);
        setSizeFull();

        addComponent(buttonFactory("Home", FontAwesome.HOME, ""));

        Layout reg = new VerticalLayout();
        reg.setCaption("Registration");
        reg.setIcon(FontAwesome.USERS);
        addButtonTo(reg, "at_con_registration",
                buttonFactory("At-Con Registration", FontAwesome.USER, OrderView.VIEW_NAME));
        addButtonTo(reg, "pre_reg_check_in",
                buttonFactory("Pre-Reg Check In", FontAwesome.USER, PreRegView.VIEW_NAME));
        addButtonTo(reg, "attendee_search",
                buttonFactory("Attendee Search", FontAwesome.SEARCH, SearchView.VIEW_NAME));
        if (reg.getComponentCount() > 0) { menu.addComponent(reg); }

        Layout tab1 = new VerticalLayout();
        tab1.setIcon(FontAwesome.GEARS);
        tab1.setCaption("Administration");
        addButtonTo(tab1, "manage_staff",
                buttonFactory("Users", FontAwesome.USER, UserView.VIEW_NAME));
        addButtonTo(tab1, "manage_roles",
                buttonFactory("Roles", FontAwesome.GROUP, RoleView.VIEW_NAME));
        addButtonTo(tab1, "manage_pass_types",
                buttonFactory("Badge Types", FontAwesome.BARCODE, PreRegView.VIEW_NAME));
        addButtonTo(tab1, "manage_devices",
                buttonFactory("Computers", FontAwesome.DESKTOP, "computers"));
        if (tab1.getComponentCount() > 0) { menu.addComponent(tab1); }

        Layout tab2 = new VerticalLayout();
        tab2.setCaption("Reports");
        tab2.setIcon(FontAwesome.FILE_TEXT);
        addButtonTo(tab2, "view_attendance_report",
                buttonFactory("Attendance", FontAwesome.FILE_TEXT_O, AttendeeReportView.VIEW_NAME));
        addButtonTo(tab2, "view_check_in_by_hour_report",
                buttonFactory("Check Ins by Hour", FontAwesome.CLOCK_O, CheckInByHourReportView.VIEW_NAME));
        addButtonTo(tab2, "view_staff_report",
                buttonFactory("Staff", FontAwesome.USERS, StaffReportView.VIEW_NAME));
        addButtonTo(tab2, "view_role_report",
                buttonFactory("Roles", FontAwesome.GROUP, RoleReportView.VIEW_NAME));
        if (tab2.getComponentCount() > 0) { menu.addComponent(tab2); }

        Layout tab3 = new VerticalLayout();
        tab3.setCaption("Utilities");
        addButtonTo(tab3, null, buttonFactory("Print Test Badge", FontAwesome.PRINT, TestBadgeView.VIEW_NAME));
        addButtonTo(tab3, "import_pre_reg_data",
                buttonFactory("Load Base Data", FontAwesome.DATABASE, LoadBaseDataView.VIEW_NAME));
        addButtonTo(tab3, "import_pre_reg_data",
                buttonFactory("Import Attendees", FontAwesome.UPLOAD, ImportAttendeeView.VIEW_NAME));
        addButtonTo(tab3, "at_con_registration",
                buttonFactory("Close Out Till", FontAwesome.DOLLAR, CloseOutTillView.VIEW_NAME));
        if (tab3.getComponentCount() > 0) { menu.addComponent(tab3); }

        addComponent(menu);
        setExpandRatio(menu, 1.0f);

        addComponent(buttonFactory("Logout", FontAwesome.LOCK, LogoutView.VIEW_NAME));

    }


    private Button buttonFactory(String name) {
        return buttonFactory(name, null, name.toLowerCase());
    }

    private Button buttonFactory(String name, FontAwesome icon) {
        return buttonFactory(name, icon, name.toLowerCase());
    }

    private Button buttonFactory(String name, FontAwesome icon, String action) {
        final String navTo = action;
        Button button = new Button();
        button.setWidth(100, Unit.PERCENTAGE);
        button.setCaption(name);
        if (icon != null) {
            button.setIcon(icon);
        }
        button.addClickListener((Button.ClickListener) clickEvent ->
                KumoRegUI.getCurrent().getNavigator().navigateTo(navTo));
        return button;
    }

    private void addButtonTo(Layout tab, String requiredRight, Button button) {
        // If the current user has the given right, add the button to the Layout tab. Used when building the
        // site menu
        if (tab == null || button == null || loggedInUser == null) { return; }
        if (requiredRight == null || loggedInUser.hasRight(requiredRight)) {
            tab.addComponent(button);
        }
    }

}
