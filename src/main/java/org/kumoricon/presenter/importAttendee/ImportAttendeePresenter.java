package org.kumoricon.presenter.importAttendee;


import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.view.importAttendee.ImportAttendeeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;


@Service
public class ImportAttendeePresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private ImportAttendeeView view;

    public ImportAttendeePresenter() {}

    public UploadReceiver getUploadReceiver() {
        return new UploadReceiver();
    }

    public class UploadReceiver implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener {
        public File file;

        public OutputStream receiveUpload(String filename,
                                          String mimeType) {
            // Create upload stream
            FileOutputStream fos = null; // Stream to write to
            try {
                // Open the file for writing.
                file = new File("/tmp/" + filename);
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file<br/>",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
            return fos; // Return the output stream to write to
        }

        public void uploadSucceeded(Upload.SucceededEvent event) {
//            Notification.show(file.getAbsolutePath() + " saved");
            AttendeeImporter importer = new AttendeeImporter(attendeeRepository, orderRepository, badgeRepository);
            view.clearStatus();
            String result = "";
            try {
                result = importer.importFromTSV(new FileReader(file));
            } catch (Exception e) {
                result = e.getMessage();
            } finally {
                file.delete();
            }
            view.appendStatus(result);

        }

        public void uploadFailed(Upload.FailedEvent event) {
            Notification.show(event.toString());
        }
    };
    public ImportAttendeeView getView() { return view; }
    public void setView(ImportAttendeeView view) { this.view = view; }
}

