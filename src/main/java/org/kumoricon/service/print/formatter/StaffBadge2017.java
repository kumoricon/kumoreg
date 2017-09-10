package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Matrix;
import org.kumoricon.model.attendee.Attendee;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;

public class StaffBadge2017 extends FormatterBase  {

    private PDDocument frontBackground;
    private PDDocument backBackground;
    private PDFont bankGothic;

    private LocalDate currentDateForAgeCalculation;

    StaffBadge2017(PDDocument document) {
        super(document);

        bankGothic = BadgeLib.loadFont(document);
        frontBackground = BadgeLib.loadBackground("2017_staff_badge_front.pdf");
        backBackground = BadgeLib.loadBackground("2017_staff_badge_back.pdf");
        this.currentDateForAgeCalculation = LocalDate.now();
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
        drawDepartmentNameFront(page, attendee);
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
        drawDepartmentNameBack(pageBack, attendee);
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

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

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

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        // Bounding box:
//        stream.setNonStrokingColor(Color.red);
//        stream.addRect(306, 144, 45, 170);
//        stream.fill();
        PDRectangle boundingBox = new PDRectangle(306, 144, 45, 170);

        stream.setLineWidth(0.25f);
        stream.beginText();
        stream.setStrokingColor(Color.white);
        stream.setNonStrokingColor(Color.black);
        stream.setRenderingMode(RenderingMode.FILL_STROKE);

        if (attendee.getStaffPositions().size() == 1) {
            int fontSize = BadgeLib.findMaxFontSize(bankGothic, attendee.getStaffPositions(), boundingBox);
            stream.setFont(bankGothic, fontSize);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 332, 145);
            stream.setTextMatrix(offset);
            stream.showText(attendee.getStaffPositions().get(0));

        } else if (attendee.getStaffPositions().size() == 2) {
            int fontSize = BadgeLib.findMaxFontSize(bankGothic, attendee.getStaffPositions(), boundingBox);
            String text = attendee.getStaffPositions().get(0);

            stream.setFont(bankGothic, fontSize);
            Matrix offsetLine1 = Matrix.getRotateInstance(90 * Math.PI * 0.25, 320, 145);
            stream.setTextMatrix(offsetLine1);
            stream.showText(text);

            text = attendee.getStaffPositions().get(1);
            stream.setFont(bankGothic, fontSize);
            Matrix offsetLine2 = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 145);
            stream.setTextMatrix(offsetLine2);
            stream.showText(text);
        } else if (attendee.getStaffPositions().size() == 3) {
            int fontSize = BadgeLib.findMaxFontSize(bankGothic, attendee.getStaffPositions(), boundingBox);
            String text = attendee.getStaffPositions().get(0);
            Matrix offsetLine1 = Matrix.getRotateInstance(90 * Math.PI * 0.25, 314, 145);
            stream.setFont(bankGothic, fontSize);
            stream.setTextMatrix(offsetLine1);
            stream.showText(text);

            text = attendee.getStaffPositions().get(1);
            stream.setFont(bankGothic, fontSize);
            Matrix offsetLine2 = Matrix.getRotateInstance(90 * Math.PI * 0.25, 324, 145);
            stream.setTextMatrix(offsetLine2);
            stream.showText(text);

            text = attendee.getStaffPositions().get(2);
            stream.setFont(bankGothic, fontSize);
            Matrix offsetLine3 = Matrix.getRotateInstance(90 * Math.PI * 0.25, 334, 145);
            stream.setTextMatrix(offsetLine3);
            stream.showText(text);
        }

        stream.close();
    }

    private void drawPositionsBack(PDPage page, Attendee attendee) throws IOException {

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        // Bounding box:
//        stream.setNonStrokingColor(Color.red);
//        stream.addRect(45, 144, 45, 170);
//        stream.fill();
        PDRectangle boundingBox = new PDRectangle(45, 144, 45, 170);

        stream.setLineWidth(0.25f);
        stream.beginText();
        stream.setStrokingColor(Color.white);
        stream.setNonStrokingColor(Color.black);
        stream.setRenderingMode(RenderingMode.FILL_STROKE);

        if (attendee.getStaffPositions().size() == 1) {
            int fontSize = BadgeLib.findMaxFontSize(bankGothic, attendee.getStaffPositions(), boundingBox);
            float width = (bankGothic.getStringWidth(attendee.getStaffPositions().get(0)) / 1000) * fontSize;

            stream.setFont(bankGothic, fontSize);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 68, 148+width);
            stream.setTextMatrix(offset);
            stream.showText(attendee.getStaffPositions().get(0));

        } else if (attendee.getStaffPositions().size() == 2) {
            int fontSize = BadgeLib.findMaxFontSize(bankGothic, attendee.getStaffPositions(), boundingBox);
            String text = attendee.getStaffPositions().get(0);
            float width = (bankGothic.getStringWidth(text) / 1000) * fontSize;
            Matrix offsetLine1 = Matrix.getRotateInstance(270 * Math.PI * 0.25, 76, 148+width);
            stream.setFont(bankGothic, fontSize);
            stream.setTextMatrix(offsetLine1);
            stream.showText(text);

            text = attendee.getStaffPositions().get(1);
            width = (bankGothic.getStringWidth(text) / 1000) * fontSize;
            stream.setFont(bankGothic, fontSize);
            Matrix offsetLine2 = Matrix.getRotateInstance(270 * Math.PI * 0.25, 62, 148+width);
            stream.setTextMatrix(offsetLine2);
            stream.showText(text);
        } else if (attendee.getStaffPositions().size() == 3) {
            int fontSize = BadgeLib.findMaxFontSize(bankGothic, attendee.getStaffPositions(), boundingBox);
            String text = attendee.getStaffPositions().get(0);
            stream.setFont(bankGothic, fontSize);
            float width = (bankGothic.getStringWidth(text) / 1000) * fontSize;
            Matrix offsetLine1 = Matrix.getRotateInstance(270 * Math.PI * 0.25, 81, 148+width);
            stream.setTextMatrix(offsetLine1);
            stream.showText(text);

            text = attendee.getStaffPositions().get(1);
            stream.setFont(bankGothic, fontSize);
            width = (bankGothic.getStringWidth(text) / 1000) * fontSize;
            Matrix offsetLine2 = Matrix.getRotateInstance(270 * Math.PI * 0.25, 72, 148+width);
            stream.setTextMatrix(offsetLine2);
            stream.showText(text);

            text = attendee.getStaffPositions().get(2);
            stream.setFont(bankGothic, fontSize);
            width = (bankGothic.getStringWidth(text) / 1000) * fontSize;
            Matrix offsetLine3 = Matrix.getRotateInstance(270 * Math.PI * 0.25, 63, 148+width);
            stream.setTextMatrix(offsetLine3);
            stream.showText(text);
        }
        stream.close();
    }


    private void drawDepartmentNameBack(PDPage page, Attendee attendee) throws IOException {
        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        // Bounding box:
//        stream.setNonStrokingColor(Color.red);
//        stream.fillRect(45, 361, 45, 170);
        stream.setLineWidth(0.25f);
        stream.beginText();
        stream.setStrokingColor(Color.white);
        stream.setNonStrokingColor(Color.black);
        stream.setRenderingMode(RenderingMode.FILL_STROKE);

        if ("chair".equals(attendee.getStaffDepartment().toLowerCase()) ||
                "department of the chair".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 46);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 56, 530);
            stream.setTextMatrix(offset);
            stream.showText("CHAIR");

        } else if ("treasury".equals(attendee.getStaffDepartment().toLowerCase()) ||
                "department of the treasurer".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 26);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 62, 525);
            stream.setTextMatrix(offset);
            stream.showText("TREASURY");
        } else if ("security".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 28);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 61, 526);
            stream.setTextMatrix(offset);
            stream.showText("SECURITY");
        } else if ("secretary".equals(attendee.getStaffDepartment().toLowerCase()) ||
                "department of the secretary".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 26);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 61, 526);
            stream.setTextMatrix(offset);
            stream.showText("SECRETARY");
        } else if ("relations".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 25);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 61, 527);
            stream.setTextMatrix(offset);
            stream.showText("RELATIONS");
        } else if ("publicity".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 27);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 62, 527);
            stream.setTextMatrix(offset);
            stream.showText("PUBLICITY");
        } else if ("programming".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 19);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 62, 529);
            stream.setTextMatrix(offset);
            stream.showText("PROGRAMMING");
        } else if ("operations".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 22);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 62, 529);
            stream.setTextMatrix(offset);
            stream.showText("OPERATIONS");
        } else if ("membership".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 21);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 62, 529);
            stream.setTextMatrix(offset);
            stream.showText("MEMBERSHIP");
        } else if ("infrastructure".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 16);
            Matrix offset = Matrix.getRotateInstance(270 * Math.PI * 0.25, 64, 529);
            stream.setTextMatrix(offset);
            stream.showText("INFRASTRUCTURE");
        } else {
            System.out.println(String.format("Error: Department %s not found", attendee.getStaffDepartment()));
        }

        stream.endText();
        stream.close();

    }

    private void drawDepartmentNameFront(PDPage page, Attendee attendee) throws IOException {

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        // Bounding box:
//        stream.setNonStrokingColor(Color.red);
//        stream.fillRect(306, 361, 45, 170);

        stream.setLineWidth(0.25f);
        stream.beginText();
        stream.setStrokingColor(Color.white);
        stream.setNonStrokingColor(Color.black);
        stream.setRenderingMode(RenderingMode.FILL_STROKE);


        if ("chair".equals(attendee.getStaffDepartment().toLowerCase()) ||
                "department of the chair".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 46);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 340, 362);
            stream.setTextMatrix(offset);
            stream.showText("CHAIR");

        } else if ("treasury".equals(attendee.getStaffDepartment().toLowerCase()) ||
                "department of the treasurer".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 26);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 366);
            stream.setTextMatrix(offset);
            stream.showText("TREASURY");
        } else if ("security".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 28);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 364);
            stream.setTextMatrix(offset);
            stream.showText("SECURITY");
        } else if ("secretary".equals(attendee.getStaffDepartment().toLowerCase()) ||
                "department of the secretary".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 26);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 350);
            stream.setTextMatrix(offset);
            stream.showText("SECRETARY");
        } else if ("relations".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 25);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 364);
            stream.setTextMatrix(offset);
            stream.showText("RELATIONS");
        } else if ("publicity".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 27);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 364);
            stream.setTextMatrix(offset);
            stream.showText("PUBLICITY");
        } else if ("programming".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 19);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 362);
            stream.setTextMatrix(offset);
            stream.showText("PROGRAMMING");
        } else if ("operations".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 22);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 336, 363);
            stream.setTextMatrix(offset);
            stream.showText("OPERATIONS");
        } else if ("membership".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 21);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 335, 363);
            stream.setTextMatrix(offset);
            stream.showText("MEMBERSHIP");
        } else if ("infrastructure".equals(attendee.getStaffDepartment().toLowerCase())) {
            stream.setFont(bankGothic, 16);
            Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25, 334, 362);
            stream.setTextMatrix(offset);
            stream.showText("INFRASTRUCTURE");
        } else {
            System.out.println(String.format("Error: Department %s not found", attendee.getStaffDepartment()));
        }

        stream.endText();
        stream.close();
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

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);

        PDImageXObject xImage = PDImageXObject.createFromFile(imageFilename, document);
        Dimension scaledDim = getScaledDimension(
                                new Dimension(xImage.getWidth(),  xImage.getHeight()),
                                new Dimension(149, 158));
                                stream.drawImage(xImage,
                                                150 + ((149-scaledDim.width)/2),
                                                334 + ((158-scaledDim.height)/2),
                                                   scaledDim.width, scaledDim.height);
//            stream.fillRect(150, 334, 149, 158);
        stream.close();
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
