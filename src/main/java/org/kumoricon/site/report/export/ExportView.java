package org.kumoricon.site.report.export;

import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import org.kumoricon.BaseGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ExportView.VIEW_NAME)
public class ExportView extends BaseGridView implements View {
    public static final String VIEW_NAME = "exportData";
    public static final String REQUIRED_RIGHT = "view_export";

    private final ExportPresenter handler;

    private final Button attendeeExportBtn = new Button("Download Attendee list as TSV");
    private final Button tillExportBtn = new Button("Download Till report as TSV");

    @Autowired
    public ExportView(ExportPresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        setColumns(1);
        setRows(2);

        addComponent(attendeeExportBtn, 0, 0);
        FileDownloader attendeeDownloader = new FileDownloader(handler.createAttendeeExport(this));
        attendeeDownloader.extend(attendeeExportBtn);

        addComponent(tillExportBtn,0, 1);
        FileDownloader tillDownloader = new FileDownloader(handler.createTillExport(this));
        tillDownloader.extend(tillExportBtn);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
