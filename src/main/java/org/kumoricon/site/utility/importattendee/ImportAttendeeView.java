package org.kumoricon.site.utility.importattendee;

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import org.kumoricon.BaseGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ImportAttendeeView.VIEW_NAME)
public class ImportAttendeeView extends BaseGridView implements View {
    public static final String VIEW_NAME = "importAttendees";
    public static final String REQUIRED_RIGHT = "import_pre_reg_data";

    private final ImportAttendeePresenter handler;

    private final Label instructions = new Label("Upload a data file containing pre-registered attendees:");
    private final TextArea status = new TextArea("");

    @Autowired
    public ImportAttendeeView(ImportAttendeePresenter handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        handler.setView(this);

        setColumns(3);
        setRows(2);
        setColumnExpandRatio(1, 5);


        addComponent(instructions, 0, 0);
        ImportAttendeePresenter.UploadReceiver receiver = handler.getUploadReceiver();
        Upload upload = new Upload("Upload JSON file", receiver);
        upload.addSucceededListener(receiver);
        upload.addFailedListener(receiver);
        addComponent(upload, 0, 1);
        status.setSizeFull();
        status.setWidth(600, Sizeable.Unit.PIXELS);
        status.setEnabled(false);
        addComponent(status, 1, 1);
    }

    public void appendStatus(String s) {
        status.setValue(status.getValue() + s + "\n");
    }

    public void clearStatus() { status.clear(); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
