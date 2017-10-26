package org.kumoricon.scheduledtasks;

import com.google.gson.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.attendee.AttendeeRepository;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.scheduledtasks.staffimport.Action;
import org.kumoricon.scheduledtasks.staffimport.ImportFile;
import org.kumoricon.scheduledtasks.staffimport.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class StaffImport {
    private static final Logger log = LoggerFactory.getLogger(StaffImport.class);
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss.SSS");

    @Value("${kumoreg.staffImport.input}")
    private String inputPathString;
    @Value("${kumoreg.staffImport.finished}")
    private String finishedPathString;
    @Value("${kumoreg.staffImport.dlq}")
    private String dlqPathString;

    private boolean importEnabled = true;

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private Badge staffBadge;

    @Scheduled(fixedRate = 10000)
    public void staffImport() {
        createDirectories();
        if (!importEnabled) { return; }

        staffBadge = badgeRepository.findOneByNameIgnoreCase("Staff");
        processInputFiles();

    }

    private ImportFile loadFile(Path filePath) throws Exception {
        try (BufferedReader br = Files.newBufferedReader(filePath))
        {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            }).create();
            ImportFile importFile = gson.fromJson(br, ImportFile.class);
            br.close();
            return importFile;
        }
    }

    private void importData(ImportFile file) {
        if (file.persons != null) {
            for (Person person : file.persons) {log.info(person.nameOnId);
                String staffId = String.format("%s", person.id);
                List<Attendee> fromDatabase = attendeeRepository.findByStaffId(staffId);
                if (fromDatabase.size() > 0) {
                    Attendee attendee = fromDatabase.get(0);
                    boolean updated = person.updateAttendee(attendee);
                    if (updated) {
                        attendeeRepository.save(attendee);
                    }
                } else {
                    Attendee attendee = person.toAttendee();
                    attendee.setBadge(staffBadge);
                    attendeeRepository.save(attendee);
//                    log.info(attendee.toString());
                }
            }
        }

        if (file.actions != null) {
            for (Action action : file.actions) {
                for (String staffId : action.deleted) {
                    log.info("Deleting staff id {}", staffId);
                    List<Attendee> attendees = attendeeRepository.findByStaffId(staffId);
                    if (attendees.size() == 1) {
                        log.info("Deleting {}", attendees.get(0));
                        attendeeRepository.delete(attendees.get(0));
                    } else if (attendees.size() == 0) {
                        log.info("No Attendee with staff id {} not found, skipping", staffId);
                    } else {
                        log.error("While deleting staff, found more than one attendee with staff id {}. Skipping." , staffId);
                    }
                }
            }
        }
    }

    private void processInputFiles() {
        Path inputPath = Paths.get(inputPathString);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputPath, "*.{json}")) {
            int count = 0;
            int errors = 0;
            for (Path path : stream) {
                count += 1;
                if (staffBadge == null) {
                    log.warn("Warning, badge type Staff not found, aborting import");
                    return;
                }
                try {
                    ImportFile file = loadFile(path);
                    importData(file);
                    Path output = Paths.get(finishedPathString, getTimestamp() + "-" + path.getFileName().toString());
                    Files.move(path, output);
                } catch (Exception ex) {
                    log.error("Error processing file {}, moving to {}", path, dlqPathString, ex);
                    errors += 1;
                    Path output = Paths.get(dlqPathString, getTimestamp() + "-" + path.getFileName().toString());
                    Files.move(path, output);
                }
            }
            if (count > 0 || errors > 0) {
                log.info("Processed {} staff import file(s) with {} error(s)", count, errors);
            }
        } catch (IOException ex) {
            log.error("Error processing staff import file(s):", ex);
        }
    }

    private static String getTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(dateFormat);
    }

    private void createDirectories() {
        if (inputPathString == null || finishedPathString == null || dlqPathString == null) {
            importEnabled = false;
            return;
        }

        Path inputPath = Paths.get(inputPathString);
        Path finishedPath = Paths.get(finishedPathString);
        Path dlqPath = Paths.get(dlqPathString);
        try {
            if (!Files.isDirectory(inputPath)) {
                Files.createDirectories(inputPath);
                log.info("Created input directory {}", inputPath);
            }
            if (!Files.isDirectory(finishedPath)) {
                Files.createDirectories(finishedPath);
                log.info("Created finished directory {}", finishedPath);
            }
            if (!Files.isDirectory(dlqPath)) {
                Files.createDirectories(Paths.get(dlqPathString));
                log.info("Created dead letter queue directory {}", dlqPath);
            }
        } catch (IOException ex) {
            log.error("Staff import disabled. Error creating staff import paths: {}", ex.getMessage());
            importEnabled = false;
        }

        importEnabled = true;
    }

}