package org.kumoricon.view.importAttendee;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import org.kumoricon.presenter.importAttendee.ImportAttendeePresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = ImportAttendeeView.VIEW_NAME)
public class ImportAttendeeView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "importAttendees";

    @Autowired
    private ImportAttendeePresenter handler;

    private Label instructions = new Label("Upload a Tab-separated file containing preregistered attendees:");
    private TextArea status = new TextArea("");

    @PostConstruct
    public void init() {
        handler.setView(this);
        setSpacing(true);
        setMargin(true);

        addComponent(instructions);
        ImportAttendeePresenter.UploadReceiver receiver = handler.getUploadReceiver();
        Upload upload = new Upload("Upload CSV file", receiver);
        upload.addSucceededListener(receiver);
        upload.addFailedListener(receiver);
        addComponent(upload);
        status.setWidth(500, Unit.PIXELS);
        status.setEnabled(false);
        addComponent(status);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    public void setHandler(ImportAttendeePresenter presenter) {
        this.handler = presenter;
    }

    public void appendStatus(String s) {
        status.setValue(status.getValue() + s + "\n");
    }

    public void clearStatus() { status.clear(); }
}
