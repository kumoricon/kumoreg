package org.kumoricon.site.user;

import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;
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

    void userSelected(UserView view, User user) {
        if (user != null) {
            log.info("{} viewed user {}", view.getCurrentUsername(), user);
            view.navigateTo(UserView.VIEW_NAME + "/" + user.getId().toString());
            view.showUser(user, getAvailableRoles());
        }
    }

    void addNewUser(UserView view) {
        log.info("{} created new user", view.getCurrentUsername());
        view.navigateTo(UserView.VIEW_NAME);
        User user = UserFactory.newUser();
        view.showUser(user, getAvailableRoles());
    }

    void cancelUser(UserEditWindow window) {
        UserView view = window.getParentView();
        view.navigateTo(UserView.VIEW_NAME);
        window.close();
        view.clearSelection();
    }

    void saveUser(UserEditWindow window, User user) {
        UserView view = window.getParentView();
        log.info("{} saved user {}", view.getCurrentUsername(), user);
        userRepository.save(user);
        window.close();
        view.navigateTo(UserView.VIEW_NAME);
        showUserList(view);
    }

    void showUserList(UserView view) {
        log.info("{} viewed user list", view.getCurrentUsername());
        List<User> users = userRepository.findAll(new Sort(Sort.Direction.ASC, "username"));
        view.afterSuccessfulFetch(users);
    }

    void navigateToUser(UserView view, String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            User user = userRepository.findOne(id);
            if (user != null) {
                view.selectUser(user);
            } else {
                log.error("{} tried to view user id {} but it was not found in the database",
                        view.getCurrentUsername(), id);
            }
        }
    }

    void resetPassword(UserEditWindow window, User user) {
        UserView view = window.getParentView();
        user.resetPassword();
        try {
            userRepository.save(user);
            view.notify("Changed password to \"password\" for user " + user.getUsername());
            log.info("{} reset password for {}", view.getCurrentUsername(), user);
            window.close();
        } catch (Exception e) {
            view.notifyError(e.getMessage());
            log.error("{} got error while resetting password for {}. {}", view.getCurrentUsername(), user, e);
        }
    }

    private List<Role> getAvailableRoles() { return roleRepository.findAll(); }
}
