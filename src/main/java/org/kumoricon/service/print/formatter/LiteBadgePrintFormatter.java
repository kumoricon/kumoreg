package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.service.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.kumoricon.service.print.formatter.badgeimage.BadgeCreator;
import org.kumoricon.service.print.formatter.badgeimage.BadgeCreatorLite2018;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class LiteBadgePrintFormatter implements BadgePrintFormatter {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    private final BadgeCreator badgeCreator = new BadgeCreatorLite2018();
    private static final Logger LOGGER = LoggerFactory.getLogger(LiteBadgePrintFormatter.class);
    private Integer xOffset = 0;
    private Integer yOffset = 0;

    /**
     * Generates a PDF containing badges ready to be printed. Lite badges have no Fan Name or number.
     * @param attendees Attendees to print badges for
     */
    public LiteBadgePrintFormatter(List<Attendee> attendees) {
        this(attendees, 0, 0);
    }

    /**
     * Generates a PDF containing badges ready to be printed. Lite badges have no Fan Name or number.
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

        } catch (IOException e) {
            LOGGER.error("Error creating badge", e);
            throw new RuntimeException(e);
        }

    }


    private PDPage generatePage(Attendee attendee, PDDocument document) throws IOException {
        PDPage page = new PDPage(new PDRectangle(612f, 396f));

        final AttendeeBadgeDTO badgeDTO = AttendeeBadgeDTO.fromAttendee(attendee);

        byte[] badgeImage = badgeCreator.createBadge(badgeDTO);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, false);
        PDImageXObject pdi = PDImageXObject.createFromByteArray(document, badgeImage, attendee.getId() + ".png");
        contentStream.drawImage(pdi,126+xOffset,54+yOffset, 360, 288);
        contentStream.close();

        return page;
    }


    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(os.toByteArray());
    }

}
