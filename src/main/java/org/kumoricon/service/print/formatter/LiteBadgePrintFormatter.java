package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.kumoricon.model.attendee.Attendee;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class LiteBadgePrintFormatter implements BadgePrintFormatter {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    private Integer xOffset = 0;
    private Integer yOffset = 0;

    /**
     * Generates a PDF containing badges ready to be printed. Lite badges have no badge name or number.
     * @param attendees
     */
    public LiteBadgePrintFormatter(List<Attendee> attendees) {
        this(attendees, 0, 0);
    }

    /**
     * Generates a PDF containing badges ready to be printed. Lite badges have no badge name or number.
     * @param attendees Attendees to generate badges for
     * @param xOffset Horizontal offset in points (1/72 inch)
     * @param yOffset Vertical offset in points (1/72 inch)
     */
    public LiteBadgePrintFormatter(List<Attendee> attendees, Integer xOffset, Integer yOffset) {
        PDDocument document;
        this.xOffset = (xOffset == null) ? 0 : xOffset;
        this.yOffset = (yOffset == null) ? 0 : yOffset;

        try {
            document = new PDDocument();
            for (Attendee attendee : attendees) {
                PDPage currentPage = generatePage(attendee, document);
                document.addPage( currentPage );
            }

            document.save(os);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private PDPage generatePage(Attendee attendee, PDDocument document) throws IOException {
        PDPage page = new PDPage(new PDRectangle(612f, 396f));
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Positions are measured from the bottom left corner of the page at 72 DPI

        // Draw real name
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(220+xOffset, 175+yOffset);
        contentStream.setFont( font, 24 );
        contentStream.drawString(attendee.getFirstName() + " " + attendee.getLastName());
        contentStream.endText();

        // Draw age color stripe
        String stripeText = "VOID";
        if (attendee.getCurrentAgeRange() != null) {
            contentStream.setNonStrokingColor(Color.decode(attendee.getCurrentAgeRange().getStripeColor()));
            stripeText = attendee.getCurrentAgeRange().getStripeText();
        } else {
            contentStream.setNonStrokingColor(Color.black);
        }
        contentStream.fillRect(150+xOffset, 89+yOffset, 310, 44);

        contentStream.setLineWidth(0.5f);
        contentStream.beginText();
        contentStream.setFont(font, 24);
        contentStream.setNonStrokingColor(Color.white);
        contentStream.setStrokingColor(Color.black);
        contentStream.moveTextPositionByAmount(297+xOffset, 102+yOffset);
        contentStream.appendRawCommands("2 Tr ");       // Set text rendering mode

        Float ageRangeWidth = ((font.getStringWidth(stripeText) / 1000.0f) * 18) / 2;
        contentStream.moveTextPositionByAmount(-ageRangeWidth, 0);
        contentStream.drawString(stripeText);
        contentStream.endText();

        contentStream.close();

        return page;
    }


    @Override
    public InputStream getStream() {
        // Here we return the pdf contents as a byte-array
        return new ByteArrayInputStream(os.toByteArray());
    }

}
