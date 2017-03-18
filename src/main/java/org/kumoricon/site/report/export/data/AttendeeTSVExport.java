package org.kumoricon.site.report.export.data;

import com.vaadin.server.StreamResource;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class AttendeeTSVExport extends BaseTSVExport implements Export {
    private static final String FILENAME="attendees.csv";

    private final AttendeeRepository attendeeRepository;

    @Autowired
    public AttendeeTSVExport(AttendeeRepository repository) {
        this.attendeeRepository = repository;
    }

    private String buildHeader() {
        return "ID\t" +
               "First Name\t" +
               "Last Name\t" +
               "Fan Name\t" +
               "Badge Number\t" +
               "ZIP\t" +
               "Country\t" +
               "Phone Number\t" +
               "Email\t" +
               "Birth Date\t" +
               "Emergency Contact\t" +
               "Emergency Phone\t" +
               "Parent Name\t" +
               "Parent Phone\t" +
               "Parent Form Rec'd?\t" +
               "Preregistered\t" +
               "Badge Type\t" +
               "Checked In?\t" +
               "Check In Time\t" +
               "Paid?\t" +
               "Paid Amount\t" +
               "\n";
    }

    private String buildTable(List<Attendee> data) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildHeader());

        for (Attendee attendee : data) {
            sb.append(format(attendee.getId().toString()));
            sb.append(format(attendee.getFirstName()));
            sb.append(format(attendee.getLastName()));
            sb.append(format(attendee.getFanName()));
            sb.append(format(attendee.getBadgeNumber()));
            sb.append(format(attendee.getZip()));
            sb.append(format(attendee.getCountry()));
            sb.append(format(attendee.getPhoneNumber()));
            sb.append(format(attendee.getEmail()));
            sb.append(format(attendee.getBirthDate()));
            sb.append(format(attendee.getEmergencyContactFullName()));
            sb.append(format(attendee.getEmergencyContactPhone()));
            sb.append(format(attendee.getParentFullName()));
            sb.append(format(attendee.getParentPhone()));
            sb.append(format(attendee.getParentFormReceived()));
            sb.append(format(attendee.isPreRegistered()));
            sb.append(format(attendee.getBadge().getName()));
            sb.append(format(attendee.getCheckedIn()));
            sb.append(format(attendee.getCheckInTime()));
            sb.append(format(attendee.getPaid()));
            sb.append(format(attendee.getPaidAmount()));
            sb.append("\n");
        }

        return sb.toString();
    }

    public StreamResource getStream() {
        return new StreamResource((StreamResource.StreamSource) () -> {
            String output = buildTable(attendeeRepository.findAll());
            return new ByteArrayInputStream(output.getBytes(Charset.forName("UTF-8")));
        }, getFilename());
    }

    public String getFilename() {
        return FILENAME;
    }


}
