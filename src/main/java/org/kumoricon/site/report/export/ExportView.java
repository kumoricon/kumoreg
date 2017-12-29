package org.kumoricon.site.report.export;

import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.VerticalLayout;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ExportView.VIEW_NAME)
public class ExportView extends BaseView implements View {
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
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setWidthUndefined();
        layout.addComponent(attendeeExportBtn);
        FileDownloader attendeeDownloader = new FileDownloader(handler.createAttendeeExport(this));
        attendeeDownloader.extend(attendeeExportBtn);

        layout.addComponent(tillExportBtn);
        FileDownloader tillDownloader = new FileDownloader(handler.createTillExport(this));
        tillDownloader.extend(tillExportBtn);

        layout.setComponentAlignment(attendeeExportBtn, Alignment.TOP_CENTER);
        layout.setComponentAlignment(tillExportBtn, Alignment.TOP_CENTER);
        addComponent(layout);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
