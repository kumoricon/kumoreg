package org.kumoricon;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;
import org.kumoricon.model.user.User;
import org.kumoricon.site.*;
import org.kumoricon.site.attendee.reg.OrderListView;
import org.kumoricon.site.attendee.reg.OrderView;
import org.kumoricon.site.attendee.search.AttendeeSearchByBadgeView;
import org.kumoricon.site.attendee.search.AttendeeSearchView;
import org.kumoricon.site.badge.BadgeView;
import org.kumoricon.site.computer.ComputerView;
import org.kumoricon.site.report.attendees.AttendeeReportView;
import org.kumoricon.site.report.checkinbyhour.CheckInByHourReportView;
import org.kumoricon.site.report.checkinbyuser.CheckInByUserReportView;
import org.kumoricon.site.report.export.ExportView;
import org.kumoricon.site.report.role.RoleReportView;
import org.kumoricon.site.report.staff.StaffReportView;
import org.kumoricon.site.report.till.TillReportView;
import org.kumoricon.site.role.RoleListView;
import org.kumoricon.site.tillsession.TillSessionView;
import org.kumoricon.site.user.UserListView;
import org.kumoricon.site.utility.closeouttill.CloseOutTillView;
import org.kumoricon.site.utility.importattendee.ImportAttendeeView;
import org.kumoricon.site.utility.loadbasedata.LoadBaseDataView;
import org.kumoricon.site.utility.preprint.PreprintBadgeView;
import org.kumoricon.site.utility.testbadge.TestBadgeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Theme("kumo")
@SpringUI
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class KumoRegUI extends UI {
    @Autowired
    private SpringViewProvider viewProvider;

    private Layout baseLayout;
    private MenuBar menuBar;

    @Value("${info.build.version}")
    private String version;

    @Value("${info.build.buildDate}")
    private String buildDate;

    @Value("${kumoreg.trainingMode}")
    private boolean trainingMode;

    public User getLoggedInUser(){
        return (User)getSession().getAttribute("user");
    }

    public void setLoggedInUser(User user){
        getSession().setAttribute("user", user);
        buildMenu();
    }

    @Override
    protected void init(VaadinRequest request) {
        baseLayout = new CssLayout();
        baseLayout.setSizeFull();
        setContent(baseLayout);

        buildMenu();

        baseLayout.addComponent(menuBar);

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        baseLayout.addComponent(viewContainer);

        viewContainer.setStyleName(ValoTheme.PANEL_BORDERLESS);
        if (trainingMode) {
            addStyleName("kumoTrainingMode");
        } else {
            addStyleName("kumoNormalMode");
        }

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.setErrorView(new ErrorView());

        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                User currentUser = ((KumoRegUI)KumoRegUI.getCurrent()).getLoggedInUser();
                if (currentUser == null && !(event.getNewView() instanceof LoginView)) {
                    event.getNavigator().navigateTo(LoginView.VIEW_NAME);
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {}
        });

        navigator.addProvider(viewProvider);
    }

    public String getVersionString() {
        return "Ver. " + version;
    }


    /**
     * Rebuilds the menu based on the currently logged in user
     */
    public void buildMenu() {
        MenuBar menu = new MenuBar();
        menu.setWidth("100%");
        menu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        if (getLoggedInUser() == null) {
            MenuBar.MenuItem version = menu.addItem(getVersionString(), null, null);
            version.setEnabled(false);
            version.setDescription("Build date: " + buildDate);
            version.setStyleName("menuRight");
        } else {
            String username = "(" + getLoggedInUser().getUsername() + ") ";
            MenuBar.MenuItem logout = menu.addItem("Logout " + username, null, v -> getNavigator().navigateTo(LogoutView.VIEW_NAME));
            logout.setStyleName("menuRight");
            MenuBar.MenuItem version = menu.addItem(getVersionString(), null, null);
            version.setDescription("Build date: " + buildDate);
            version.setEnabled(false);
            version.setStyleName("menuRight");
            menu.addItem("Home", null, v -> getNavigator().navigateTo(HomeView.VIEW_NAME));
            MenuBar.MenuItem registration = menu.addItem("Registration", null, null);

            if (getLoggedInUser().hasRight("at_con_registration")) {
                registration.addItem("At-Con Check In", null, v -> getNavigator().navigateTo(OrderView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("attendee_search")) {
                registration.addItem("Attendee Search/Check In", null, v -> getNavigator().navigateTo(AttendeeSearchView.VIEW_NAME));
                registration.addItem("Search by Badge Type", null, v -> getNavigator().navigateTo(AttendeeSearchByBadgeView.VIEW_NAME));
            }

            MenuBar.MenuItem admin = menu.addItem("Administration", null, null);
            if (getLoggedInUser().hasRight("manage_staff")) {
                admin.addItem("Users", null, v -> getNavigator().navigateTo(UserListView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("manage_roles")) {
                admin.addItem("Roles", null, v -> getNavigator().navigateTo(RoleListView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("manage_pass_types")) {
                admin.addItem("Badge Types", null, v -> getNavigator().navigateTo(BadgeView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("manage_devices")) {
                admin.addItem("Printers", null, v -> getNavigator().navigateTo(ComputerView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("manage_orders")) {
                admin.addItem("Orders", null, v -> getNavigator().navigateTo(OrderListView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("manage_till_sessions")) {
                admin.addItem("Till Sessions", null, v -> getNavigator().navigateTo(TillSessionView.VIEW_NAME));
            }

            if (!admin.hasChildren()) {
                menu.removeItem(admin);
            }

            MenuBar.MenuItem reports = menu.addItem("Reports", null, null);
            if (getLoggedInUser().hasRight("view_attendance_report")) {
                reports.addItem("Attendance", null, v -> getNavigator().navigateTo(AttendeeReportView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("view_check_in_by_hour_report")) {
                reports.addItem("Check Ins by Hour", null, v -> getNavigator().navigateTo(CheckInByHourReportView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("view_check_ins_by_user_report")) {
                reports.addItem("Check Ins by User", null, v -> getNavigator().navigateTo(CheckInByUserReportView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("view_staff_report")) {
                reports.addItem("Staff", null, v -> getNavigator().navigateTo(StaffReportView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("view_role_report")) {
                reports.addItem("Roles", null, v -> getNavigator().navigateTo(RoleReportView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("view_till_report")) {
                reports.addItem("Tills", null, v -> getNavigator().navigateTo(TillReportView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("view_export")) {
                reports.addItem("Export", null, v -> getNavigator().navigateTo(ExportView.VIEW_NAME));
            }
            if (!reports.hasChildren()) {
                menu.removeItem(reports);
            }

            MenuBar.MenuItem utility = menu.addItem("Utility", null, null);
            utility.addItem("Print Test Badge", null, v -> getNavigator().navigateTo(TestBadgeView.VIEW_NAME));
            if (getLoggedInUser().hasRight("at_con_registration")) {
                utility.addItem("Close out Till", null, v -> getNavigator().navigateTo(CloseOutTillView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("import_pre_reg_data")) {
                utility.addSeparator();
                utility.addItem("Load Base Data", null, v -> getNavigator().navigateTo(LoadBaseDataView.VIEW_NAME));
                utility.addItem("Import Attendee Data", null, v -> getNavigator().navigateTo(ImportAttendeeView.VIEW_NAME));
            }
            if (getLoggedInUser().hasRight("pre_print_badges")) {
                utility.addSeparator();
                utility.addItem("Pre-Print Badges", null, v -> getNavigator().navigateTo(PreprintBadgeView.VIEW_NAME));
            }

        }
        baseLayout.replaceComponent(this.menuBar, menu);
        this.menuBar = menu;
    }



}