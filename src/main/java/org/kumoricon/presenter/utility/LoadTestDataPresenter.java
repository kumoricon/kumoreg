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
        String[] rights = {"viewAttendee", "editAttendee", "search", "import"};
        for (String right : rights) {
            rightRepository.save(new Right(right));
        }
    }

    private void addRoles() {
        view.addResult("Creating roles");
        String[] roles = {"Staff", "Coordinator", "Manager", "Ops", "Admin"};
        Right search = rightRepository.findByNameIgnoreCase("Search");
        for (String roleName : roles) {
            Role role = new Role(roleName);
            role.addRight(search);
            view.addResult("    Creating " + role.toString());
            roleRepository.save(role);
        }
    }

    private void addUsers() {
        view.addResult("Creating users");
        String[][] userList = {{"admin", "user", "admin"},
                {"Jack", "Bauer", "Staff"},
                {"Kim", "Bauer", "Staff"},
                {"Michelle", "Dessler", "Coordinator"},
                {"Greg", "Müller", "ops"}};

        for (String[] currentUser : userList) {
            User user = new User(currentUser[0], currentUser[1]);
            Role role = roleRepository.findByNameIgnoreCase(currentUser[2]);
            user.setRole(role);
            view.addResult("    Creating " + user.toString());
            userRepository.save(user);
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
}
