package org.kumoricon.site.report.attendeeExport;

import com.vaadin.server.StreamResource;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Controller
public class AttendeeExportPresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    private static final Logger log = LoggerFactory.getLogger(AttendeeExportPresenter.class);


    public AttendeeExportPresenter() {
    }

    private static String format(Boolean input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    private static String format(String input) {
        if (input == null || input.trim().equals("")) {
            return "\t";
        } else {
            return input.trim() + "\t";
        }
    }

    private static String format(LocalDate input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    private static String format(Date input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    private static String format(BigDecimal input) {
        if (input == null) return "\t";
        return input.toString() + "\t";
    }

    private String buildTable(List<Attendee> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID\t");
        sb.append("First Name\t");
        sb.append("Last Name\t");
        sb.append("Badge Name\t");
        sb.append("Badge Number\t");
        sb.append("ZIP\t");
        sb.append("Country\t");
        sb.append("Phone Number\t");
        sb.append("Email\t");
        sb.append("Birthdate\t");
        sb.append("Emergency Contact\t");
        sb.append("Emergency Phone\t");
        sb.append("Parent Name\t");
        sb.append("Parent Phone\t");
        sb.append("Parent Form Rec'd?\t");
        sb.append("Preregistered\t");
        sb.append("Badge Type\t");
        sb.append("Checked In?\t");
        sb.append("Check In Time\t");
        sb.append("Paid?\t");
        sb.append("Paid Amount\t");
        sb.append("\n");

        for (Attendee attendee : data) {
            sb.append(format(attendee.getId().toString()));
            sb.append(format(attendee.getFirstName()));
            sb.append(format(attendee.getLastName()));
            sb.append(format(attendee.getBadgeName()));
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

    public StreamResource createExportContent(AttendeeExportView view) {
        log.info("{} downloaded Attendee Export", view.getCurrentUser());
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream () {
                String output = buildTable(attendeeRepository.findAll());
                return new ByteArrayInputStream(output.getBytes(Charset.forName("UTF-8")));
            }
        }, "attendees.csv");
    }
}
