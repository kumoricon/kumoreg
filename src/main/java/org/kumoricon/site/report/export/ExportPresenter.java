package org.kumoricon.site.report.export;

import com.vaadin.server.StreamResource;
import org.kumoricon.site.report.export.data.AttendeeTSVExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class ExportPresenter {
    @Autowired
    private AttendeeTSVExport attendeeTSVExport;

    private static final Logger log = LoggerFactory.getLogger(ExportPresenter.class);


    public ExportPresenter() {
    }


    public StreamResource createAttendeeExport(ExportView view) {
        log.info("{} downloaded Attendee Export", view.getCurrentUser());
        return attendeeTSVExport.getStream();
    }
}
