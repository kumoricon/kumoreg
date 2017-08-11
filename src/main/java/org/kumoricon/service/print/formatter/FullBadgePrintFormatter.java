package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.BadgeType;

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
        public boolean     centered = true; // Should possibly be a Right | Left | Center enum?
        public int         lines = 1; // To support splitting into multiple lines. Currently unsupported.
        public float       lineSize = size * 1.3f;

        public ResizeOptions() {}
        public ResizeOptions(ResizeOptions other) {
            this.font = other.font;
            this.size = other.size;
            this.minFontSize = other.minFontSize;
            this.maxTextWidth = other.maxTextWidth;
            this.centered = other.centered;
            this.lines = other.lines;
            this.lineSize = other.lineSize;
        }
    }

    /**
     * Draws the given string, optionally supports scaling to fit.
     * @param contentStream Open stream to draw in to
     * @param x Left side of text, or center point of text if centered (1/72 inch)
     * @param y Bottom of text, in points (1/72 inch)
     * @param text Text to draw
     * @param optOrig Resize Options
     * @throws IOException Error generating PDF
     */
    private void drawStringWithResizing(PDPageContentStream contentStream, float x, float y, String text, ResizeOptions optOrig) throws IOException {
        ResizeOptions opt = new ResizeOptions(optOrig);
        float textSize = opt.font.getStringWidth(text); // in thousandths of font pt size.
        float size = opt.size;

        // If text size is greater than maximum width, recalculate the correct font size, based on our restrictions
        if (textSize * (size/1000.0f) > opt.maxTextWidth) {
            size = opt.maxTextWidth * 1000.0f / textSize;
            if (size < opt.minFontSize) {
                // We have utterly failed to fit the text with the minimum font size,
                // So we're forced to use that.
                size = opt.minFontSize;
            }
        }

        if (opt.centered) {
            x -= textSize * (size/(2*1000.0f));
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

        // Positions are measured from the bottom left corner of the page at 72 DPI

        // Set up global transformation matrix according to xOffset, yOffset.
        // NOTE: if we want to support per-printer/per-client scaling, we would replace the 1's
        // below, with xScale and yScale.
        contentStream.concatenate2CTM(1, 0, 0, 1, xOffset, yOffset);

        // Draw fields on badge depending on badge type
        if (attendee.getBadge().getBadgeType().equals(BadgeType.STAFF)) {
            drawStaffNames(contentStream, attendee);
            drawBadgeNumber(contentStream, attendee);
            drawAgeColorStripe(contentStream, font, attendee);
            drawBadgeType(contentStream, attendee);
        } else {
            drawMainNames(contentStream, attendee);
            drawBadgeNumber(contentStream, attendee);
            drawAgeColorStripe(contentStream, font, attendee);
            drawBadgeType(contentStream, attendee);
        }
        
        contentStream.close();

        return page;
    }

    private void drawBadgeType(PDPageContentStream contentStream, Attendee attendee) throws IOException {
        // Draw badge type in color stripe
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(167, 105);
        contentStream.drawString(attendee.getBadge().getBadgeTypeText());
        contentStream.endText();
    }

    private void drawAgeColorStripe(PDPageContentStream contentStream, PDFont font, Attendee attendee) throws IOException {
        // Draw age color stripe
        String stripeText = "VOID";
        if (attendee.getCurrentAgeRange() != null) {
            contentStream.setNonStrokingColor(Color.decode(attendee.getCurrentAgeRange().getStripeColor()));
            stripeText = attendee.getCurrentAgeRange().getStripeText();
        } else {
            contentStream.setNonStrokingColor(Color.black);
        }
        contentStream.fillRect(155, 92, 300, 45);

        contentStream.setLineWidth(0.5f);

        // Draw age range text in color stripe
        contentStream.beginText();
        contentStream.setFont(font, 32);
        contentStream.setNonStrokingColor(Color.white);
        contentStream.setStrokingColor(Color.black);
        contentStream.moveTextPositionByAmount(438, 105);
        contentStream.appendRawCommands("2 Tr ");       // Set text rendering mode
        Float ageRangeWidth = ((font.getStringWidth(stripeText) / 1000.0f) * 32);
        contentStream.moveTextPositionByAmount(-ageRangeWidth, 0);
        contentStream.drawString(stripeText);
        contentStream.endText();
    }

    private void drawBadgeNumber(PDPageContentStream contentStream, Attendee attendee) throws IOException {
        // Draw badge number, centered
        ResizeOptions resizeOpt = new ResizeOptions();
        resizeOpt.size = 14;
        resizeOpt.maxTextWidth = 38;
        drawStringWithResizing(contentStream, 407, 145, attendee.getBadgeNumber(), resizeOpt);
    }

    private void drawMainNames(PDPageContentStream contentStream, Attendee attendee) throws IOException {
        ResizeOptions resizeOpt = new ResizeOptions();
        resizeOpt.size = 24;
        resizeOpt.lines = 1;
        resizeOpt.maxTextWidth = 160;

        String fanName = attendee.getFanName();
        String realName = attendee.getFirstName() + " " + attendee.getLastName();
        if (fanName != null && fanName.matches("^\\s*$")) {
            fanName = null;
        }
        String name = fanName;

        // Draw main name (Fan Name if set, otherwise real name)
        if (name == null) {
            name = realName;
        }
        drawStringWithResizing(contentStream, 360, 165, name, resizeOpt);

        // Draw real name if Fan Name set
        if (fanName != null) {
            resizeOpt.size = 18;
            resizeOpt.minFontSize = 6;
            resizeOpt.lines = 1;
            resizeOpt.maxTextWidth = 140;
            drawStringWithResizing(contentStream, 310, 143, realName, resizeOpt);
        }
    }

    private void drawStaffNames(PDPageContentStream contentStream, Attendee attendee) throws IOException {
        ResizeOptions resizeOpt = new ResizeOptions();
        resizeOpt.size = 24;
        resizeOpt.lines = 1;
        resizeOpt.maxTextWidth = 160;

        String fanName = attendee.getFanName();
        String realName = attendee.getFirstName() + " " + attendee.getLastName();
        if (fanName != null && fanName.matches("^\\s*$")) {
            fanName = null;
        }
        String name = fanName;

        // Draw main name (Fan Name if set, otherwise real name)
        if (name == null) {
            name = realName;
        }
        drawStringWithResizing(contentStream, 360, 165, "Staff " + name, resizeOpt);

        // Draw real name if Fan Name set
        if (fanName != null) {
            resizeOpt.size = 18;
            resizeOpt.minFontSize = 6;
            resizeOpt.lines = 1;
            resizeOpt.maxTextWidth = 140;
            drawStringWithResizing(contentStream, 310, 143, "Staff " + realName, resizeOpt);
        }
    }


    /**
     * Returns the output of PDF generation
     * @return PDF contents as byte array
     */
    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(os.toByteArray());
    }

}
