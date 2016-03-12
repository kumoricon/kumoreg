package org.kumoricon.presenter.report;

import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.report.StaffReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class StaffReportPresenter {
    @Autowired
    private UserRepository userRepository;

    public StaffReportPresenter() {
    }

    public void showUserList(StaffReportView view) {
        List<User> users = userRepository.findAll();
        view.afterSuccessfulFetch(users);
    }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }
}
