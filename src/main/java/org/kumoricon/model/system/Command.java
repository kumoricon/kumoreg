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
            /* TODO validate command */

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); /* Have errors and output in the same stream for simplicity */

            // Validate the given directory
            if (directory != "") {
                /* TODO validate directory */
                pb.directory(new File(directory));
            }

            // Run the command
            Process p = pb.start();
            try {
                final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
                r.lines().iterator().forEachRemaining(sj::add);
                status = sj.toString();
                final int exitValue = p.waitFor();

                // The command was successful
                if (exitValue == 0) {
                    if (reportSuccess) { status = "Command completed successfully. Output: " + status; }
                }

                // The command was not successful
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
            }

            // The command was interrupted
            catch (InterruptedException e) {
                status = e.getMessage();
            }

            p.destroy();
        }

        // There was an I/O error
        catch (final IOException e) {
            status = e.getMessage();
        }

        return status;
    }

}
