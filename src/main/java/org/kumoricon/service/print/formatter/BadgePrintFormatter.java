package org.kumoricon.service.print.formatter;

import com.vaadin.server.StreamResource;

import java.io.InputStream;

public interface BadgePrintFormatter extends StreamResource.StreamSource {
    @Override
    InputStream getStream();
}
