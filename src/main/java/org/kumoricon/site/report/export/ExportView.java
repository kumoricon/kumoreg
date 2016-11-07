package org.kumoricon.site.report.export;

import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ExportView.VIEW_NAME)
public class ExportView extends BaseView implements View {
    public static final String VIEW_NAME = "exportData";
    public static final String REQUIRED_RIGHT = "view_export";

    @Autowired
    private ExportPresenter handler;

    private Button attendeeExportBtn = new Button("Download Attendee list as TSV");
    private Button tillExportBtn = new Button("Download Till report as TSV");

    @PostConstruct
    public void init() {
        addComponent(attendeeExportBtn);
        FileDownloader attendeeDownloader = new FileDownloader(handler.createAttendeeExport(this));
        attendeeDownloader.extend(attendeeExportBtn);

        addComponent(tillExportBtn);
        FileDownloader tillDownloader = new FileDownloader(handler.createTillExport(this));
        tillDownloader.extend(tillExportBtn);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
