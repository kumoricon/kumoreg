package org.kumoricon.site.utility.importattendee;


import com.vaadin.ui.Upload;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.UserRepository;

import org.kumoricon.site.utility.importattendee.AttendeeImporterService;
import org.kumoricon.site.utility.importattendee.ImportAttendeeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;


@Controller
@Scope("request")
public class ImportAttendeePresenter {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

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
            FileOutputStream fos; // Stream to write to
            try {
                // Open the file for writing.
                file = new File("/tmp/" + filename);
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                view.notifyError("Could not open file<br/>" + e.getMessage());
                return null;
            }
            return fos; // Return the output stream to write to
        }

        public void uploadSucceeded(Upload.SucceededEvent event) {
            AttendeeImporterService importer = new AttendeeImporterService(attendeeRepository, orderRepository, badgeRepository, userRepository);
            view.clearStatus();
            String result = "";

            try {
                result = importer.importFromTSV(new FileReader(file), view.getCurrentUser());
            } catch (Exception e) {
                result = e.getMessage();
            } finally {
                file.delete();
            }
            view.appendStatus(result);

        }

        public void uploadFailed(Upload.FailedEvent event) {
            view.notifyError(event.toString());
        }
    };

    public ImportAttendeeView getView() { return view; }
    public void setView(ImportAttendeeView view) { this.view = view; }
}

