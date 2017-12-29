package org.kumoricon.site.user;

import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserPresenter {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Logger log = LoggerFactory.getLogger(UserPresenter.class);

    @Autowired
    public UserPresenter(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    void saveUser(UserEditView view, User user) {
        log.info("{} saved user {}", view.getCurrentUsername(), user);
        userRepository.save(user);
        view.navigateTo(UserListView.VIEW_NAME);
    }

    void showUserList(UserListView view) {
        log.info("{} viewed user list", view.getCurrentUsername());
        List<User> users = userRepository.findAll(new Sort(Sort.Direction.ASC, "username"));
        view.afterSuccessfulFetch(users);
    }


    void resetPassword(UserEditView view, User user) {
        user.resetPassword();
        try {
            userRepository.save(user);
            view.notify("Changed password to \"password\" for user " + user.getUsername());
            log.info("{} reset password for {}", view.getCurrentUsername(), user);
        } catch (Exception e) {
            view.notifyError(e.getMessage());
            log.error("{} got error while resetting password for {}. {}", view.getCurrentUsername(), user, e);
        }
    }

    public List<Role> getAvailableRoles() { return roleRepository.findAll(); }

    public void showUser(UserEditView view, String userId) {
        if (userId != null) {
            log.info("{} viewed user {}", view.getCurrentUsername(), userId);
            User user = userRepository.findOne(Integer.parseInt(userId));
            view.showUser(user, getAvailableRoles());
        } else {
            view.showUser(new User(), getAvailableRoles());
        }

    }
}
