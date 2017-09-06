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

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class AttendeeBadge2017 extends FormatterBase  {

    private PDFont bankGothic;
    private PDDocument frontBackground;

    private LocalDate currentDateForAgeCalculation;

    public AttendeeBadge2017() {
        super(null);
    }

    public AttendeeBadge2017(PDDocument document) {
        super(document);

        frontBackground = BadgeLib.loadBackground("2017_attendee_badge.pdf");
        bankGothic = BadgeLib.loadFont(document);
        this.currentDateForAgeCalculation = LocalDate.now();
    }

    public AttendeeBadge2017(PDDocument document, LocalDate currentDateForAgeCalculation) {
        super(document);
        frontBackground = BadgeLib.loadBackground("2017_attendee_badge.pdf");
        this.currentDateForAgeCalculation = currentDateForAgeCalculation;
        bankGothic = BadgeLib.loadFont(document);
    }


    public void addBadge(Attendee attendee, Integer xOffset, Integer yOffset) throws IOException {
//        PDPage page = document.importPage(new PDPage(new PDRectangle(612f, 396f)));

        PDPage templatePage = frontBackground.getDocumentCatalog().getPages().get(0);
        COSDictionary pageDict = templatePage.getCOSObject();
        COSDictionary newPageDict = new COSDictionary(pageDict);
        newPageDict.removeItem(COSName.ANNOTS);
        newPageDict.removeItem(COSName.ACTUAL_TEXT);

        PDPage page = document.importPage(new PDPage(newPageDict));

        // Positions are measured from the bottom left corner of the page at 72 DPI
        drawAgeColorStripe(page, attendee);
        drawVerticalAgeRangeText(page, bankGothic, attendee);
        drawName(page, attendee);
        drawBadgeTypeStripe(page, bankGothic, attendee);
        drawBadgeTypeText(page, bankGothic, attendee);
    }

    private String getAgeRangeAtCon(Attendee attendee) {
        String ageRangeName;
        long ageAtCon = attendee.getAge(currentDateForAgeCalculation);
        if (ageAtCon >= 18) {
            ageRangeName = "adult";
        } else if (ageAtCon >= 13) {
            ageRangeName = "youth";
        } else {
            ageRangeName = "child";
        }

        return ageRangeName;
    }

    private void drawBadgeTypeStripe(PDPage page, PDFont font, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        if (attendee.getBadge() != null && attendee.getBadge().getBadgeTypeBackgroundColor() != null) {
            stream.setNonStrokingColor(Color.decode(attendee.getBadge().getBadgeTypeBackgroundColor()));
        } else {
            stream.setNonStrokingColor(Color.black);
        }
        stream.fillRect(206, 85, 253, 44);

        stream.close();
    }

    private void drawBadgeTypeText(PDPage page, PDFont font, Attendee attendee) throws IOException {
        String badgeTypeText;
        if (attendee.getBadge() != null && attendee.getBadge().getBadgeTypeText() != null) {
            badgeTypeText = attendee.getBadge().getBadgeTypeText();
        } else {
            return;     // no text, don't draw anything
        }
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        PDRectangle boundingBox = new PDRectangle(206, 85, 253, 44);

        stream.setLineWidth(0.5f);
        stream.beginText();
        int fontSize = BadgeLib.findMaxLineSize(font, badgeTypeText,240, 40);
        stream.setFont(font, fontSize);
        stream.setNonStrokingColor(Color.white);
        stream.setStrokingColor(Color.black);
        stream.appendRawCommands("2 Tr ");       // Set text rendering mode

        float textWidth = font.getStringWidth(badgeTypeText);
        Float offset = textWidth * (fontSize/(2*1000.0f));
        stream.moveTextPositionByAmount(330-offset, 100);   // First character position
        stream.drawString(badgeTypeText);
        stream.close();
    }

    private void drawVerticalAgeRangeText(PDPage page, PDFont font, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        stream.setLineWidth(0.5f);
        stream.beginText();
        stream.setFont(font, 30);
        stream.setNonStrokingColor(Color.white);
        stream.setStrokingColor(Color.black);
        stream.appendRawCommands("2 Tr ");       // Set text rendering mode

        stream.moveTextPositionByAmount(172, 275);   // First character position

        String ageString = getAgeRangeAtCon(attendee);
        if (ageString.toLowerCase().equals("adult")) {
            stream.drawString("A");
            stream.moveTextPositionByAmount(-1, -32);
            stream.drawString("D");
            stream.moveTextPositionByAmount(0, -32);
            stream.drawString("U");
            stream.moveTextPositionByAmount(2, -32);
            stream.drawString("L");
            stream.moveTextPositionByAmount(2, -32);
            stream.drawString("T");
        } else if (ageString.toLowerCase().equals("youth")) {
            stream.drawString("Y");
            stream.moveTextPositionByAmount(-2, -32);
            stream.drawString("O");
            stream.moveTextPositionByAmount(0, -32);
            stream.drawString("U");
            stream.moveTextPositionByAmount(4, -32);
            stream.drawString("T");
            stream.moveTextPositionByAmount(-3, -32);
            stream.drawString("H");
        } else if (ageString.toLowerCase().equals("child")) {
            stream.drawString("C");
            stream.moveTextPositionByAmount(0, -32);
            stream.drawString("H");
            stream.moveTextPositionByAmount(5, -32);
            stream.drawString("I");
            stream.moveTextPositionByAmount(-3, -32);
            stream.drawString("L");
            stream.moveTextPositionByAmount(-2, -32);
            stream.drawString("D");
        } else {
            stream.drawString("V");
            stream.moveTextPositionByAmount(0, -32);
            stream.drawString("O");
            stream.moveTextPositionByAmount(5, -32);
            stream.drawString("I");
            stream.moveTextPositionByAmount(-5, -32);
            stream.drawString("D");
        }
        stream.close();
    }

    private void drawAgeColorStripe(PDPage page, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        if (attendee.getCurrentAgeRange() != null) {
            stream.setNonStrokingColor(Color.decode(attendee.getCurrentAgeRange().getStripeColor()));
        } else {
            stream.setNonStrokingColor(Color.black);
        }
        stream.fillRect(160, 85, 46, 230);

        stream.close();
    }


    private void drawName(PDPage page, Attendee attendee) throws IOException {

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        // Name bounding box:
        // stream.fillRect(285, 165, 170, 50);

        stream.beginText();
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
