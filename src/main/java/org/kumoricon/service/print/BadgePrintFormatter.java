package org.kumoricon.service.print;

import com.vaadin.server.StreamResource;
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

public class BadgePrintFormatter implements StreamResource.StreamSource {
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();

        public BadgePrintFormatter(List<Attendee> attendees) {
            PDDocument document;

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

        // Draw main name (badge name if set, otherwise real name)
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(240, 200);
        contentStream.setFont( font, 24 );
        if (attendee.getBadgeName() != null) {
            contentStream.drawString(attendee.getBadgeName());
        } else {
            contentStream.drawString(attendee.getFirstName() + " " + attendee.getLastName());
        }
        contentStream.endText();

        // Draw real name if badge name set
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(280, 180);
        contentStream.setFont(font, 18);
        if (attendee.getBadgeName() != null) {
            contentStream.drawString(attendee.getFirstName() + " " + attendee.getLastName());
        }
        contentStream.endText();

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
