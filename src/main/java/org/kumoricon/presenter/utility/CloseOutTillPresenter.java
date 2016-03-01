package org.kumoricon.presenter.utility;

import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.utility.CloseOutTillView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@Scope("request")
public class CloseOutTillPresenter {

    private CloseOutTillView view;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void setView(CloseOutTillView view) {
        this.view = view;
    }

    public void closeTill(User currentUser) {
        if (currentUser != null) {
            StringBuilder output = new StringBuilder();
            output.append(currentUser.getId() + " " + currentUser.getUsername() + "\n");
            output.append(currentUser.getFirstName() + " " + currentUser.getLastName() + "\n");
            output.append(LocalDateTime.now() + "\n");
            output.append("--------------------------------------------------------------------------------\n");
            output.append("Session Number: " + currentUser.getSessionNumber() + "\n");

            // Todo: Add count and total of orders broken out by payment type

            currentUser.setSessionNumber(currentUser.getSessionNumber() + 1);
            userRepository.save(currentUser);
            output.append("--------------------------------------------------------------------------------\n");
            output.append("Session closed. New session number is: " + currentUser.getSessionNumber());
            view.showData(output.toString());
            // Todo: Print report (to which printer?) as well
        }
    }
}
