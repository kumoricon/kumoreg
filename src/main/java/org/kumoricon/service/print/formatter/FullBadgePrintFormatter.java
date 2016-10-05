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


public class FullBadgePrintFormatter implements BadgePrintFormatter {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    private Integer xOffset = 0;
    private Integer yOffset = 0;

    /**
     * Generates a PDF containing badges ready to be printed. Full badges have all fields.
     * @param attendees Attendees to generate badges for
     */
    public FullBadgePrintFormatter(List<Attendee> attendees) {
        this(attendees, 0, 0);
    }

    /**
     * Generates a PDF containing badges ready to be printed. Full badges have all fields.
     * @param attendees Attendees to generate badges for
     * @param xOffset Horizontal offset in points (1/72 inch)
     * @param yOffset Vertical offset in points (1/72 inch)
     */
    public FullBadgePrintFormatter(List<Attendee> attendees, Integer xOffset, Integer yOffset) {
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

    private class ResizeOptions {
        public PDFont      font = PDType1Font.HELVETICA_BOLD;
        public float       size = 14;
        public float       minFontSize = 10;
        public float       maxTextWidth = 0;
        public int         lines = 1;
        public float       lineSize = size * 1.3f;

        public ResizeOptions() {}
        public ResizeOptions(ResizeOptions other) {
            this.font = other.font;
            this.size = other.size;
            this.minFontSize = other.minFontSize;
            this.maxTextWidth = other.maxTextWidth;
            this.lines = other.lines;
            this.lineSize = other.lineSize;
        }
    }
    private void drawStringWithResizing(PDPageContentStream contentStream, float x, float y, String text, ResizeOptions optOrig) throws IOException {
        ResizeOptions opt = new ResizeOptions(optOrig);
        float textSize = opt.font.getStringWidth(text); // in thousandths of font pt size.
        float size = opt.size;

        // Calculate the correct font size, based on our restrictions
        if (textSize * (size/1000.0f) <= opt.maxTextWidth) {
            // Great! Fits already
        }
        else{
            size = opt.maxTextWidth * 1000.0f / textSize;
            if (size < opt.minFontSize) {
                // XXX: Bail. At least log that this happened.
                size = opt.minFontSize;
            }
        }

        // Actually draw the text
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(x, y);
        contentStream.setFont(opt.font, size);
        contentStream.drawString(text);
        contentStream.endText();
    }

    private PDPage generatePage(Attendee attendee, PDDocument document) throws IOException {
        PDPage page = new PDPage(new PDRectangle(612f, 396f));
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        ResizeOptions resizeOpt = new ResizeOptions();
        String badgeName = attendee.getBadgeName();
        String realName = attendee.getFirstName() + " " + attendee.getLastName();

        if (badgeName.matches("^\\s*$")) {
            badgeName = null;
        }
        String name = badgeName;

        // Positions are measured from the bottom left corner of the page at 72 DPI

        // Set up global transformation matrix according to xOffset, yOffset.
        // NOTE: if we want to support per-printer/per-client scaling, we would replace the 1's
        // below, with xScale and yScale.
        contentStream.concatenate2CTM(1, 0, 0, 1, xOffset, yOffset);

        // Draw main name (badge name if set, otherwise real name)
        resizeOpt.size = 24;
        resizeOpt.lines = 1;
        resizeOpt.maxTextWidth = 206;
        if (name == null) {
            name = realName;
        }
        drawStringWithResizing(contentStream, 240, 200, name, resizeOpt);

        // Draw real name if badge name set
        if (badgeName != null) {
            resizeOpt.size = 18;
            resizeOpt.minFontSize = 8;
            resizeOpt.lines = 1;
            resizeOpt.maxTextWidth = 166;
            drawStringWithResizing(contentStream, 280, 180, realName, resizeOpt);
        }

        // Draw badge type
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(200, 128);
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.drawString(attendee.getBadge().getDayText());
        contentStream.endText();


        // Draw badge number, right-aligned
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(450, 128);
        Float badgeNumberWidth = (PDType1Font.HELVETICA.getStringWidth(attendee.getBadgeNumber()) / 1000.0f) * 14;
        contentStream.moveTextPositionByAmount(-badgeNumberWidth, 0);
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.drawString(attendee.getBadgeNumber());
        contentStream.moveTextPositionByAmount(badgeNumberWidth, 0);
        contentStream.endText();

        // Draw age color stripe
        String stripeText = "VOID";
        if (attendee.getCurrentAgeRange() != null) {
            contentStream.setNonStrokingColor(Color.decode(attendee.getCurrentAgeRange().getStripeColor()));
            stripeText = attendee.getCurrentAgeRange().getStripeText();
        } else {
            contentStream.setNonStrokingColor(Color.black);
        }
        contentStream.fillRect(200, 100, 250, 25);

        contentStream.setLineWidth(0.5f);
        contentStream.beginText();
        contentStream.setFont(font, 18);
        contentStream.setNonStrokingColor(Color.white);
        contentStream.setStrokingColor(Color.black);
        contentStream.moveTextPositionByAmount(325, 105);
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
