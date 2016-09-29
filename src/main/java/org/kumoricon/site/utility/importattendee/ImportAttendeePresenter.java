package org.kumoricon.site.utility.importattendee;


import com.vaadin.ui.Upload;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ImportAttendeePresenter.class);

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
                log.error("When importing attendees, could not open file {}", e.getMessage(), e);
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
                FileReader reader = new FileReader(file);
                result = importer.importFromTSV(reader, view.getCurrentUser());
                reader.close();
            } catch (Exception e) {
                log.error("Error importing attendees: ", e.getMessage());
                result = e.getMessage();
            } finally {
                if (!file.delete()) {
                    log.error("Error deleting file " + file.getAbsolutePath());
                }
            }
            view.appendStatus(result);

            // Refresh user info in UI
            view.setLoggedInUser(userRepository.findOne(view.getCurrentUser().getId()));

        }

        public void uploadFailed(Upload.FailedEvent event) {
            view.notifyError(event.toString());
        }
    };

    public ImportAttendeeView getView() { return view; }
    public void setView(ImportAttendeeView view) { this.view = view; }
}

