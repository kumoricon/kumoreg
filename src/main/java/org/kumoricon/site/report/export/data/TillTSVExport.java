package org.kumoricon.site.report.export.data;

import com.vaadin.server.StreamResource;
import org.kumoricon.model.session.Session;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class TillTSVExport extends BaseTSVExport implements Export {
    private final static String FILENAME="till.csv";
    private final SessionService sessionService;

    @Autowired
    public TillTSVExport(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    private String buildHeader() {
        return "User\t" +
               "Session\t" +
               "Start\t" +
               "End\t" +
               "Payment\n";
    }

    private String buildTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildHeader());

        List<Session> sessions = sessionService.getAllSessions();

        for (Session session : sessions) {
            String[] totals = sessionService.buildTextTotalsForSession(session).split("\n");
            User user = session.getUser();
            for (String total : totals) {
                sb.append(String.format("%s %s (%s: %s)\t",
                        user.getFirstName(), user.getLastName(), user.getId(), user.getUsername()));
                sb.append(String.format("%s\t", session.getId()));
                sb.append(String.format("%s\t", session.getStart()));
                sb.append(String.format("%s\t", session.getEnd()));
                sb.append(String.format("%s\t", total));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public StreamResource getStream() {
        return new StreamResource((StreamResource.StreamSource) () -> {
            String output = buildTable();
            return new ByteArrayInputStream(output.getBytes(Charset.forName("UTF-8")));
        }, getFilename());
    }

    public String getFilename() {
        return FILENAME;
    }
}
