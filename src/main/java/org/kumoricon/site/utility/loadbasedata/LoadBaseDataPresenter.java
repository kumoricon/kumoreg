package org.kumoricon.site.utility.loadbasedata;

import org.kumoricon.model.badge.AgeRange;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeFactory;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(LoadBaseDataPresenter.class);

    public LoadBaseDataPresenter() {
    }

    public void loadDataButtonClicked(LoadBaseDataView view) {
        log.info("{} loaded full base data", view.getCurrentUsername());
        StringBuilder results = new StringBuilder();
        if (targetTablesAreEmpty(results)) {
            addRights(results);
            addRoles(results);
            addUsers(results);
            addBadges(results);
            addSpecialtyBadges(results);
        }
        view.addResult(results.toString());
    }

    public void loadLiteDataButtonClicked(LoadBaseDataView view) {
        log.info("{} loaded lite base data", view.getCurrentUsername());
        StringBuilder results = new StringBuilder();
        if (targetTablesAreEmpty(results)) {
            addRights(results);
            addRoles(results);
            addUsers(results);
            addLiteBadges(results);
            addSpecialtyBadges(results);
        }
        view.addResult(results.toString());
    }


    private Boolean targetTablesAreEmpty(StringBuilder results) {
        // Abort if there is more than one right, role, or user - it should just have the admin user
        // with the Admin role and super_admin right.

        Integer errors = 0;
        if (rightRepository.count() > 1) {
            log.error("rights table is not empty");
            results.append("Error: rights table not empty.\n");
            errors++;
        }
        if (roleRepository.count() > 1) {
            results.append("Error: roles table not empty.\n");
            log.error("roles table is not empty");
            errors++;
        }
        if (userRepository.count() > 1) {
            results.append("Error: users table not empty.\n");
            log.error("users table is not empty");
            errors++;
        }
        if (badgeRepository.count() > 0) {
            log.error("badges table is not empty");
            results.append("Error: badges table not empty.\n");
            errors++;
        }

        if (errors == 0) {
            return true;
        } else {
            results.append("Aborting.\n");
            log.error("Aborting base data import");
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
            {"attendee_add_note", "Edit notes field on attendees, but no other fields"},
            {"attendee_edit_with_override", "Edit attendee if a user with attendee_edit right approves it"},
            {"attendee_override_price", "Manually set price for attendee"},
            {"print_badge", "Print badge on attendee check in"},
            {"reprint_badge", "Reprint attendee badges after attendee is checked in"},
            {"reprint_badge_with_override", "Reprint badge if a user with reprint_badge right approves it"},
            {"badge_type_emerging_press", "Select/check in the \"Emerging Press\" badge type"},
            {"badge_type_standard_press", "Select/check in the \"Standard Press\" badge type"},
            {"badge_type_vip", "Select/check in the \"VIP\" badge type"},
            {"badge_type_artist", "Select/check in the \"Artist\" badge type"},
            {"badge_type_exhibitor", "Select/check in the \"Exhibitor\" badge type"},
            {"badge_type_guest", "Select/check in the \"Guest\" badge type"},
            {"badge_type_industry", "Select/check in the \"Industry\" badge type"},
            {"badge_type_panelist", "Select/check in the \"Panelist\" badge type"},
            {"badge_type_staff", "Select/check in the \"Staff\" badge type"},
            {"view_attendance_report", "View attendance report (counts only)"},
            {"view_attendance_report_revenue", "View attendance report (with revenue totals)"},
            {"view_check_in_by_hour_report", "View attendee check ins per hour report"},
            {"view_staff_report", "View staff report (lists name/phone numbers)"},
            {"view_role_report", "View registration system role report"},
            {"manage_staff", "Add/edit users and reset passwords"},
            {"manage_pass_types", "Add/edit badge types"},
            {"manage_roles", "Add/edit security roles"},
            {"manage_devices", "Add/edit devices (computer/printer mappings)"},
            {"import_pre_reg_data", "Import pre-registered attendees and orders"},
            {"load_base_data", "Load default data (users, roles, rights)"}
        };

        for (String[] rightInfo : rights) {
            log.info("Creating right {}", rightInfo[0]);
            Right right = new Right(rightInfo[0], rightInfo[1]);
            rightRepository.save(right);
        }
    }

    private void addRoles(StringBuilder results) {
        results.append("Creating roles\n");
        HashMap<String, String[]> roles = new HashMap<>();
        roles.put("Staff", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search", "print_badge",
                                         "attendee_add_note", "attendee_edit_with_override",
                                         "reprint_badge_with_override"});
        roles.put("Coordinator", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                                               "print_badge", "attendee_edit", "attendee_add_note",
                                               "reprint_badge", "view_staff_report",
                                               "view_check_in_by_hour_report"});
        roles.put("Coordinator - VIP Badges", new String[] {"at_con_registration", "pre_reg_check_in",
                                                            "attendee_search", "print_badge", "attendee_edit",
                                                            "attendee_add_note", "reprint_badge", "view_staff_report",
                                                            "view_check_in_by_hour_report", "badge_type_vip"});
        roles.put("Coordinator - Other Badges", new String[] {"at_con_registration", "pre_reg_check_in",
                                                              "attendee_search", "print_badge", "attendee_edit",
                                                              "attendee_add_note", "reprint_badge", "view_staff_report",
                                                              "view_check_in_by_hour_report", "badge_type_artist",
                                                              "badge_type_exhibitor", "badge_type_guest",
                                                              "badge_type_panelist", "badge_type_industry"});
        roles.put("Manager", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_add_note",
                "badge_type_vip", "badge_type_emerging_press", "badge_type_standard_press", "badge_type_artist",
                "badge_type_exhibitor", "badge_type_guest", "badge_type_industry", "badge_type_panelist",
                "badge_type_staff", "attendee_override_price", "reprint_badge", "manage_staff", "view_staff_report",
                "view_attendance_report", "view_check_in_by_hour_report"});
                "badge_type_vip", "badge_type_press", "badge_type_artist", "badge_type_exhibitor", "badge_type_guest",
                "badge_type_industry", "badge_type_panelist",
                "attendee_override_price", "reprint_badge", "manage_staff", "view_staff_report",
                "view_check_in_by_hour_report", "view_check_in_by_badge_report", "view_till_report"});
        roles.put("Director", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_add_note",
                "attendee_override_price", "reprint_badge", "manage_staff", "manage_pass_types",
                "badge_type_vip", "badge_type_emerging_press", "badge_type_standard_press", "badge_type_artist",
                "badge_type_exhibitor", "badge_type_guest", "badge_type_industry", "badge_type_panelist",
                "badge_type_staff", "view_role_report", "view_attendance_report", "view_attendance_report_revenue",
                "view_staff_report", "view_check_in_by_hour_report"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_add_note"});
                "badge_type_vip", "badge_type_press", "badge_type_artist", "badge_type_exhibitor", "badge_type_guest",
                "badge_type_industry", "badge_type_panelist",
                "view_role_report",
                "view_attendance_report", "view_revenue_report", "view_staff_report", "view_check_in_by_hour_report",
                "view_check_in_by_badge_report", "view_till_report"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_add_note", "view_check_in_by_badge_report",
                "view_till_report"});

        HashMap<String, Right> rightMap = getRightsHashMap();

        for (String roleName : roles.keySet()) {
            log.info("Creating role {}", roleName);
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
            log.info("Creating user {}", currentUser[0]);
            User user = UserFactory.newUser(currentUser[0], currentUser[1]);
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
            log.info("Creating badge {}", currentBadge[0]);
            Badge badge = BadgeFactory.createBadge(currentBadge[0], currentBadge[0],
                    Float.parseFloat(currentBadge[1]),
                    Float.parseFloat(currentBadge[2]),
                    Float.parseFloat(currentBadge[3]));
            results.append("    Creating " + badge.toString() + "\n");
            badgeRepository.save(badge);
        }

        // Create badge types with security restrictions below
        log.info("Creating badge VIP");
        Badge vip = BadgeFactory.createBadge("VIP", "VIP", 300, 300, 300);
        vip.setRequiredRight("badge_type_vip");
        vip.setWarningMessage("VIP check in. See your coordinator!");
        results.append("    Creating " + vip.toString() + "\n");
        badgeRepository.save(vip);
    }

    /**
     * Creates specialty badges (artist, exhibitor, etc)
     * @param results StringBuilder to append status messages to
     */
    private void addSpecialtyBadges(StringBuilder results) {
        log.info("Creating badge Artist");
        Badge artist = BadgeFactory.createBadge("Artist", "Weekend", 0f, 0f, 0f, "#5900A3");
        artist.setRequiredRight("badge_type_artist");
        artist.setWarningMessage("Artist check in. See your coordinator!");
        results.append("    Creating " + artist.toString() + "\n");
        badgeRepository.save(artist);

        log.info("Creating badge Exhibitor");
        Badge exhibitor = BadgeFactory.createBadge("Exhibitor", "Exhibitor", 0f, 0f, 0f, "#00597C");
        exhibitor.setRequiredRight("badge_type_exhibitor");
        exhibitor.setWarningMessage("Exhibitor check in. See your coordinator!");
        for (AgeRange a : artist.getAgeRanges()) {
            a.setStripeColor("#1DE5D1");
        }
        results.append("    Creating " + exhibitor.toString() + "\n");
        badgeRepository.save(exhibitor);

        log.info("Creating badge Guest");
        Badge guest = BadgeFactory.createBadge("Guest", "Guest", 0f, 0f, 0f, "#62F442");
        guest.setRequiredRight("badge_type_guest");
        guest.setWarningMessage("Guest check in. See your coordinator!");
        results.append("    Creating " + guest.toString() + "\n");
        badgeRepository.save(guest);

        log.info("Creating badge Emerging Press");
        Badge ePress = BadgeFactory.createBadge("Emerging Press", "E Press", 0f, 0f, 0f, "#1DE5D1");
        ePress.setRequiredRight("badge_type_emerging_press");
        ePress.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating " + ePress.toString() + "\n");
        badgeRepository.save(ePress);

        log.info("Creating badge Standard Press");
        Badge sPress = BadgeFactory.createBadge("Standard Press", "S Press", 0f, 0f, 0f, "#1DE5D1");
        sPress.setRequiredRight("badge_type_standard_press");
        sPress.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating " + sPress.toString() + "\n");
        badgeRepository.save(sPress);

        log.info("Creating badge Industry");
        Badge industry = BadgeFactory.createBadge("Industry", "Industry", 0f, 0f, 0f, "#FF00FC");
        industry.setRequiredRight("badge_type_industry");
        industry.setWarningMessage("Industry check in. See your coordinator!");
        results.append("    Creating " + industry.toString() + "\n");
        badgeRepository.save(industry);

        log.info("Creating badge Panelist");
        Badge panelist = BadgeFactory.createBadge("Panelist", "Panelist", 0f, 0f, 0f, "#FFA500");
        panelist.setRequiredRight("badge_type_panelist");
        panelist.setWarningMessage("Panelist check in. See your coordinator!");
        results.append("    Creating " + panelist.toString() + "\n");
        badgeRepository.save(panelist);
    }

    private void addLiteBadges(StringBuilder results) {
        results.append("Creating badges\n");

        log.info("Creating badge Kumoricon Lite");
        Badge lite = BadgeFactory.createBadge("Kumoricon Lite", "Sunday", 10, 10, 10);
        results.append("    Creating " + lite.toString() + "\n");
        badgeRepository.save(lite);

        log.info("Creating badge Kumoricon Lite - Manga Donation");
        Badge liteDonation = BadgeFactory.createBadge("Kumoricon Lite - Manga Donation", "Sunday", 0, 0, 0);
        results.append("    Creating " + liteDonation.toString() + "\n");
        badgeRepository.save(liteDonation);

        log.info("Creating badge Staff");
        Badge staff = BadgeFactory.createBadge("Staff", "Staff", 0f, 0f, 0f);
        staff.setRequiredRight("badge_type_staff");
        staff.setWarningMessage("Staff check in. See your coordinator!");
        // Clear stripe color and text - it's already printed
        for (AgeRange a : staff.getAgeRanges()) {
            a.setStripeColor("#FFFFFF");
            a.setStripeText("");
        }
        results.append("    Creating " + staff.toString() + "\n");
        badgeRepository.save(staff);
    }


    private HashMap<String, Right> getRightsHashMap() {
        HashMap<String, Right> rightHashMap = new HashMap<>();
        for (Right r : rightRepository.findAll()) {
            rightHashMap.put(r.getName(), r);
        }
        return rightHashMap;
    }
}
