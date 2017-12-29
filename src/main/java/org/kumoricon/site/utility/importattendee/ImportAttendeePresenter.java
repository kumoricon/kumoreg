package org.kumoricon.site.utility.importattendee;


import com.vaadin.v7.ui.Upload;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.model.order.OrderRepository;
import org.kumoricon.model.session.SessionService;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


@Controller
@Scope("request")
public class ImportAttendeePresenter {
    private static final Logger log = LoggerFactory.getLogger(ImportAttendeePresenter.class);

    private final SessionService sessionService;
    private final OrderRepository orderRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private ImportAttendeeView view;

    @Autowired
    public ImportAttendeePresenter(SessionService sessionService, OrderRepository orderRepository, BadgeRepository badgeRepository, UserRepository userRepository) {
        this.sessionService = sessionService;
        this.orderRepository = orderRepository;
        this.badgeRepository = badgeRepository;
        this.userRepository = userRepository;
    }

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
            AttendeeImporterService importer = new AttendeeImporterService(sessionService, orderRepository, badgeRepository, userRepository);
            view.clearStatus();
            String result;

            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), UTF_8);
                result = importer.importFromJSON(reader, view.getCurrentUser());
//                result = importer.importFromTSV(reader, view.getCurrentUser());
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
    }

    public void setView(ImportAttendeeView view) { this.view = view; }
}

