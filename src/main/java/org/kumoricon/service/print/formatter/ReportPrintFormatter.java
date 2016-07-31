package org.kumoricon.service.print.formatter;


import com.vaadin.server.StreamResource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ReportPrintFormatter implements StreamResource.StreamSource {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    private PDFont font = PDType1Font.COURIER;
    private final Integer fontSize = 11;
    private Integer linesPerPage = 1;   // Will be recalculated below

    private Integer xOffset;            // Horizontal offset in points (1/72 inch)
    private Integer yOffset;            // Vertical offset in points (1/72 inch)

    /**
     * Generates a PDF of inputText on a monospaced font. Assumes 5.5" x 8.5" page. Handles newlines
     * and splitting text over multiple pages.
     * @param inputText text to print
     */
    public ReportPrintFormatter(String inputText) {
        this(inputText, 0, 0);
    }

    public ReportPrintFormatter(String inputText, Integer xOffset, Integer yOffset) {
        if (inputText == null) { return; }
        this.xOffset = (xOffset == null) ? 0 : xOffset;
        this.yOffset = (yOffset == null) ? 0 : yOffset;

        PDDocument document;
        String[] lines = inputText.split("\n");
        linesPerPage = 324 / (fontSize);        // 72 DPI

        try {
            document = new PDDocument();
            Integer count = 0;

            do {
                PDPage currentPage = generatePage(lines, count, document);
                document.addPage(currentPage);
                count += linesPerPage;
            } while (count < lines.length);

            document.save(os);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PDPage generatePage(String[] lines, int startAt, PDDocument document) throws IOException {
        PDPage page = new PDPage(new PDRectangle(612f, 396f));
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Positions are measured from the bottom left corner of the page at 72 DPI

        // Add report text to page
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(36+xOffset, 360+yOffset);
        contentStream.setFont(font, fontSize);
        int lineNumber = startAt;
        while (lineNumber < startAt + linesPerPage && lineNumber < lines.length) {
            contentStream.drawString(lines[lineNumber]);
            contentStream.moveTextPositionByAmount(0, (-1*fontSize));
            lineNumber += 1;
        }
        contentStream.endText();

        contentStream.close();

        return page;

    }

    @Override
    /**
     * Returns stream containing PDF
     */
    public InputStream getStream() {
        return new ByteArrayInputStream(os.toByteArray());
    }

}
