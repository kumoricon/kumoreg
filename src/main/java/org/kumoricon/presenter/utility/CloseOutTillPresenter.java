package org.kumoricon.presenter.utility;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.kumoricon.view.utility.CloseOutTillView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

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
            output.append("User ID: " + currentUser.getId() + " (" + currentUser.getUsername() + ")\n");
            output.append(currentUser.getFirstName() + " " + currentUser.getLastName() + "\n");
            output.append(LocalDateTime.now() + "\n");
            output.append("--------------------------------------------------------------------------------\n");
            output.append("Session Number: " + currentUser.getSessionNumber() + "\n\n");

            List<Object[]> results = orderRepository.getSessionOrderCountsAndTotals(
                    currentUser.getId(), currentUser.getSessionNumber());
            output.append(String.format("%-40s\t%s\t%s\n", "Payment Type", "Count", "Total"));
            for (Object[] line : results) {
                output.append(String.format("%-40s\t%5d\t$%8.2f\n",
                        getPaymentType((Integer)line[0]), line[1], line[2]));
            }
            output.append("--------------------------------------------------------------------------------\n");

            currentUser.setSessionNumber(currentUser.getSessionNumber() + 1);
            userRepository.save(currentUser);
            output.append("Session closed. New session number is: ");
            output.append(currentUser.getSessionNumber());
            view.showData(output.toString());
            // Todo: Print report (to which printer?) as well
        }
    }

    private String getPaymentType(Integer typeId) {
        Order.PaymentType[] orderTypes = Order.PaymentType.values();
        return orderTypes[typeId].toString();
    }
}
