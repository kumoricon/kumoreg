package org.kumoricon.view.importAttendee;

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import org.kumoricon.presenter.importAttendee.ImportAttendeePresenter;
import org.kumoricon.view.BaseView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ImportAttendeeView.VIEW_NAME)
public class ImportAttendeeView extends BaseView implements View {
    public static final String VIEW_NAME = "importAttendees";
    public static final String REQUIRED_RIGHT = "import_pre_reg_data";

    @Autowired
    private ImportAttendeePresenter handler;

    private Label instructions = new Label("Upload a Tab-separated file containing preregistered attendees:");
    private TextArea status = new TextArea("");

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSizeFull();

        addComponent(instructions);
        ImportAttendeePresenter.UploadReceiver receiver = handler.getUploadReceiver();
        Upload upload = new Upload("Upload CSV file", receiver);
        upload.addSucceededListener(receiver);
        upload.addFailedListener(receiver);
        addComponent(upload);
        status.setSizeFull();
        status.setWidth(600, Sizeable.Unit.PIXELS);
        status.setEnabled(false);
        addComponent(status);
        setExpandRatio(status, 1.0f);
    }

    public void setHandler(ImportAttendeePresenter presenter) {
        this.handler = presenter;
    }

    public void appendStatus(String s) {
        status.setValue(status.getValue() + s + "\n");
    }

    public void clearStatus() { status.clear(); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }
}
