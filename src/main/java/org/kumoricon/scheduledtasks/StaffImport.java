package org.kumoricon.scheduledtasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Scheduled(fixedRate = 10000)
    public void staffImport() {
        createDirectories();
        getInputFiles();
    }

    private List<Path> getInputFiles() {
        Path inputPath = Paths.get(inputPathString);

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(inputPath, "*.{json}");
            int count = 0;
            int errors = 0;
            for (Path path : stream) {
                count += 1;
                try {
                    Path output = Paths.get(finishedPathString, getTimestamp() + "-" + path.getFileName().toString());
                    Files.move(path, output);
                } catch (IOException ex) {
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
        return null;
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