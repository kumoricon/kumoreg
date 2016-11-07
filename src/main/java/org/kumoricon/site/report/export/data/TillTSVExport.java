package org.kumoricon.site.report.export.data;

import com.vaadin.server.StreamResource;
import org.kumoricon.model.order.Order;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class TillTSVExport extends BaseTSVExport implements Export {
    private static String FILENAME="till.csv";

    private OrderRepository orderRepository;
    private UserRepository userRepository;

    @Autowired
    public TillTSVExport(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private String buildHeader() {
        String header = "User\t" +
                "Session\t" +
                "Earliest Transaction\t" +
                "Latest Transaction\t" +
                "Payment Type\t" +
                "Orders\t" +
                "Total\t" +
                "\n";
        return header;
    }

    private String buildTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildHeader());

        List<Object[]> results = orderRepository.getAllOrderCountsAndTotals();
        for (Object[] line : results) {
            sb.append(String.format("%s %s (%s: %s)\t", line[2], line[3], line[0], line[1]));
            sb.append(String.format("%s\t", line[4]));
            sb.append(String.format("%s\t", line[5]));
            sb.append(String.format("%s\t", line[6]));
            sb.append(String.format("%s\t", Order.PaymentType.fromInteger((Integer)line[7]).toString()));
            sb.append(String.format("%s\t", line[8]));
            sb.append(String.format("$%s\t", line[9]));
            sb.append("\n");
        }

        return sb.toString();
    }

    public StreamResource getStream() {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream () {
                String output = buildTable();
                return new ByteArrayInputStream(output.getBytes(Charset.forName("UTF-8")));
            }
        }, getFilename());
    }

    public String getFilename() {
        return FILENAME;
    }


}
