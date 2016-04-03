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
        results.append("Creating rights");
        String[] rights = {"at_con_registration", "pre_reg_check_in", "attendee_search", "attendee_edit",
                "attendee_edit_notes", "attendee_override_price", "print_badge", "reprint_badge",
                "reprint_badge_with_override", "badge_type_press", "badge_type_vip", "badge_type_artist",
                "view_attendance_report", "view_revenue_report",
                "view_check_in_by_hour_report", "view_staff_report", "view_role_report", "view_panelist_report",
                "manage_staff", "manage_pass_types", "manage_roles", "manage_devices", "import_pre_reg_data",
                "load_base_data"};

        for (String right : rights) {
            rightRepository.save(new Right(right));
        }
    }

    private void addRoles(StringBuilder results) {
        results.append("Creating roles");
        HashMap<String, String[]> roles = new HashMap<>();
        roles.put("Staff", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search", "print_badge",
                                         "reprint_badge_with_override"});
        roles.put("Coordinator", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                                               "print_badge", "attendee_edit", "attendee_edit_notes",
                                               "attendee_override_price", "reprint_badge", "view_staff_report",
                                               "view_check_in_by_hour_report", "view_presenter_report"});
        roles.put("Manager", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_edit_notes",
                "badge_type_vip", "badge_type_press", "badge_type_artist",
                "attendee_override_price", "reprint_badge", "manage_staff", "view_staff_report",
                "view_check_in_by_hour_report, view_presenter_report"});
        roles.put("Director", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_edit_notes",
                "attendee_override_price", "reprint_badge", "manage_staff", "manage_pass_types", "view_role_report",
                "view_attendance_report", "view_revenue_report", "view_staff_report", "view_check_in_by_hour_report",
                "view_presenter_report"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_edit_notes", "view_panelist_report"});

        HashMap<String, Right> rightMap = getRightsHashMap();

        for (String roleName : roles.keySet()) {
            Role role = new Role(roleName);
            for (String rightName : roles.get(roleName)) {
                if (rightMap.containsKey(rightName)) {
                    role.addRight(rightMap.get(rightName));
                } else {
                    results.append("Error creating role " + roleName + ". Right " + rightName + " not found");
                }
            }
            results.append("    Creating " + role.toString());
            roleRepository.save(role);
        }
    }

    private void addUsers(StringBuilder results) {
        results.append("Creating users");
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
                results.append("    Error creating user " + currentUser[0] + ". Role " + currentUser[2] + " not found");
            } else {
                user.setRole(role);
                results.append("    Creating " + user.toString());
                userRepository.save(user);
            }
        }
    }

    private void addBadges(StringBuilder results) {
        results.append("Creating badges");
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
            results.append("    Creating " + badge.toString());
            badgeRepository.save(badge);
        }

        // Create badge types with security restrictions below
        Badge vip = BadgeFactory.badgeFactory("VIP", "VIP", 300, 300, 300);
        vip.setRequiredRight("badge_type_vip");
        vip.setWarningMessage("VIP check in. See your coordinator!");
        results.append("    Creating " + vip.toString());
        badgeRepository.save(vip);

        Badge press = BadgeFactory.badgeFactory("Press", "Weekend", 0f, 0f, 0f);
        press.setRequiredRight("badge_type_press");
        press.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating " + press.toString());
        badgeRepository.save(press);

        Badge artist = BadgeFactory.badgeFactory("Artist", "Weekend", 0f, 0f, 0f);
        artist.setRequiredRight("badge_type_artist");
        artist.setWarningMessage("Artist check in. See your coordinator!");
        results.append("    Creating " + artist.toString());
        badgeRepository.save(artist);
    }

    private HashMap<String, Right> getRightsHashMap() {
        HashMap<String, Right> rightHashMap = new HashMap<>();
        for (Right r : rightRepository.findAll()) {
            rightHashMap.put(r.getName(), r);
        }
        return rightHashMap;
    }
}
