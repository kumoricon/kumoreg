package org.kumoricon.site;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.user.User;
import org.kumoricon.site.attendee.reg.OrderView;
import org.kumoricon.site.attendee.search.AttendeeSearchByBadgeView;
import org.kumoricon.site.attendee.search.AttendeeSearchView;
import org.kumoricon.site.badge.BadgeView;
import org.kumoricon.site.computer.ComputerView;
import org.kumoricon.site.attendee.reg.OrderListView;
import org.kumoricon.site.report.attendees.AttendeeReportView;
import org.kumoricon.site.report.checkinbyhour.CheckInByHourReportView;
import org.kumoricon.site.report.checkinbyuser.CheckInByUserReportView;
import org.kumoricon.site.report.export.ExportView;
import org.kumoricon.site.report.role.RoleReportView;
import org.kumoricon.site.report.staff.StaffReportView;
import org.kumoricon.site.report.till.TillReportView;
import org.kumoricon.site.role.RoleView;
import org.kumoricon.site.tillsession.TillSessionView;
import org.kumoricon.site.user.UserView;
import org.kumoricon.site.utility.closeouttill.CloseOutTillView;
import org.kumoricon.site.utility.importattendee.ImportAttendeeView;
import org.kumoricon.site.utility.loadbasedata.LoadBaseDataView;
import org.kumoricon.site.utility.preprint.PreprintBadgeView;
import org.kumoricon.site.utility.testbadge.TestBadgeView;


@SpringComponent
@UIScope
public class SiteMenu extends VerticalLayout {
    private Accordion menu = new Accordion();

    private User loggedInUser;

    public SiteMenu() {}

    public SiteMenu(User loggedInUser) {
        this(loggedInUser, null, null);
    }

    public SiteMenu(User loggedInUser, String version, String buildDate) {
        this.loggedInUser = loggedInUser;
        setSizeFull();
        setMargin(false);
        setSpacing(false);

        addComponent(buttonFactory("Home", FontAwesome.HOME, ""));

        Layout reg = new VerticalLayout();
        reg.setCaption("Registration");
        reg.setIcon(FontAwesome.USERS);
        addButtonTo(reg, "at_con_registration",
                buttonFactory("At-Con Registration", FontAwesome.USER, OrderView.VIEW_NAME));
        addButtonTo(reg, "attendee_search",
                buttonFactory("Search/Check In", FontAwesome.SEARCH, AttendeeSearchView.VIEW_NAME));
        addButtonTo(reg, "attendee_search",
                buttonFactory("Search by Badge", FontAwesome.BARCODE, AttendeeSearchByBadgeView.VIEW_NAME));
        if (reg.getComponentCount() > 0) { menu.addComponent(reg); }

        Layout tab1 = new VerticalLayout();
        tab1.setIcon(FontAwesome.GEARS);
        tab1.setCaption("Administration");
        addButtonTo(tab1, "manage_staff",
                buttonFactory("Users", FontAwesome.USER, UserView.VIEW_NAME));
        addButtonTo(tab1, "manage_roles",
                buttonFactory("Roles", FontAwesome.GROUP, RoleView.VIEW_NAME));
        addButtonTo(tab1, "manage_pass_types",
                buttonFactory("Badge Types", FontAwesome.BARCODE, BadgeView.VIEW_NAME));
        addButtonTo(tab1, "manage_devices",
                buttonFactory("Printers", FontAwesome.DESKTOP, ComputerView.VIEW_NAME));
        addButtonTo(tab1, "manage_orders",
                buttonFactory("Orders", FontAwesome.FILE, OrderListView.VIEW_NAME));
        addButtonTo(tab1, "manage_till_sessions",
                buttonFactory("Till Sessions", FontAwesome.DOLLAR, TillSessionView.VIEW_NAME));
        if (tab1.getComponentCount() > 0) { menu.addComponent(tab1); }

        Layout tab2 = new VerticalLayout();
        tab2.setCaption("Reports");
        tab2.setIcon(FontAwesome.FILE_TEXT);
        addButtonTo(tab2, "view_attendance_report",
                buttonFactory("Attendance", FontAwesome.FILE_TEXT_O, AttendeeReportView.VIEW_NAME));
        addButtonTo(tab2, "view_check_in_by_hour_report",
                buttonFactory("Check Ins by Hour", FontAwesome.CLOCK_O, CheckInByHourReportView.VIEW_NAME));
        addButtonTo(tab2, "view_check_in_by_user_report",
                buttonFactory("Check Ins by User", FontAwesome.FILE_TEXT_O, CheckInByUserReportView.VIEW_NAME));
        addButtonTo(tab2, "view_staff_report",
                buttonFactory("Staff", FontAwesome.USERS, StaffReportView.VIEW_NAME));
        addButtonTo(tab2, "view_role_report",
                buttonFactory("Roles", FontAwesome.GROUP, RoleReportView.VIEW_NAME));
        addButtonTo(tab2, "view_till_report",
                buttonFactory("Tills", FontAwesome.DOLLAR, TillReportView.VIEW_NAME));
        addButtonTo(tab2, ExportView.REQUIRED_RIGHT,
                buttonFactory("Export", FontAwesome.DOWNLOAD, ExportView.VIEW_NAME));
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
        addButtonTo(tab3, "pre_print_badges",
                buttonFactory("Pre-Print Badges", FontAwesome.PRINT, PreprintBadgeView.VIEW_NAME));
        if (tab3.getComponentCount() > 0) { menu.addComponent(tab3); }

        addComponent(menu);

        addComponent(buttonFactory("Logout", FontAwesome.LOCK, LogoutView.VIEW_NAME));


        VerticalLayout spacer = new VerticalLayout();
        spacer.setHeight("100%");

        if (version != null) {
            Label lblVersion = new Label("Version " + version);
            lblVersion.setSizeUndefined();
            if (buildDate != null) {
                lblVersion.setDescription("Build date: " + buildDate);
            }
            lblVersion.setSizeUndefined();
            addComponent(spacer);
            addComponent(lblVersion);
            setExpandRatio(spacer, 1.0f);
            setComponentAlignment(lblVersion, Alignment.BOTTOM_CENTER);
        }
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
