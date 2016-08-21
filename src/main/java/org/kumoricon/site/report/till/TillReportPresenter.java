package org.kumoricon.site.report.till;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Scope("request")
public class TillReportPresenter {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    public static String getTillReportStr(User currentUser, Integer sessionNum, List<Object[]> results) {
        StringBuilder output = new StringBuilder();
        output.append(String.format("User ID: %d (%s)\n", currentUser.getId(), currentUser.getUsername()));
        output.append(String.format("%s %s\n", currentUser.getFirstName(), currentUser.getLastName()));
        output.append(String.format("%s\n", LocalDateTime.now()));
        output.append("--------------------------------------------------------------------------------\n");
        output.append(String.format("Session Number: %d\n\n", sessionNum));

        output.append(String.format("%-40s\t%s\t%s\n", "Payment Type", "Count", "Total"));
        for (Object[] line : results) {
            output.append(String.format("%-40s\t%5d\t$%8.2f\n",
                    Order.PaymentType.fromInteger((Integer)line[0]).toString(), line[1], line[2]));
        }
        output.append("--------------------------------------------------------------------------------\n");

        return output.toString();
    }

    public void showAllTills(TillReportView view) {
        StringBuilder output = new StringBuilder();
        List<Object[]> results = orderRepository.getAllOrderCountsAndTotals();

        output.append(String.format("%s\n", LocalDateTime.now()));

        output.append("--------------------------------------------------------------------------------\n");

        for (Object[] line : results) {
            int i=0;
            Integer uid = (Integer)line[i++];
            String userName = (String)line[i++];
            String firstName = (String)line[i++];
            String lastName = (String)line[i++];
            Integer session = (Integer)line[i++];
            Order.PaymentType payType = Order.PaymentType.fromInteger((Integer)line[i++]);
            BigInteger count = (BigInteger)line[i++];
            BigDecimal total = (BigDecimal)line[i++];

            output.append(String.format("User ID: %d (%s)\n", uid, userName));
            output.append(String.format("%s %s\n", firstName, lastName));
            output.append(String.format("Session Number: %d\n\n", session));
            output.append(String.format("%-40s\t%s\t%s\n", "Payment Type", "Count", "Total"));
            output.append(String.format("%-40s\t%5d\t$%8.2f\n", payType.toString(), count, total));
        }
        output.append("--------------------------------------------------------------------------------\n");

        view.showData(output.toString());
    }
}
