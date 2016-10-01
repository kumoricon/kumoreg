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

    // Column widths, not counting padding characters (a space on either side).
    private static final int payTypeWidth = 30;
    private static final int countWidth = 7;
    private static final int totalsWidth = 12;
    private static final int separatorWidth = 3; // width of " | "
    // Width of the inside of a table line, as one column.
    private static final int innerWidth = payTypeWidth + separatorWidth + countWidth + separatorWidth + totalsWidth;

    private static String lineSep;

    public TillReportPresenter() {
        if (lineSep == null) {
            StringBuilder line = new StringBuilder();
            line.append('|');
            for (int i = 0; i != payTypeWidth + 2; ++i) {
                line.append('-');
            }
            line.append('|');
            for (int i = 0; i != countWidth + 2; ++i) {
                line.append('-');
            }
            line.append('|');
            for (int i = 0; i != totalsWidth + 2; ++i) {
                line.append('-');
            }
            line.append('|');
            lineSep = line.toString();
        }
    }

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

        Integer savedUid = -1;
        BigInteger totalCount = BigInteger.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;
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

            // If this is a row for a new user, print a new table header (and footer for previous)
            if (! savedUid.equals(uid)) {
                if (! savedUid.equals(-1)) {
                    // wrap up previous user's table
                    output.append(tableFooter(totalCount, grandTotal));
                }
                output.append(tableHeader(uid, userName, firstName, lastName));

                savedUid = uid;
                totalCount = BigInteger.ZERO;
                grandTotal = BigDecimal.ZERO;
            }
            else {
                // Add a blank line within the table output
                output.append(String.format("| %-*c | %*c | %*c |", payTypeWidth, ' ',
                        countWidth, ' ', totalsWidth, ' '));
            }
            totalCount = totalCount.add(count);
            grandTotal = grandTotal.add(total);

            /*
            output.append(String.format("User ID: %d (%s)\n", uid, userName));
            output.append(String.format("%s %s\n", firstName, lastName));
            */
            // Session info line
            String sessionStamp = String.format("Session Number: %d (FIXME: DATE RANGE GOES HERE)", session);
            output.append(String.format("| %-" + innerWidth + "s |\n", sessionStamp));

            // Detail line
            String totalStr = String.format("$%8.2f", total);
            output.append(String.format("| %-" + payTypeWidth.toString()
                            + "s | %" + countWidth.toString()
                            + "d | %" + totalsWidth.toString() + "s |\n",
                    payType.toString(), count, totalStr));
        }
        output.append(tableFooter(totalCount, grandTotal));
        output.append("\n\nEND OF REPORT\n");

        view.showData(output.toString());
    }

    private String tableHeader(Integer uid, String userName, String firstName, String lastName) {
        StringBuilder output = new StringBuilder();
        output.append(String.format("%s (%s %s) #%d\n", userName, firstName, lastName, uid));
        output.append(String.format("| %-" + payTypeWidth.toString()
                        + "s | %" + countWidth.toString()
                        + "s | %" + totalsWidth.toString() + "s |\n",
                "Type", "Orders", "Total"));
        output.append(lineSep);
        return output.toString();
    }

    private String tableFooter(BigInteger totalCount, BigDecimal grandTotal) {
        StringBuilder output = new StringBuilder();
        output.append(lineSep);
        String grandTotalStr = String.format("$%8.2f", grandTotal);
        output.append(String.format("| %-*s | %*d | %*s |\n",
                payTypeWidth, "Grand Total",
                countWidth, totalCount,
                totalsWidth, grandTotalStr));
        output.append(lineSep);
        return output.toString();
    }
}
