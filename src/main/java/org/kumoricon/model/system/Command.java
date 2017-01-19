package org.kumoricon.model.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Encapsulates running commands at the system level
 */
public class Command {

    public static String run(List<String> command, String directory, boolean reportSuccess) {
        String status = "";
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            if (directory != "") {
                /* TODO validate directory */
                pb.directory(new File(directory));
            }

            Process p = pb.start();
            try {
                final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
                r.lines().iterator().forEachRemaining(sj::add);
                status = sj.toString();
                final int exitValue = p.waitFor();
                if (exitValue == 0) { if (reportSuccess) { status = "Command completed successfully. Output: " + status; } }
                else {
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        String errorString = "";
                        if ((errorString = b.readLine()) != null) {
                            status = errorString;
                        }
                    } catch (final IOException e) {
                        status = e.getMessage();
                    }
                }
            } catch (InterruptedException e) {
                status = e.getMessage();
            }
            p.destroy();
        } catch (final IOException e) {
            status = e.getMessage();
        }
        return status;
    }

}
