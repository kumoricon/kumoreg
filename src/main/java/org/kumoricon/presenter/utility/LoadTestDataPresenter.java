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
import org.kumoricon.view.utility.LoadTestDataView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.HashMap;


@Controller
@Scope("request")
public class LoadTestDataPresenter {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private RightRepository rightRepository;

    private LoadTestDataView view;

    public LoadTestDataPresenter() {
    }


    public void loadDataButtonClicked() {
        addRights();
        addRoles();
        addUsers();
        addBadges();
    }

    public void setView(LoadTestDataView view) {
        this.view = view;
    }

    private void addRights() {
        view.addResult("Creating rights");
        String[] rights = {"at_con_registration", "pre_reg_check_in", "attendee_search", "attendee_edit",
                "attendee_edit_notes", "attendee_override_price", "print_badge", "reprint_badge",
                "view_attendance_report", "view_revenue_report", "view_staff_report", "manage_staff",
                "manage_pass_types", "manage_roles", "manage_devices", "import_pre_reg_data"};

        for (String right : rights) {
            rightRepository.save(new Right(right));
        }
    }

    private void addRoles() {
        view.addResult("Creating roles");
        HashMap<String, String[]> roles = new HashMap<>();
        roles.put("Staff", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search", "print_badge"});
        roles.put("Coordinator", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                                               "print_badge", "attendee_edit", "attendee_edit_notes",
                                               "attendee_override_price", "reprint_badge", "view_staff_report"});
        roles.put("Manager", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_edit_notes",
                "attendee_override_price", "reprint_badge", "manage_staff", "view_staff_report"});
        roles.put("Director", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_edit_notes",
                "attendee_override_price", "reprint_badge", "manage_staff", "manage_pass_types", "view_role_report",
                 "view_attendance_report", "view_revenue_report", "view_staff_report"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_edit_notes"});

        HashMap<String, Right> rightMap = getRightsHashMap();

        for (String roleName : roles.keySet()) {
            Role role = new Role(roleName);
            for (String rightName : roles.get(roleName)) {
                if (rightMap.containsKey(rightName)) {
                    role.addRight(rightMap.get(rightName));
                } else {
                    view.addResult("Error creating role " + roleName + ". Right " + rightName + " not found");
                }
            }
            view.addResult("    Creating " + role.toString());
            roleRepository.save(role);
        }
    }

    private void addUsers() {
        view.addResult("Creating users");
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
                view.addResult("    Error creating user " + currentUser[0] + ". Role " + currentUser[2] + " not found");
            } else {
                user.setRole(role);
                view.addResult("    Creating " + user.toString());
                userRepository.save(user);
            }
        }
    }

    private void addBadges() {
        view.addResult("Creating badges");
        String[][] badgeList = {
                {"Weekend", "55", "45", "35"},
                {"Friday", "40", "30", "20"},
                {"Saturday", "40", "30", "20"},
                {"Sunday", "35", "25", "15"},
                {"VIP", "300", "300", "300"}};
        for (String[] currentBadge : badgeList) {
            Badge badge = BadgeFactory.badgeFactory(currentBadge[0], currentBadge[0],
                    Float.parseFloat(currentBadge[1]),
                    Float.parseFloat(currentBadge[2]),
                    Float.parseFloat(currentBadge[3]));
            view.addResult("    Creating " + badge.toString());
            badgeRepository.save(badge);
        }
    }

    private HashMap<String, Right> getRightsHashMap() {
        HashMap<String, Right> rightHashMap = new HashMap<>();
        for (Right r : rightRepository.findAll()) {
            rightHashMap.put(r.getName(), r);
        }
        return rightHashMap;
    }

}
