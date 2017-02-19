package org.kumoricon.site.tillsession;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

public class ReportWindow extends Window {

    private final Panel reportPanel = new Panel();
    private final Label lblReport = new Label();
    private final Button btnClose = new Button("Cancel");

    public ReportWindow(String title, String report) {
        super(title);

        setIcon(FontAwesome.FILE_PDF_O);
        setModal(true);
        setClosable(true);
        center();
        setWidth("70%");
        setHeight("80%");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(btnClose);

        lblReport.setContentMode(ContentMode.PREFORMATTED);
        lblReport.setValue(report);
        reportPanel.setContent(lblReport);
        reportPanel.setWidth("100%");
        reportPanel.setHeight("600");
        lblReport.setHeightUndefined();
        lblReport.setWidthUndefined();
        verticalLayout.addComponent(reportPanel);

        setContent(verticalLayout);


        btnClose.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnClose.addClickListener((Button.ClickListener) clickEvent -> {
            this.close();
        });
    }
}