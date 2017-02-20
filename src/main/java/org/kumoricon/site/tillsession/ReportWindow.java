package org.kumoricon.site.tillsession;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportWindow extends Window {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_H_mm_ss");

    private final Panel reportPanel = new Panel();
    private final Label lblReport = new Label();
    private final Button btnDownload = new Button("Download");
    private final Button btnClose = new Button("Close Window");
    private final String title;

    public ReportWindow(String title, String report) {
        super(title);

        this.title = title;

        buildWindowLayout();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(buildButtonBar());
        verticalLayout.addComponent(buildReportPanel(report));

        setContent(verticalLayout);

        FileDownloader downloader = new FileDownloader(getDownloadStream());
        downloader.extend(btnDownload);
    }

    private Panel buildReportPanel(String report) {
        lblReport.setContentMode(ContentMode.HTML);
        lblReport.setValue(report);
        reportPanel.setContent(lblReport);
        reportPanel.setWidth("100%");
        reportPanel.setHeight("600");
        lblReport.setHeightUndefined();
        lblReport.setWidthUndefined();
        return reportPanel;
    }

    private HorizontalLayout buildButtonBar() {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnDownload);
        buttons.addComponent(btnClose);

        btnClose.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnClose.addClickListener((Button.ClickListener) clickEvent -> this.close());

        return buttons;
    }

    private void buildWindowLayout() {
        setIcon(FontAwesome.FILE_PDF_O);
        setModal(true);
        setClosable(true);
        center();
        setWidth("70%");
        setHeight("80%");
    }

    private StreamResource getDownloadStream() {
        return new StreamResource((StreamResource.StreamSource) () -> {
            String output = lblReport.getValue();
            if (output == null) { output = "Empty report"; }
            return new ByteArrayInputStream(output.getBytes(Charset.forName("UTF-8")));
        }, generateFilename(title));
    }

    private static String generateFilename(String name) {
        if (name == null) { name = "file"; }
        return name.replace(' ', '_') +
                "_-_" +
                LocalDateTime.now().format(DATE_TIME_FORMATTER) +
                ".html";
    }
}