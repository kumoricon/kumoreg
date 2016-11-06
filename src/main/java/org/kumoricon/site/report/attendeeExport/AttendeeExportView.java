package org.kumoricon.site.report.attendeeExport;

import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = AttendeeExportView.VIEW_NAME)
public class AttendeeExportView extends BaseView implements View {
    public static final String VIEW_NAME = "attendeeExportReport";
    public static final String REQUIRED_RIGHT = "view_attendee_export";

    @Autowired
    private AttendeeExportPresenter handler;
    protected FileDownloader fileDownloader;

    private Button download = new Button("Download");

    @PostConstruct
    public void init() {
        addComponent(download);
//        FileDownloader fileDownloader = new FileDownloader(handler.createExportContent(this)), "export.xlsx"));
//        fileDownloader.extend(download);
        FileDownloader downloader = new FileDownloader(handler.createExportContent(this));
        downloader.extend(download);
    }

    public String getRequiredRight() {
        return REQUIRED_RIGHT;
    }
}
