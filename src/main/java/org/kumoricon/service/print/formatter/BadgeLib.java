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

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BadgeLib {

    /* Todo: badgeResourcePath should probably be configurable in the application.properties file, but
       this will have to be refactored in to a Spring service so it can be autowired instead
       of just being a static class.
    */
    private static String badgeResourcePath = "/usr/local/kumoreg/badgeResources";
    private static final Logger log = LoggerFactory.getLogger(BadgeLib.class);
    private static final Pattern badgeNumberPattern = Pattern.compile("([A-Za-z]+)(\\d+)");

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
            Path filePath = Paths.get(badgeResourcePath, "/badgeImage/", attendee.getStaffImageFilename());
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
     * Get the absolute path on disk for the age range image
     * @param attendee Current attendee
     * @param currentDateForAgeCalculation Date to base age calculation off of
     * @return File Path
     */
    static String getStaffAgeImageFilename(Attendee attendee, LocalDate currentDateForAgeCalculation) {
        String ageRange = getAgeRangeAtCon(attendee, currentDateForAgeCalculation);
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

    static int findMaxLineSize(PDFont font, String text, float maxTextWidth, float maxTextHeight) throws IOException {
        float textWidth = font.getStringWidth(text);
        Float size = maxTextHeight;
        if (textWidth * (size/1000.0f) > maxTextWidth) {
            size = maxTextWidth * 1000.0f / textWidth;
        }
        return size.intValue();
    }

    /**
     * Given a badge number AB12345, splits that in to "AB" and "12345".
     * Works with different numbers of letters and numbers
     * @param badgeNumber Badge Number
     * @return List of strings
     */
    static List<String> splitBadgeNumber(String badgeNumber) {
        List<String> parts = new ArrayList<>();
        Matcher m = badgeNumberPattern.matcher(badgeNumber);
        if (m.find()) {
            parts.add(m.group(1));
            parts.add(m.group(2));
        } else {
            parts.add(badgeNumber);
        }
        return parts;
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

    static String getAgeRangeAtCon(Attendee attendee, LocalDate currentDateForAgeCalculation) {
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


}