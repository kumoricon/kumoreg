package org.kumoricon.site.report.staff;

import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class StaffReportPresenter {
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(StaffReportPresenter.class);

    @Autowired
    public StaffReportPresenter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void showUserList(StaffReportView view) {
        List<User> users = userRepository.findAll();
        view.afterSuccessfulFetch(users);
        log.info("{} viewed Staff Report", view.getCurrentUsername());

    }
}
