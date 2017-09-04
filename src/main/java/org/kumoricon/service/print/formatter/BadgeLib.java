package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.kumoricon.model.attendee.Attendee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    /**
     * Open an existing PDF from the resource path. If it can't be loaded, return a blank page
     * @param filename File name
     * @return PDDocument
     */
    static PDDocument loadBackground(String filename) {
        Path filePath = Paths.get(badgeResourcePath, filename);
        PDDocument background;
        try {
            background = PDDocument.load(filePath.toFile());
        } catch (IOException ex) {
            log.warn("Couldn't load PDF {}, falling back to blank page", filename);
            background = new PDDocument();
            background.addPage(new PDPage());
        }
        return background;
    }

    /**
     * Get the absolute path on disk of the staff image from an Attendee record. Returns null if
     * the Attendee has no image
     * @param attendee Attendee
     * @return File path
     */
    static String getStaffImageFilename(Attendee attendee) {
        if (attendee.getStaffImageFilename() != null) {
            Path filePath = Paths.get(badgeResourcePath, "/badgeimage/", attendee.getStaffImageFilename());
            return filePath.toAbsolutePath().toString();
        }
        return null;
    }


    /**
     * Get the absolute path on disk of the mascot image
     * @return File path
     */
    static String getMascotImageFilename() {
        Path filePath = Paths.get(badgeResourcePath, "kumoricon_2017-mascot_chibi.png");
        return filePath.toAbsolutePath().toString();
    }

    /**
     * Get the absolute path on disk of the age overlay image
     * @param ageRange "adult", "youth", or "child"
     * @return File path
     */
    static String getStaffAgeImageFilename(String ageRange) {
        if ("adult".equals(ageRange.toLowerCase())) {
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
     * Calculates the maximum font size that can be used to fit in to a given bounding box.
     * Assumes that landscape (wider than taller) boxes are using horizontal text and
     * portrait (taller than wider) boxes are using text rotated 90 degrees
     * @param font Font text will be drawn in
     * @param text Lines of text to be drawn. Must contain at least one line
     * @param boundingBox Bounds to fit text in
     * @return font size
     * @throws IOException Error working with font bubbled up from PDFBox
     */
    static int findMaxFontSize(PDFont font, List<String> text, PDRectangle boundingBox) throws IOException {
        if (text.size() == 0) {
            throw new RuntimeException("findMaxFontSize called with empty text argument");
        }
        float maxTextWidth;
        float maxTextHeight;
        if (boundingBox.getWidth() > boundingBox.getHeight()) {
            maxTextWidth = boundingBox.getWidth();
            maxTextHeight = boundingBox.getHeight();
        } else {
            maxTextWidth = boundingBox.getHeight();
            maxTextHeight = boundingBox.getWidth();
        }

        Float maxLineSize = Float.MAX_VALUE;
        for (String line : text) {
            float lineSize = findMaxLineSize(font, line, maxTextWidth, maxTextHeight);
            if (lineSize < maxLineSize) {
                maxLineSize = lineSize;
            }
        }
        if (maxLineSize * text.size() > maxTextHeight ) {
            maxLineSize = maxLineSize / text.size();
        }
        return  maxLineSize.intValue();
    }

    private static int findMaxLineSize(PDFont font, String text, float maxTextWidth, float maxTextHeight) throws IOException {
        float textSize = font.getStringWidth(text);
        Float size = maxTextHeight;
        if (textSize * (size/1000.0f) > maxTextWidth) {
            size = maxTextWidth * 1000.0f / textSize;
        }
        return size.intValue();
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
            return "#3953a4"; // Not sure which color code is correct, this is from 2016
//            return "#3a53a5";
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
