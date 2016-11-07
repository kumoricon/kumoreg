package org.kumoricon.site.report.export;

import com.vaadin.server.StreamResource;
import org.kumoricon.site.report.export.data.AttendeeTSVExport;
import org.kumoricon.site.report.export.data.TillTSVExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class ExportPresenter {
    @Autowired
    private AttendeeTSVExport attendeeTSVExport;

    @Autowired
    private TillTSVExport tillTSVExport;

    private static final Logger log = LoggerFactory.getLogger(ExportPresenter.class);


    public ExportPresenter() {
    }


    public StreamResource createAttendeeExport(ExportView view) {
        log.info("{} downloaded Attendee Export", view.getCurrentUser());
        return attendeeTSVExport.getStream();
    }

    public StreamResource createTillExport(ExportView view) {
        log.info("{} downloaded Till Export", view.getCurrentUser());
        return tillTSVExport.getStream();
    }
}
