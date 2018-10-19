package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.kumoricon.model.attendee.Attendee;

import javax.imageio.IIOException;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class StaffBadge2017 extends FormatterBase  {

    private PDDocument frontBackground;
    private PDDocument backBackground;
    private PDFont bankGothic;
    private static final boolean DRAW_BOUNDING_BOX = false;
    private LocalDate currentDateForAgeCalculation;

    StaffBadge2017(PDDocument document) {
        super(document);

        bankGothic = BadgeLib.loadFont(document);
        frontBackground = BadgeLib.loadBackground("2017_staff_badge_front.pdf");
        backBackground = BadgeLib.loadBackground("2017_staff_badge_back.pdf");
        this.currentDateForAgeCalculation = LocalDate.now(ZoneId.of("America/Los_Angeles"));
    }

    public StaffBadge2017(PDDocument document, LocalDate currentDateForAgeCalculation) {
        super(document);
        this.currentDateForAgeCalculation = currentDateForAgeCalculation;
        bankGothic = BadgeLib.loadFont(document);
        frontBackground = BadgeLib.loadBackground("2017_staff_badge_front.pdf");
        backBackground = BadgeLib.loadBackground("2017_staff_badge_back.pdf");
    }


    void addBadge(Attendee attendee, Integer xOffset, Integer yOffset) throws IOException {
        PDPage templatePage = frontBackground.getDocumentCatalog().getPages().get(0);
        COSDictionary pageDict = templatePage.getCOSObject();
        COSDictionary newPageDict = new COSDictionary(pageDict);
        newPageDict.removeItem(COSName.ANNOTS);
        newPageDict.removeItem(COSName.ACTUAL_TEXT);

        PDPage page = document.importPage(new PDPage(newPageDict));

        // Positions are measured from the bottom left corner of the page at 72 DPI
        drawDepartmentBackgroundColorFront(page, attendee);
        //drawDepartmentNameFront(page, attendee);
        drawPositionsFront(page, attendee);
        drawImage(page, attendee);
        drawName(page, attendee);
        drawAgeImageFront(page, attendee);

        // Badge back
        PDPage templateBack = backBackground.getDocumentCatalog().getPages().get(0);
        COSDictionary backPageDict = templateBack.getCOSObject();
        COSDictionary newBackPageDict = new COSDictionary(backPageDict);
        newBackPageDict.removeItem(COSName.ANNOTS);
        newBackPageDict.removeItem(COSName.ACTUAL_TEXT);

        PDPage pageBack = document.importPage(new PDPage(newBackPageDict));
        drawDepartmentBackgroundColorBack(pageBack, attendee);
        //drawDepartmentNameBack(pageBack, attendee);
        drawPositionsBack(pageBack, attendee);
        drawNameBack(pageBack, attendee);
        drawAgeImageBack(pageBack, attendee);
    }

    private void drawAgeImageFront(PDPage page, Attendee attendee) throws IOException {

        String imageFilename = BadgeLib.getStaffAgeImageFilename(attendee, currentDateForAgeCalculation);
        if (imageFilename == null) { return; }

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        PDImageXObject xImage = PDImageXObject.createFromFile(imageFilename, document);
        Dimension scaledDim = getScaledDimension(
                new Dimension(xImage.getWidth(),  xImage.getHeight()),
                new Dimension(149, 130));
        stream.drawImage(xImage,
                45 + ((149-scaledDim.width)/2),
                335 + ((158-scaledDim.height)/2),
                scaledDim.width, scaledDim.height);
        stream.close();
    }

    private void drawAgeImageBack(PDPage page, Attendee attendee) throws IOException {
        String imageFilename = BadgeLib.getStaffAgeImageFilename(attendee, currentDateForAgeCalculation);
        if (imageFilename == null) { return; }

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, DRAW_BOUNDING_BOX);

        PDImageXObject xImage = PDImageXObject.createFromFile(imageFilename, document);
        Dimension scaledDim = getScaledDimension(
                new Dimension(xImage.getWidth(),  xImage.getHeight()),
                new Dimension(149, 115));
        stream.drawImage(xImage,
                201 + ((149-scaledDim.width)/2),
                344 + ((158-scaledDim.height)/2),
                scaledDim.width, scaledDim.height);
        stream.close();
    }

    private void drawDepartmentBackgroundColorFront(PDPage page, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        stream.setNonStrokingColor(Color.decode(attendee.getStaffDepartmentColor()));
        stream.setStrokingColor(Color.decode(attendee.getStaffDepartmentColor()));
        stream.addRect(306, 144, 45, 387);  // Left hand vertical
        stream.fill();
        stream.close();
    }

    private void drawDepartmentBackgroundColorBack(PDPage page, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        stream.setStrokingColor(Color.decode(attendee.getStaffDepartmentColor()));
        stream.setNonStrokingColor(Color.decode(attendee.getStaffDepartmentColor()));
        stream.addRect(45, 144, 45, 387);  // Left hand vertical
        stream.fill();
        stream.close();
    }


    private void drawPositionsFront(PDPage page, Attendee attendee) throws IOException {
        Color color = BadgeLib.getForegroundColor(attendee.getStaffDepartmentColor());

        // Bounding box:
        PDRectangle boundingBox;
        //if ("".equals(attendee.getStaffDepartment())) {
        if (true) {
            boundingBox = new PDRectangle(306, 173, 30, 325);
            List<String> lines = BadgeLib.wrapPositions(attendee.getStaffPositions(), 36);
            BadgeLib.drawText(document, page, boundingBox, bankGothic, color, lines, BadgeLib.ALIGNMENT.CENTER, BadgeLib.ROTATION.LEFT, 14, 14, true, DRAW_BOUNDING_BOX);
        } else {
            boundingBox = new PDRectangle(306, 148, 30, 200);
            List<String> lines = BadgeLib.wrapPositions(attendee.getStaffPositions(), 16);
            BadgeLib.drawText(document, page, boundingBox, bankGothic, color, lines, BadgeLib.ALIGNMENT.CENTER, BadgeLib.ROTATION.LEFT, 14, 14, true, DRAW_BOUNDING_BOX);
        }
    }

    private void drawPositionsBack(PDPage page, Attendee attendee) throws IOException {

        Color color = BadgeLib.getForegroundColor(attendee.getStaffDepartmentColor());

        // Bounding box:
        PDRectangle boundingBox;
        //if ("".equals(attendee.getStaffDepartment())) {
        if (true) {
            boundingBox = new PDRectangle(60, 173, 30, 325);
            List<String> lines = BadgeLib.wrapPositions(attendee.getStaffPositions(), 36);
            BadgeLib.drawText(document, page, boundingBox, bankGothic, color, lines, BadgeLib.ALIGNMENT.CENTER, BadgeLib.ROTATION.RIGHT, 14, 14, true, DRAW_BOUNDING_BOX);
        } else {
            boundingBox = new PDRectangle(60, 148, 30, 200);
            List<String> lines = BadgeLib.wrapPositions(attendee.getStaffPositions(), 16);
            BadgeLib.drawText(document, page, boundingBox, bankGothic, color, lines, BadgeLib.ALIGNMENT.CENTER, BadgeLib.ROTATION.RIGHT, 14, 14, true, DRAW_BOUNDING_BOX);
        }

    }


    private void drawDepartmentNameBack(PDPage page, Attendee attendee) throws IOException {
        if ("".equals(attendee.getStaffDepartment())) { return; }
        // Bounding box:
        PDRectangle boundingBox = new PDRectangle(60, 361, 30, 156);
        Color color = BadgeLib.getForegroundColor(attendee.getStaffDepartmentColor());
        String[] lines = {attendee.getStaffDepartment()};
        BadgeLib.drawText(document, page, boundingBox, bankGothic, color, lines, BadgeLib.ALIGNMENT.CENTER, BadgeLib.ROTATION.RIGHT, 14, 14, false, DRAW_BOUNDING_BOX);
    }

    private void drawDepartmentNameFront(PDPage page, Attendee attendee) throws IOException {
        if ("".equals(attendee.getStaffDepartment())) { return; }

//        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        // Bounding box:
        PDRectangle boundingBox = new PDRectangle(306, 361, 30, 156);
        Color color = BadgeLib.getForegroundColor(attendee.getStaffDepartmentColor());
        String[] lines = {attendee.getStaffDepartment()};
        BadgeLib.drawText(document, page, boundingBox, bankGothic, color, lines, BadgeLib.ALIGNMENT.CENTER, BadgeLib.ROTATION.LEFT, 14, 14, false, DRAW_BOUNDING_BOX);
    }

    private void drawName(PDPage page, Attendee attendee) throws IOException {
        // Name bounding box:
        //         stream.fillRect(105, 152, 190, 63);

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        ResizeOptions opts = new ResizeOptions();
        opts.centered = true;
        opts.font = bankGothic;
        opts.lines = 2;
        opts.maxTextWidth = 190;
        opts.minFontSize = 8;
        opts.size = 36;

        drawStringWithResizing(stream, 200, 190, attendee.getFirstName(), opts);
        drawStringWithResizing(stream, 200, 160, attendee.getLastName(), opts);
        stream.close();
    }

    private void drawNameBack(PDPage page, Attendee attendee) throws IOException {
        // Name bounding box:
        //         stream.fillRect(105, 152, 190, 63);

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        ResizeOptions opts = new ResizeOptions();
        opts.centered = true;
        opts.font = bankGothic;
        opts.lines = 2;
        opts.maxTextWidth = 190;
        opts.minFontSize = 8;
        opts.size = 36;

        drawStringWithResizing(stream, 195, 190, attendee.getFirstName(), opts);
        drawStringWithResizing(stream, 195, 160, attendee.getLastName(), opts);
        stream.close();
    }


    private void drawImage(PDPage page, Attendee attendee) throws IOException {
        String imageFilename = BadgeLib.getStaffImageFilename(attendee);
        if (imageFilename == null) {
            imageFilename = BadgeLib.getMascotImageFilename();
        }

        PDImageXObject xImage;
        try (PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false)) {
            xImage = PDImageXObject.createFromFile(imageFilename, document);
            Dimension scaledDim = getScaledDimension(
                    new Dimension(xImage.getWidth(),  xImage.getHeight()),
                    new Dimension(149, 158));
            stream.drawImage(xImage,
                    150 + ((149-scaledDim.width)/2),
                    334 + ((158-scaledDim.height)/2),
                    scaledDim.width, scaledDim.height);
//            stream.fillRect(150, 334, 149, 158);
            stream.close();
        } catch (IIOException ex) {
            System.out.println("Error opening " + imageFilename);
        }
    }

    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // check if width needs to be scaled
        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        // then check height still needs to be scaled
        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    void closeTemplates() {
        if (frontBackground != null) {
            try {
                frontBackground.close();
            } catch (Exception ignored) {}
        }
        if (backBackground != null) {
            try {
                backBackground.close();
            } catch (Exception ignored) {}
        }
    }

    public static String getFormatterName() {
        return "2017Staff";
    }

}
