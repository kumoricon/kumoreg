package org.kumoricon.service.print.formatter;


import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.kumoricon.model.attendee.Attendee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class BadgeLib {

    private static String badgeResourcePath = "/home/jason/kumoreg_resources";
    private static final Logger log = LoggerFactory.getLogger(BadgeLib.class);

    /**
     * Loads BankGothic Md BT Medium.ttf in to a given document or falls back to Helvetica Bold
     * @param document Open PDDocument (PDF)
     * @return font
     */
    static PDFont loadFont(PDDocument document) {
        Path fontPath = Paths.get(badgeResourcePath, "/Bitstream - BankGothic Md BT Medium.ttf");
        try {
            return PDTrueTypeFont.loadTTF(document, fontPath.toFile());
        } catch (IOException ex) {
            log.warn("Error, couldn't load font '{}'", fontPath.toAbsolutePath());
            return PDType1Font.HELVETICA_BOLD;
        }
    }

    static PDDocument loadBackground(String filename) {
        Path filePath = Paths.get(badgeResourcePath, filename);
        PDDocument background = null;
        try {
            background = PDDocument.load(filePath.toFile());
        } catch (IOException ex) {
            log.warn("Couldn't load PDF {}, falling back to blank page", filename);
            background = new PDDocument();
            background.addPage(new PDPage());
        }
        return background;
    }

    static PDPage importPageBackground(PDDocument document, String filename) {
        Path filePath = Paths.get(badgeResourcePath, filename);
        PDDocument background = null;
        try {
            background = PDDocument.load(filePath.toFile());
            PDPage templatePage = (PDPage)background.getDocumentCatalog().getPages().get(0);
            COSDictionary pageDict = templatePage.getCOSObject();
            COSDictionary newPageDict = new COSDictionary(pageDict);
            newPageDict.removeItem(COSName.ANNOTS);
            newPageDict.removeItem(COSName.ACTUAL_TEXT);
            PDPage newPage = new PDPage(newPageDict);
//            background.close();
            return document.importPage(newPage);
        } catch (IOException ex) {
            log.warn("Error, couldn't load background PDF '{}', using default", filename);
            try {
                return document.importPage(new PDPage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static String getStaffImageFilename(Attendee attendee) {
        if (attendee.getStaffImageFilename() != null) {
            Path filePath = Paths.get(badgeResourcePath, "/badgeimage/", attendee.getStaffImageFilename());
            return filePath.toAbsolutePath().toString();
        }
        return null;
    }

    static String getMascotImageFilename() {
        Path filePath = Paths.get(badgeResourcePath, "kumoricon_2017-mascot_chibi.png");
        return filePath.toAbsolutePath().toString();
    }

    static String getStaffAgeImageFilename(String ageRange) {
        if (ageRange == null) {
            return null;
        } else if ("adult".equals(ageRange.toLowerCase())) {
            Path filePath = Paths.get(badgeResourcePath, "staffadult.png");
            return filePath.toAbsolutePath().toString();
        } else if ("youth".equals(ageRange.toLowerCase())) {
            Path filePath = Paths.get(badgeResourcePath, "staffyouth.png");
            return filePath.toAbsolutePath().toString();
        } else if ("child".equals(ageRange.toLowerCase())) {
            Path filePath = Paths.get(badgeResourcePath, "staffchild.png");
            return filePath.toAbsolutePath().toString();
        } else {
            return null;
        }
    }

    /**
     * Draws the given string, optionally supports scaling to fit.
     * @param x Left side of text, or center point of text if centered (1/72 inch)
     * @param y Bottom of text, in points (1/72 inch)
     * @param text Text to draw
     * @param optOrig Resize Options
     * @throws IOException Error generating PDF
     */
    public static void drawStringWithResizing(PDPageContentStream stream, float x, float y, String text, ResizeOptions optOrig) throws IOException {
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
        stream.beginText();
        stream.setStrokingColor(Color.black);
        stream.setNonStrokingColor(Color.black);
        stream.moveTextPositionByAmount(x, y);
        stream.setFont(opt.font, size);
        stream.drawString(text);
        stream.endText();
    }

    /**
     * For a department name, lookup and return the HTML color code for their background
     * @param department Department name
     * @return String color code (ex: #FF00EC)
     */
    public static String findDepartmentColorCode(String department) {
        String dept;
        if (department == null) {
            dept= "";
        } else {
            dept = department.toLowerCase();
        }
        if ("treasury".equals(dept)) {
            return "#0a8141";
        } else if ("department of the treasurer".equals(dept)) {
            return "#0a8141";
        } else if ("secretary".equals(dept)) {
            return "#3a53a5";
        } else if ("department of the secretary".equals(dept)) {
            return "#3a53a5";
        } else if ("relations".equals(dept)) {
            return "#f282b4";
        } else if ("publicity".equals(dept)) {
            return "#e0e0e0";
        } else if ("programming".equals(dept)) {
            return "#6b52a2";
        } else if ("operations".equals(dept)) {
            return "#ec2426";
        } else if ("membership".equals(dept)) {
            return "#f57f20";
        } else if ("infrastructure".equals(dept)) {
            return "#414242";
        } else if ("chair".equals(dept)) {
            return "#f99f1d";
        } else if ("department of the chair".equals(dept)) {
            return "#f99f1d";
        } else {
            System.out.println("Warning, couldn't find color code for " + department);
            return "#FFFFFF";
        }
    }

}
