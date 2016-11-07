package org.kumoricon.site.report.export.data;

import com.vaadin.server.StreamResource;

public interface Export {
    String getFilename();               // The filename as it will be sent to the browser
    String getMIMEType();               // For example, text/csv or text/plain
    StreamResource getStream();         // Returns the byte stream of the file
}
