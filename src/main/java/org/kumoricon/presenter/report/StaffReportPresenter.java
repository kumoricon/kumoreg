package org.kumoricon.presenter.report;

import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.report.StaffReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class StaffReportPresenter {
    @Autowired
    private UserRepository userRepository;

    private StaffReportView view;

    public StaffReportPresenter() {
    }

    public void showUserList() {
        List<User> users = userRepository.findAll();
        view.afterSuccessfulFetch(users);
    }

    public StaffReportView getView() { return view; }
    public void setView(StaffReportView view) { this.view = view; }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }
}
