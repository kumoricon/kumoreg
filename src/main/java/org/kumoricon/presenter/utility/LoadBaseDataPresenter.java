package org.kumoricon.presenter.utility;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeFactory;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.utility.LoadBaseDataView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class LoadBaseDataPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private RightRepository rightRepository;

    public LoadBaseDataPresenter() {
    }

    public void loadDataButtonClicked(LoadBaseDataView view) {
        StringBuilder results = new StringBuilder();
        if (targetTablesAreEmpty(results)) {
            addRights(results);
            addRoles(results);
            addUsers(results);
            addBadges(results);
        }
        view.addResult(results.toString());
    }

    private Boolean targetTablesAreEmpty(StringBuilder results) {
        // Abort if there is more than one right, role, or user - it should just have the admin user
        // with the Admin role and super_admin right.

        Integer errors = 0;
        if (rightRepository.count() > 1) {
            results.append("Error: rights table not empty.\n");
            errors++;
        }
        if (roleRepository.count() > 1) {
            results.append("Error: roles table not empty.\n");
            errors++;
        }
        if (userRepository.count() > 1) {
            results.append("Error: users table not empty.\n");
            errors++;
        }
        if (badgeRepository.count() > 0) {
            results.append("Error: badges table not empty.\n");
            errors++;
        }

        if (errors == 0) {
            return true;
        } else {
            results.append("Aborting.\n");
            return false;
        }
    }

    private void addRights(StringBuilder results) {
        results.append("Creating rights\n");
        String[][] rights = {
            {"at_con_registration", "Add new attendees via At-Con Registration and close till"},
            {"pre_reg_check_in", "Check in preregistered attendees"},
            {"attendee_search", "Search for and view attendees"},
            {"attendee_edit", "Edit attendees from search results"},
            {"attendee_edit_notes", "Edit notes field on attendees, but no other fields"},
            {"attendee_override_price", "Manually set price for attendee"},
            {"print_badge", "Print badge on attendee check in"},
            {"reprint_badge", "Reprint attendee badges after attendee is checked in"},
            {"reprint_badge_with_override", "Reprint badge if a user with reprint_badge right approves it"},
            {"badge_type_press", "Select/check in the \"Press\" badge type"},
            {"badge_type_vip", "Select/check in the \"VIP\" badge type"},
            {"badge_type_artist", "Select/check in the \"Artist\" badge type"},
            {"badge_type_exhibitor", "Select/check in the \"Exhibitor\" badge type"},
            {"badge_type_guest", "Select/check in the \"Guest\" badge type"},
            {"badge_type_industry", "Select/check in the \"Industry\" badge type"},
            {"badge_type_panelist", "Select/check in the \"Panelist\" badge type"},
            {"view_attendance_report", "View attendance report (counts only)"},
            {"view_revenue_report", "View revenue report"},
            {"view_check_in_by_hour_report", "View attendee check ins per hour report"},
            {"view_staff_report", "View staff report (lists name/phone numbers)"},
            {"view_role_report", "View registration system role report"},
            {"view_panelist_report", "View panelist check in report"},
            {"manage_staff", "Add/edit users and reset passwords"},
            {"manage_pass_types", "Add/edit badge types"},
            {"manage_roles", "Add/edit security roles"},
            {"manage_devices", "Add/edit devices (computer/printer mappings)"},
            {"import_pre_reg_data", "Import pre-registered attendees and orders"},
            {"load_base_data", "Load default data (users, roles, rights)"}
        };

        for (String[] rightInfo : rights) {
            rightRepository.save(new Right(rightInfo[0], rightInfo[1]));
        }
    }

    private void addRoles(StringBuilder results) {
        results.append("Creating roles\n");
        HashMap<String, String[]> roles = new HashMap<>();
        roles.put("Staff", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search", "print_badge",
                                         "reprint_badge_with_override"});
        roles.put("Coordinator", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                                               "print_badge", "attendee_edit", "attendee_edit_notes",
                                               "attendee_override_price", "reprint_badge", "view_staff_report",
                                               "view_check_in_by_hour_report", "view_panelist_report"});
        roles.put("Manager", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_edit_notes",
                "badge_type_vip", "badge_type_press", "badge_type_artist", "badge_type_exhibitor", "badge_type_guest",
                "badge_type_industry", "badge_type_panelist",
                "attendee_override_price", "reprint_badge", "manage_staff", "view_staff_report",
                "view_check_in_by_hour_report", "view_panelist_report"});
        roles.put("Director", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_edit_notes",
                "attendee_override_price", "reprint_badge", "manage_staff", "manage_pass_types",
                "badge_type_vip", "badge_type_press", "badge_type_artist", "badge_type_exhibitor", "badge_type_guest",
                "badge_type_industry", "badge_type_panelist",
                "view_role_report",
                "view_attendance_report", "view_revenue_report", "view_staff_report", "view_check_in_by_hour_report",
                "view_panelist_report"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_edit_notes", "view_panelist_report"});

        HashMap<String, Right> rightMap = getRightsHashMap();

        for (String roleName : roles.keySet()) {
            Role role = new Role(roleName);
            for (String rightName : roles.get(roleName)) {
                if (rightMap.containsKey(rightName)) {
                    role.addRight(rightMap.get(rightName));
                } else {
                    results.append("Error creating role " + roleName + ". Right " + rightName + " not found\n");
                }
            }
            results.append("    Creating " + role.toString() + "\n");
            roleRepository.save(role);
        }
    }

    private void addUsers(StringBuilder results) {
        results.append("Creating users\n");
        String[][] userList = {
                              {"Staff", "User", "Staff"},
                              {"Coordinator", "User", "Coordinator"},
                              {"Manager", "User", "Manager"},
                              {"Director", "User", "Director"},
                              {"Ops", "User", "ops"}};

        for (String[] currentUser : userList) {
            User user = new User(currentUser[0], currentUser[1]);
            user.setUsername(currentUser[0]);
            Role role = roleRepository.findByNameIgnoreCase(currentUser[2]);
            if (role == null) {
                results.append("    Error creating user " + currentUser[0] + ". Role " + currentUser[2] + " not found\n");
            } else {
                user.setRole(role);
                results.append("    Creating " + user.toString() + "\n");
                userRepository.save(user);
            }
        }
    }

    private void addBadges(StringBuilder results) {
        results.append("Creating badges\n");
        String[][] badgeList = {
                {"Weekend", "60", "60", "45"},
                {"Friday", "40", "40", "30"},
                {"Saturday", "40", "40", "30"},
                {"Sunday", "30", "30", "20"}};
        for (String[] currentBadge : badgeList) {
            Badge badge = BadgeFactory.badgeFactory(currentBadge[0], currentBadge[0],
                    Float.parseFloat(currentBadge[1]),
                    Float.parseFloat(currentBadge[2]),
                    Float.parseFloat(currentBadge[3]));
            results.append("    Creating " + badge.toString() + "\n");
            badgeRepository.save(badge);
        }

        // Create badge types with security restrictions below
        Badge vip = BadgeFactory.badgeFactory("VIP", "VIP", 300, 300, 300);
        vip.setRequiredRight("badge_type_vip");
        vip.setWarningMessage("VIP check in. See your coordinator!");
        results.append("    Creating " + vip.toString() + "\n");
        badgeRepository.save(vip);

        Badge artist = BadgeFactory.badgeFactory("Artist", "Weekend", 0f, 0f, 0f);
        artist.setRequiredRight("badge_type_artist");
        artist.setWarningMessage("Artist check in. See your coordinator!");
        results.append("    Creating " + artist.toString() + "\n");
        badgeRepository.save(artist);

        Badge exhibitor = BadgeFactory.badgeFactory("Exhibitor", "Exhibitor", 0f, 0f, 0f);
        exhibitor.setRequiredRight("badge_type_exhibitor");
        exhibitor.setWarningMessage("Exhibitor check in. See your coordinator!");
        results.append("    Creating " + exhibitor.toString() + "\n");
        badgeRepository.save(exhibitor);

        Badge guest = BadgeFactory.badgeFactory("Guest", "Guest", 0f, 0f, 0f);
        guest.setRequiredRight("badge_type_guest");
        guest.setWarningMessage("Guest check in. See your coordinator!");
        results.append("    Creating " + guest.toString() + "\n");
        badgeRepository.save(guest);

        Badge press = BadgeFactory.badgeFactory("Press", "Press", 0f, 0f, 0f);
        press.setRequiredRight("badge_type_press");
        press.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating " + press.toString() + "\n");
        badgeRepository.save(press);

        Badge industry = BadgeFactory.badgeFactory("Industry", "Industry", 0f, 0f, 0f);
        press.setRequiredRight("badge_type_industry");
        press.setWarningMessage("Industry check in. See your coordinator!");
        results.append("    Creating " + industry.toString() + "\n");
        badgeRepository.save(industry);

        Badge panelist = BadgeFactory.badgeFactory("Panelist", "Panelist", 0f, 0f, 0f);
        press.setRequiredRight("badge_type_panelist");
        press.setWarningMessage("Panelist check in. See your coordinator!");
        results.append("    Creating " + panelist.toString() + "\n");
        badgeRepository.save(panelist);
    }

    private HashMap<String, Right> getRightsHashMap() {
        HashMap<String, Right> rightHashMap = new HashMap<>();
        for (Right r : rightRepository.findAll()) {
            rightHashMap.put(r.getName(), r);
        }
        return rightHashMap;
    }
}
