package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.kumoricon.model.attendee.Attendee;

import java.util.List;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class AttendeeBadge2017 extends FormatterBase  {

    private PDFont bankGothic;
    private PDDocument frontBackground;
    private int xOffset;
    private int yOffset;
    private LocalDate currentDateForAgeCalculation;


    public AttendeeBadge2017(PDDocument document, LocalDate currentDateForAgeCalculation) {
        super(document);
        this.xOffset = 0;
        this.yOffset = 0;
        frontBackground = BadgeLib.loadBackground("2017_attendee_badge.pdf");
        this.currentDateForAgeCalculation = currentDateForAgeCalculation;
        bankGothic = BadgeLib.loadFont(document);
    }


    void addBadge(Attendee attendee, Integer xOffset, Integer yOffset) throws IOException {
//        PDPage templatePage = frontBackground.getDocumentCatalog().getPages().get(0);
//        COSDictionary pageDict = templatePage.getCOSObject();
//        COSDictionary newPageDict = new COSDictionary(pageDict);
//        newPageDict.removeItem(COSName.ANNOTS);
//        newPageDict.removeItem(COSName.ACTUAL_TEXT);
//
//        PDPage page = document.importPage(new PDPage(newPageDict));
        this.xOffset = xOffset; // Todo: don't store this in the class
        this.yOffset = yOffset;
        PDPage page = document.importPage(new PDPage(new PDRectangle(612f, 396f)));

        // Positions are measured from the bottom left corner of the page at 72 DPI
        drawAgeColorStripe(page, attendee);
        drawVerticalAgeRangeText(page, bankGothic, attendee);
        drawName(page, attendee);
        drawBadgeTypeStripe(page, attendee);
        drawBadgeTypeText(page, bankGothic, attendee);
        drawBadgeNumber(page, bankGothic, attendee);
    }



    private void drawBadgeNumber(PDPage page, PDFont font, Attendee attendee) throws IOException {
        String badgeNumber = attendee.getBadgeNumber();
        if (badgeNumber == null) {
            return;     // no text, don't draw anything
        }

        List<String> badgeNumberParts = BadgeLib.splitBadgeNumber(badgeNumber);

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        String stripeColor = attendee.getCurrentAgeRange().getStripeColor();
        stream.setNonStrokingColor(BadgeLib.getForegroundColor(stripeColor));
        // Bounding box:
//         stream.fillRect(163, 95, 40, 30);

        PDRectangle boundingBox = new PDRectangle(163, 95, 40, 30);
        stream.setLineWidth(0.25f);
        stream.beginText();
        int fontSize = BadgeLib.findMaxFontSize(font, badgeNumberParts,boundingBox);
        stream.setFont(font, fontSize);

        float textWidth = font.getStringWidth(badgeNumberParts.get(0));
        Float offset = textWidth * (fontSize/(2*1000.0f));
        stream.newLineAtOffset(185-offset, 105+fontSize);   // First character position
        stream.showText(badgeNumberParts.get(0));

        if (badgeNumberParts.size() > 1) {
            textWidth = font.getStringWidth(badgeNumberParts.get(1));
            Float newOffset = textWidth * (fontSize/(2*1000.0f));
            stream.newLineAtOffset(offset-newOffset, -1*fontSize);   // First character position
            stream.showText(badgeNumberParts.get(1));
        }
        stream.close();
    }

    private void drawBadgeTypeStripe(PDPage page, Attendee attendee) throws IOException {

        if (attendee.getBadge() != null &&
                (attendee.getBadge().getBadgeTypeText().equals("Friday") ||
                 attendee.getBadge().getBadgeTypeText().equals("Saturday") ||
                 attendee.getBadge().getBadgeTypeText().equals("Sunday"))) {
            PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
            // This only needs to be set once in the page to set the global offset.
            stream.concatenate2CTM(1, 0, 0, 1, 0,0);

            if (attendee.getCurrentAgeRange() != null) {
                stream.setNonStrokingColor(Color.decode(attendee.getCurrentAgeRange().getStripeColor()));
            } else {
                stream.setNonStrokingColor(Color.black);
            }
            stream.addRect(206, 85, 253, 44);
            stream.fill();
            stream.close();

        } else {
            PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

            if (attendee.getBadge() != null && attendee.getBadge().getBadgeTypeBackgroundColor() != null) {
                stream.setNonStrokingColor(Color.decode(attendee.getBadge().getBadgeTypeBackgroundColor()));
                stream.setStrokingColor(Color.decode(attendee.getBadge().getBadgeTypeBackgroundColor()));
            } else {
                stream.setNonStrokingColor(Color.black);
                stream.setStrokingColor(Color.black);
            }
            stream.addRect(206, 85, 253, 44);
            stream.fill();
            stream.close();
        }
    }

    private void drawBadgeTypeText(PDPage page, PDFont font, Attendee attendee) throws IOException {
        if (attendee.getBadge() == null || attendee.getBadge().getBadgeTypeText() == null) {
            return;     // no text, don't draw anything
        }
        String badgeTypeText = attendee.getBadge().getBadgeTypeText();
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

//        PDRectangle boundingBox = new PDRectangle(206, 85, 253, 44);

        stream.setLineWidth(0.5f);
        stream.beginText();
        int fontSize = BadgeLib.findMaxLineSize(font, badgeTypeText,240, 40);
        stream.setFont(font, fontSize);
        if (attendee.getBadge() != null &&
                (attendee.getBadge().getBadgeTypeText().equals("Friday") ||
                        attendee.getBadge().getBadgeTypeText().equals("Saturday") ||
                        attendee.getBadge().getBadgeTypeText().equals("Sunday"))) {
            if (attendee.getBadge() != null && attendee.getBadge().getBadgeTypeBackgroundColor() != null) {
                stream.setNonStrokingColor(BadgeLib.getForegroundColor(attendee.getCurrentAgeRange().getStripeColor()));
            } else {
                stream.setNonStrokingColor(Color.WHITE);
                stream.setStrokingColor(Color.black);
            }

        } else {
            if (attendee.getBadge() != null && attendee.getBadge().getBadgeTypeBackgroundColor() != null) {
                stream.setNonStrokingColor(BadgeLib.getForegroundColor(attendee.getBadge().getBadgeTypeBackgroundColor()));
            } else {
                stream.setNonStrokingColor(Color.WHITE);
                stream.setStrokingColor(Color.black);
            }

        }

        float textWidth = font.getStringWidth(badgeTypeText);
        Float offset = textWidth * (fontSize/(2*1000.0f));
        stream.newLineAtOffset(330-offset, 100);   // First character position
        stream.showText(badgeTypeText);
        stream.close();
    }

    private void drawVerticalAgeRangeText(PDPage page, PDFont font, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        stream.setLineWidth(0.5f);
        stream.beginText();
        stream.setFont(font, 30);

        String stripeColor = attendee.getCurrentAgeRange().getStripeColor();
        stream.setNonStrokingColor(BadgeLib.getForegroundColor(stripeColor));

        stream.newLineAtOffset(172, 275);   // First character position

        String ageString = BadgeLib.getAgeRangeAtCon(attendee, currentDateForAgeCalculation);
        if (ageString.toLowerCase().equals("adult")) {
            stream.showText("A");
            stream.newLineAtOffset(-1, -32);
            stream.showText("D");
            stream.newLineAtOffset(0, -32);
            stream.showText("U");
            stream.newLineAtOffset(2, -32);
            stream.showText("L");
            stream.newLineAtOffset(2, -32);
            stream.showText("T");
        } else if (ageString.toLowerCase().equals("youth")) {
            stream.showText("Y");
            stream.newLineAtOffset(-2, -32);
            stream.showText("O");
            stream.newLineAtOffset(0, -32);
            stream.showText("U");
            stream.newLineAtOffset(4, -32);
            stream.showText("T");
            stream.newLineAtOffset(-3, -32);
            stream.showText("H");
        } else if (ageString.toLowerCase().equals("child")) {
            stream.showText("C");
            stream.newLineAtOffset(0, -32);
            stream.showText("H");
            stream.newLineAtOffset(5, -32);
            stream.showText("I");
            stream.newLineAtOffset(-3, -32);
            stream.showText("L");
            stream.newLineAtOffset(-2, -32);
            stream.showText("D");
        } else {
            stream.showText("V");
            stream.newLineAtOffset(0, -32);
            stream.showText("O");
            stream.newLineAtOffset(5, -32);
            stream.showText("I");
            stream.newLineAtOffset(-5, -32);
            stream.showText("D");
        }
        stream.close();
    }

    private void drawAgeColorStripe(PDPage page, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        // This only needs to be set once in the page to set the global offset.
        stream.concatenate2CTM(1, 0, 0, 1, xOffset, yOffset);

        if (attendee.getCurrentAgeRange() != null) {
            stream.setNonStrokingColor(Color.decode(attendee.getCurrentAgeRange().getStripeColor()));
        } else {
            stream.setNonStrokingColor(Color.black);
        }
        stream.addRect(160, 85, 46, 230);
        stream.fill();
        stream.close();
    }


    private void drawName(PDPage page, Attendee attendee) throws IOException {

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        // Name bounding box:
        // stream.fillRect(285, 165, 170, 50);

        stream.beginText();
        stream.setNonStrokingColor(Color.BLACK);
        if (attendee.getFanName() == null) {
            // Draw preferred name, no fan name
            int fontSize = BadgeLib.findMaxLineSize(bankGothic, attendee.getName(), 160, 50);
            stream.setFont(bankGothic, fontSize);
            Matrix offsetLine1 = Matrix.getTranslateInstance(285, 165);
            stream.setTextMatrix(offsetLine1);
            stream.showText(attendee.getName());
        } else {
            // Draw fan name and preferred name
            stream.setFont(bankGothic, BadgeLib.findMaxLineSize(bankGothic, attendee.getFanName(), 160, 25));
            Matrix offsetLine1 = Matrix.getTranslateInstance(285, 190);
            stream.setTextMatrix(offsetLine1);
            stream.showText(attendee.getFanName());

            stream.setFont(bankGothic, BadgeLib.findMaxLineSize(bankGothic, attendee.getName(), 160, 25));
            Matrix offsetLine2 = Matrix.getTranslateInstance(285, 165);
            stream.setTextMatrix(offsetLine2);
            stream.showText(attendee.getName());

        }

//        drawStringWithResizing(stream, 200, 190, attendee.getFirstName() + " " + attendee.getLastName(), opts);
//        drawStringWithResizing(stream, 200, 190, attendee.getFirstName(), opts);
//        drawStringWithResizing(stream, 200, 160, attendee.getLastName(), opts);
        stream.close();
    }

    public static String getFormatterName() {
        return "2017Attendee";
    }
}
