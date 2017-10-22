package org.kumoricon.site.utility.loadbasedata;

import org.kumoricon.model.badge.*;
import org.kumoricon.model.blacklist.BlacklistName;
import org.kumoricon.model.blacklist.BlacklistRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class LoadBaseDataPresenter {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BadgeRepository badgeRepository;

    private final RightRepository rightRepository;

    private final BlacklistRepository blacklistRepository;

    @Value("${kumoreg.trainingMode}")
    private boolean trainingMode;


    private static final Logger log = LoggerFactory.getLogger(LoadBaseDataPresenter.class);

    @Autowired
    public LoadBaseDataPresenter(UserRepository userRepository, RoleRepository roleRepository, BadgeRepository badgeRepository, RightRepository rightRepository, BlacklistRepository blacklistRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.badgeRepository = badgeRepository;
        this.rightRepository = rightRepository;
        this.blacklistRepository = blacklistRepository;
    }

    void loadDataButtonClicked(LoadBaseDataView view) {
        log.info("{} loaded full base data", view.getCurrentUsername());
        StringBuilder results = new StringBuilder();
        if (targetTablesAreEmpty(results)) {
            addRights(results);
            addRoles(results);
            addUsers(results);
            addAttendeeBadges(results);
            addStaffBadges(results);
            addSpecialtyBadges(results);
            addBlacklistedNames(results);
        }
        view.addResult(results.toString());
    }

    void loadLiteDataButtonClicked(LoadBaseDataView view) {
        log.info("{} loaded lite base data", view.getCurrentUsername());
        StringBuilder results = new StringBuilder();
        if (targetTablesAreEmpty(results)) {
            addRights(results);
            addRoles(results);
            addUsers(results);
            addLiteAttendeeBadges(results);
            addStaffBadges(results);
            addSpecialtyBadges(results);
            addBlacklistedNames(results);
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
            {"at_con_registration_blacklist", "Allow at-con registration for names on the blacklist"},
            {"pre_reg_check_in", "Check in preregistered attendees"},
            {"pre_reg_check_in_edit", "Edit preregistered attendee information during check in"},
            {"attendee_search", "Search for and view attendees"},
            {"attendee_edit", "Edit attendees from mySearch results"},
            {"attendee_add_note", "Edit notes field on attendees, but no other fields"},
            {"attendee_edit_with_override", "Edit attendee if a user with attendee_edit right approves it"},
            {"attendee_override_price", "Manually set price for attendee"},
            {"print_badge", "Print badge on attendee check in"},
            {"reprint_badge", "Reprint attendee badges after attendee is checked in"},
            {"reprint_badge_with_override", "Reprint badge if a user with reprint_badge right approves it"},
            {"badge_type_emerging_press", "Select/check in the \"Emerging Press\" badge type"},
            {"badge_type_standard_press", "Select/check in the \"Standard Press\" badge type"},
            {"badge_type_small_press", "Select/check in the \"Small Press\" badge type"},
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
            {"view_check_in_by_user_report", "View attendee check ins per user report"},
            {"view_staff_report", "View staff report (lists name/phone numbers)"},
            {"view_role_report", "View registration system role report"},
            {"view_till_report", "View till report"},
            {"view_export", "Export information/reports"},
            {"manage_staff", "Add/edit users and reset passwords"},
            {"manage_pass_types", "Add/edit badge types"},
            {"manage_roles", "Add/edit security roles"},
            {"manage_orders", "List/edit orders after they have been placed"},
            {"manage_devices", "Add/edit devices (computer/printer mappings)"},
            {"manage_till_sessions", "View/Close Till Sessions for other users"},
            {"import_pre_reg_data", "Import pre-registered attendees and orders"},
            {"load_base_data", "Load default data (users, roles, rights)"},
            {"pre_print_badges", "Pre-print badges for all attendees with a particular badge type"}
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
        roles.put("Staff - Specialty Badges", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search", "print_badge",
                                         "attendee_add_note", "attendee_edit_with_override",
                                         "reprint_badge_with_override", "badge_type_artist",
                                         "badge_type_standard_press", "badge_type_emerging_press",
                                         "badge_type_exhibitor", "badge_type_guest",
                                         "badge_type_panelist", "badge_type_industry", "pre_reg_check_in_edit"});

        roles.put("Coordinator", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                                               "print_badge", "attendee_edit", "attendee_add_note",
                                               "reprint_badge", "view_staff_report",
                                               "view_check_in_by_hour_report", "pre_reg_check_in_edit"});
        roles.put("Coordinator - VIP Badges", new String[] {"at_con_registration", "pre_reg_check_in",
                                                            "attendee_search", "print_badge", "attendee_edit",
                                                            "attendee_add_note", "reprint_badge", "view_staff_report",
                                                            "view_check_in_by_hour_report", "badge_type_vip",
                                                            "pre_reg_check_in_edit"});
        roles.put("Coordinator - Specialty Badges", new String[] {"at_con_registration", "pre_reg_check_in",
                                                              "attendee_search", "print_badge", "attendee_edit",
                                                              "attendee_add_note", "reprint_badge", "view_staff_report",
                                                              "view_check_in_by_hour_report", "badge_type_artist",
                                                              "badge_type_standard_press", "badge_type_emerging_press",
                                                              "badge_type_exhibitor", "badge_type_guest",
                                                              "badge_type_panelist", "badge_type_industry",
                                                              "badge_type_small_press",
                                                              "pre_reg_check_in_edit"});
        roles.put("MSO", new String[] {"pre_reg_check_in",
                "attendee_search", "print_badge", "attendee_edit",
                "attendee_add_note", "reprint_badge", "view_staff_report",
                "view_check_in_by_hour_report", "badge_type_staff",
                "pre_reg_check_in_edit"});
        roles.put("Manager", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_add_note", "at_con_registration_blacklist",
                "badge_type_vip", "badge_type_emerging_press", "badge_type_standard_press", "badge_type_artist",
                "badge_type_exhibitor", "badge_type_guest", "badge_type_industry", "badge_type_panelist", "badge_type_small_press",
                "badge_type_staff", "attendee_override_price", "reprint_badge", "manage_staff", "view_staff_report",
                "view_attendance_report", "view_check_in_by_hour_report", "view_till_report", "view_export",
                "view_check_in_by_user_report", "pre_reg_check_in_edit", "manage_orders", "manage_till_sessions"});
        roles.put("Director", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_add_note", "at_con_registration_blacklist",
                "attendee_override_price", "reprint_badge", "manage_staff", "manage_pass_types",
                "badge_type_vip", "badge_type_emerging_press", "badge_type_standard_press", "badge_type_artist",
                "badge_type_exhibitor", "badge_type_guest", "badge_type_industry", "badge_type_panelist",
                "badge_type_small_press",
                "badge_type_staff", "view_role_report", "view_attendance_report", "view_attendance_report_revenue",
                "view_staff_report", "view_check_in_by_hour_report", "view_till_report", "pre_reg_check_in_edit",
                "view_check_in_by_user_report", "view_export", "manage_orders", "manage_till_sessions",
                "pre_print_badges"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_add_note"});

        HashMap<String, Right> rightMap = getRightsHashMap();

        for (String roleName : roles.keySet()) {
            log.info("Creating role {}", roleName);
            Role role = new Role(roleName);
            for (String rightName : roles.get(roleName)) {
                if (rightMap.containsKey(rightName)) {
                    role.addRight(rightMap.get(rightName));
                } else {
                    results.append("Error creating role ")
                            .append(roleName)
                            .append(". Right ")
                            .append(rightName)
                            .append(" not found\n");
                }
            }
            results.append("    Creating ")
                    .append(role.toString())
                    .append("\n");
            roleRepository.save(role);
        }
    }

    private void addUsers(StringBuilder results) {
        if (!trainingMode) {
            results.append("trainingMode not set in application configuration, skipping example user creation\n");
            return;
        }
        results.append("Creating users\n");
        String[][] userList = {
                              {"Staff", "User", "Staff"},
                              {"Coordinator", "User", "Coordinator"},
                              {"Manager", "User", "Manager"},
                              {"Director", "User", "Director"},
                              {"Ops", "User", "ops"},
                              {"Member Services", "Person", "MSO"}};

        for (String[] currentUser : userList) {
            log.info("Creating user {}", currentUser[0]);
            User user = UserFactory.newUser(currentUser[0], currentUser[1]);
            user.setUsername(currentUser[0]);
            Role role = roleRepository.findByNameIgnoreCase(currentUser[2]);
            if (role == null) {
                results.append("    Error creating user ")
                        .append(currentUser[0])
                        .append(". Role ")
                        .append(currentUser[2])
                        .append(" not found\n");
            } else {
                user.setRole(role);
                results.append("    Creating ")
                        .append(user.toString())
                        .append("\n");
                userRepository.save(user);
            }
        }
    }

    private void addAttendeeBadges(StringBuilder results) {
        results.append("Creating badges\n");
        String[][] badgeList = {
                {"Weekend", "#000000", "65", "65", "45"},
                {"Friday", "#000000", "50", "50", "30"},
                {"Saturday", "#000000", "50", "50", "30"},
                {"Sunday", "#000000", "40", "40", "20"}};
        for (String[] currentBadge : badgeList) {
            log.info("Creating badge {}", currentBadge[0]);
            Badge badge = BadgeFactory.createBadge(currentBadge[0], BadgeType.ATTENDEE,
                    currentBadge[0],
                    currentBadge[1],
                    Float.parseFloat(currentBadge[2]),
                    Float.parseFloat(currentBadge[3]),
                    Float.parseFloat(currentBadge[4]));
            results.append("    Creating ").append(badge.toString()).append("\n");
            badgeRepository.save(badge);
        }

        // Create badge types with security restrictions below
        log.info("Creating badge VIP");
        Badge vip = BadgeFactory.createBadge("VIP", BadgeType.ATTENDEE, "VIP", "#000000", 300, 300, 300);
        vip.setRequiredRight("badge_type_vip");
        vip.setWarningMessage("VIP check in. See your coordinator!");
        vip.setBadgeTypeText("VIP");
        results.append("    Creating ").append(vip.toString()).append("\n");
        badgeRepository.save(vip);
    }

    private void addStaffBadges(StringBuilder results) {
        log.info("Creating badge Staff");
        Badge staff = BadgeFactory.createBadge("Staff", BadgeType.STAFF, "Staff", "#FFFFFF", 0f, 0f, 0f);
        staff.setRequiredRight("badge_type_staff");
        staff.setWarningMessage("Staff check in. See your coordinator!");
        // Clear stripe color and text - it's already printed
        for (AgeRange a : staff.getAgeRanges()) {
            a.setStripeColor("#FFFFFF");
            a.setStripeText("Staff");
        }
        results.append("    Creating ").append(staff.toString()).append("\n");
        badgeRepository.save(staff);
    }

    /**
     * Creates specialty badges (artist, exhibitor, etc)
     * @param results StringBuilder to append status messages to
     */
    private void addSpecialtyBadges(StringBuilder results) {
        log.info("Creating badge Artist");
        Badge artist = BadgeFactory.createBadge("Artist", BadgeType.OTHER, "Artist", "#800080", 0f, 0f, 0f);
        artist.setRequiredRight("badge_type_artist");
        artist.setWarningMessage("Artist check in. See your coordinator!");
        results.append("    Creating ").append(artist.toString()).append("\n");
        badgeRepository.save(artist);

        log.info("Creating badge Exhibitor");
        Badge exhibitor = BadgeFactory.createBadge("Exhibitor", BadgeType.OTHER, "Exhibitor", "#00FFFF", 0f, 0f, 0f);
        exhibitor.setRequiredRight("badge_type_exhibitor");
        exhibitor.setWarningMessage("Exhibitor check in. See your coordinator!");
        results.append("    Creating ").append(exhibitor.toString()).append("\n");
        badgeRepository.save(exhibitor);

        log.info("Creating badge Guest");
        Badge guest = BadgeFactory.createBadge("Guest", BadgeType.OTHER,"Guest", "#62F442", 0f, 0f, 0f);
        guest.setRequiredRight("badge_type_guest");
        guest.setWarningMessage("Guest check in. See your coordinator!");
        results.append("    Creating ").append(guest.toString()).append("\n");
        badgeRepository.save(guest);

        log.info("Creating badge Small Press");
        Badge smallPress = BadgeFactory.createBadge("Small Press", BadgeType.OTHER,"Small Press", "#800080", 0f, 0f, 0f);
        smallPress.setRequiredRight("badge_type_small_press");
        smallPress.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating ").append(smallPress.toString()).append("\n");
        badgeRepository.save(smallPress);

        log.info("Creating badge Emerging Press");
        Badge ePress = BadgeFactory.createBadge("Emerging Press", BadgeType.OTHER,"E Press", "#1DE5D1", 0f, 0f, 0f);
        ePress.setRequiredRight("badge_type_emerging_press");
        ePress.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating ").append(ePress.toString()).append("\n");
        badgeRepository.save(ePress);

        log.info("Creating badge Standard Press");
        Badge sPress = BadgeFactory.createBadge("Standard Press", BadgeType.OTHER,"S Press", "#1DE5D1", 0f, 0f, 0f);
        sPress.setRequiredRight("badge_type_standard_press");
        sPress.setWarningMessage("Press check in. See your coordinator!");
        results.append("    Creating ").append(sPress.toString()).append("\n");
        badgeRepository.save(sPress);

        log.info("Creating badge Industry");
        Badge industry = BadgeFactory.createBadge("Industry", BadgeType.OTHER,"Industry", "#FF00FC", 0f, 0f, 0f);
        industry.setRequiredRight("badge_type_industry");
        industry.setWarningMessage("Industry check in. See your coordinator!");
        results.append("    Creating ").append(industry.toString()).append("\n");
        badgeRepository.save(industry);

        log.info("Creating badge Panelist");
        Badge panelist = BadgeFactory.createBadge("Panelist", BadgeType.OTHER,"Panelist", "#FFA500", 0f, 0f, 0f);
        panelist.setRequiredRight("badge_type_panelist");
        panelist.setWarningMessage("Panelist check in. See your coordinator!");
        results.append("    Creating ").append(panelist.toString()).append("\n");
        badgeRepository.save(panelist);
    }

    private void addBlacklistedNames(StringBuilder results) {
        results.append("Adding 'Blacklist Test' to name blacklist");
        blacklistRepository.save(new BlacklistName("Blacklist", "Test"));
    }

    private void addLiteAttendeeBadges(StringBuilder results) {
        results.append("Creating badges\n");

        log.info("Creating badge Kumoricon Lite");
        Badge lite = BadgeFactory.createBadge("Kumoricon Lite", BadgeType.ATTENDEE, "Sunday", "#323E99", 10, 10, 10);
        results.append("    Creating ").append(lite.toString()).append("\n");
        badgeRepository.save(lite);

        log.info("Creating badge Kumoricon Lite - Manga Donation");
        Badge liteDonation = BadgeFactory.createBadge("Kumoricon Lite - Manga Donation", BadgeType.ATTENDEE, "Sunday", "#323E99", 0, 0, 0);
        results.append("    Creating ").append(liteDonation.toString()).append("\n");
        badgeRepository.save(liteDonation);
    }


    private HashMap<String, Right> getRightsHashMap() {
        HashMap<String, Right> rightHashMap = new HashMap<>();
        for (Right r : rightRepository.findAll()) {
            rightHashMap.put(r.getName(), r);
        }
        return rightHashMap;
    }
}
