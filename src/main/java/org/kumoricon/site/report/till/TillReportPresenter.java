package org.kumoricon.site.report.till;

import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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

        // Header
        output.append("<table border=\"1\" cellpadding=\"2\"><tr>");
        output.append("<td>User</td>");
        output.append("<td>Session</td>");
        output.append("<td>Earliest Transaction</td>");
        output.append("<td>Latest Transaction</td>");
        output.append("<td>Payment Type</td>");
        output.append("<td>Orders</td>");
        output.append("<td>Amount</td>");
        output.append("</tr>");

        String lastUser = "";

        for (Object[] line : results) {
            if (!lastUser.equals(line[1])) {    // blank line after each user
                lastUser = line[1].toString();
                output.append("<tr><td colspan=\"7\">&nbsp;</td></tr>");
            }
            output.append("<tr>");
            output.append(String.format("<td>%s %s (%s: %s)</td>", line[2], line[3], line[0], line[1]));
            output.append(String.format("<td align=\"right\">%s</td>", line[4]));
            output.append(String.format("<td align=\"right\">%s</td>", line[5]));
            output.append(String.format("<td align=\"right\">%s</td>", line[6]));
            output.append(String.format("<td align=\"right\">%s</td>", Order.PaymentType.fromInteger((Integer)line[7]).toString()));
            output.append(String.format("<td align=\"right\">%s</td>", line[8]));
            output.append(String.format("<td align=\"right\">$%s</td>", line[9]));
            output.append("</tr>");
        }

        output.append("</table>");
        view.showData(output.toString());
    }
}
