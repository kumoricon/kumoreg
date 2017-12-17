package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.kumoricon.model.attendee.Attendee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

        try (InputStream stream = new FileInputStream(fontPath.toFile())) {
            return PDType0Font.load(document, stream);
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
        try (InputStream stream = new FileInputStream(filePath.toFile())) {
            background = PDDocument.load(stream);
            return background;
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
            return "#FFFFFF";
        } else {
            dept = department.toLowerCase();
        }
        if ("treasury".equals(dept)) {
            return "#0a8141";
        } else if ("department of the treasurer".equals(dept)) {
            return "#0a8141";
        } else if ("secretarial".equals(dept)) {
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

    /**
     * Returns the best text color for the given background color. Ideally chooses white text on dark
     * backgrounds and black text on light backgrounds. Uses the formula from
     * http://en.wikipedia.org/wiki/HSV_color_space%23Lightness
     * Based on https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
     * @param backgroundColor HTML color code. Ex: #0C0AB1
     * @return Color (#FFFFFF for white or #000000 for black)
     */
    static Color getForegroundColor(String backgroundColor) {
        Color background = Color.decode(backgroundColor);
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;

        if (a < 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
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

    /**
     * Given a list of positions: If there is only one position and the length is greater than
     * 24 characters, wrap that line to the second line. The original list of positions is not
     * changed.
     * @param positions List of positions
     * @param maxLineLength Maximum line length in characters
     * @return List of positions
     */
    static List<String> wrapPositions(List<String> positions, int maxLineLength) {
        if (positions == null) {
            return new ArrayList<>();
        } else if (positions.size() != 1) {
            return new ArrayList<>(positions);
        } else {
            if (positions.get(0).length() <= maxLineLength) {
                return new ArrayList<>(positions);
            } else {
                List<String> output = new ArrayList<>();
                String line = positions.get(0).trim();
                int breakAt = line.lastIndexOf(" ", (line.length()/2) +5);
                if (breakAt == -1) {
                    output.add(line);
                } else {
                    output.add(line.substring(0, breakAt).trim());
                    output.add(line.substring(breakAt).trim());
                }
                return output;
            }
        }
    }


    public enum ALIGNMENT {LEFT, CENTER, RIGHT}
    public enum ROTATION {NONE, LEFT, RIGHT}

    /**
     *
     * Helper function to call drawText after converting a List of Strings in to a String[] array
     * @param document          Open PDDcument file to draw in to
     * @param page              Open PDPage (the current page to draw on)
     * @param box               The bounding box
     * @param font              The font to use
     * @param lines             The String array of text lines to draw
     * @param alignment         LEFT / CENTER / RIGHT
     * @param rotation          NONE / LEFT / RIGHT
     * @param fontLeading       The total height of a line of text, inclusive of whitespace, in point size
     * @param fontSize          Set the font size, the value of which doesn't matter if relying on autoScale
     * @param autoScale         Whether or not to automatically scale text to fit the bounding box
     * @param drawBoundingBox   Whether or not to make the bounding box visible
     * @throws IOException
     */
    @Deprecated
    static void drawText(PDDocument document, PDPage page, PDRectangle box, PDFont font, Color color, List<String> lines, ALIGNMENT alignment, ROTATION rotation, float fontLeading, float fontSize, boolean autoScale, boolean drawBoundingBox) throws IOException {
        String[] linesArray = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            linesArray[i] = lines.get(i);
        }
        drawText(document, page, box, font, color, linesArray, alignment, rotation, fontLeading, fontSize, autoScale, drawBoundingBox);
    }

    /*
     * Draws multiline text scaled to fill the given bounding box and alignment
     * with an optional left or right rotation
     *
     * alignment = left:
     * +------------------------+
     * | Line 1                 |
     * +------------------------+
     *
     * alignment = center:
     * +------------------------+
     * |         Line 1         |
     * +------------------------+
     *
     * alignment = right:
     * +------------------------+
     * |                  Line 1|
     * +------------------------+
     *
     * @param document          Open PDDcument file to draw in to
     * @param page              Open PDPage (the current page to draw on)
     * @param box               The bounding box
     * @param font              The font to use
     * @param lines             The String array of text lines to draw
     * @param alignment         LEFT / CENTER / RIGHT
     * @param rotation          NONE / LEFT / RIGHT
     * @param fontLeading       The total height of a line of text, inclusive of whitespace, in point size
     * @param fontSize          Set the font size, the value of which doesn't matter if relying on autoScale
     * @param autoScale         Whether or not to automatically scale text to fit the bounding box
     * @param drawBoundingBox   Whether or not to make the bounding box visible
     * @throws IOException
     *
     */
    static void drawText(PDDocument document, PDPage page, PDRectangle box, PDFont font, Color color, String[] lines, ALIGNMENT alignment, ROTATION rotation, float fontLeading, float fontSize, boolean autoScale, boolean drawBoundingBox) throws IOException {


        /* Prepare the content stream */
        PDPageContentStream stream = new PDPageContentStream(document, page, true, true);


        /* Define the working boundary for the text being inserted */
        float boundaryX = box.getLowerLeftX();
        float boundaryY = box.getUpperRightY();
        float boundaryWidth = box.getUpperRightX() - box.getLowerLeftX();
        float boundaryHeight = box.getUpperRightY() - box.getLowerLeftY();

        /* Define font attributes */
        if (fontSize <= 0)
            fontSize = 12.0f;

        stream.setFont(font, fontSize);

        if (fontLeading > 0)
            stream.setLeading(fontLeading);

        /* Get the size of the page in printer DPI */
        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();

        /* Draw the outline of the bounding box */
        if (drawBoundingBox == true) {
            stream.setNonStrokingColor(Color.BLUE);
            stream.addRect(box.getLowerLeftX(), box.getLowerLeftY(), box.getWidth(), box.getHeight());
            stream.fill();
            stream.setNonStrokingColor(Color.WHITE);
            stream.addRect(box.getLowerLeftX() + 1, box.getLowerLeftY() + 1, box.getWidth() - 2, box.getHeight() - 2);
            stream.fill();
        }


        /* Enter into text drawing mode */
        stream.beginText();
        stream.setNonStrokingColor(color);

        /* Create a TextMatrix object */
        // The text matrix allows drawing text normally without consideration for where it is located,
        // how big it is, or what direction it is facing
        Matrix matrix = new Matrix();

        /* Determine the value to scale the text to fit the boundary box, taking into account optional rotation */

        // Get the widths of each line
        float[] lineWidths = new float[lines.length];
        for (int i = 0; i < (lines.length); i++) {
            lineWidths[i] = font.getStringWidth(lines[i])/1000f*fontSize;
        }

        // Get the width of the longest line
        Arrays.sort(lineWidths);
        float maxlineWidth = lineWidths[(lines.length-1)];

        // Calculate autoScaleFactor based on the type of rotation
        float autoScaleFactor = 1.0f;
        if (rotation == ROTATION.RIGHT) {
            //The boundaryWidth and boundaryHeight variables are swapped for the rotate right and left cases

            // Calculate the scale factor to fit the longest line in the bounding box
            float fitWidthScaleFactor = boundaryHeight / maxlineWidth;

            // Determine the value to scale the combined height of text to fit the boundary box
            float fitHeightScaleFactor = boundaryWidth / (lines.length*fontLeading);

            // Go with the smaller of the calculated width and height scale values
            if (fitHeightScaleFactor < fitWidthScaleFactor)
                autoScaleFactor = fitHeightScaleFactor;
            else
                autoScaleFactor = fitWidthScaleFactor;

            matrix.translate(boundaryX+boundaryWidth, boundaryY);
            matrix.rotate(-Math.PI/2);
        } else if (rotation == ROTATION.LEFT){
            //The boundaryWidth and boundaryHeight variables are swapped for the rotate right and left cases

            // Calculate the scale factor to fit the longest line in the bounding box
            float fitWidthScaleFactor = boundaryHeight / maxlineWidth;

            // Determine the value to scale the combined height of text to fit the boundary box
            float fitHeightScaleFactor = boundaryWidth / (lines.length*fontLeading);

            // Go with the smaller of the calculated width and height scale values
            if (fitHeightScaleFactor < fitWidthScaleFactor)
                autoScaleFactor = fitHeightScaleFactor;
            else
                autoScaleFactor = fitWidthScaleFactor;

            matrix.translate(boundaryX,boundaryY-boundaryHeight);
            matrix.rotate(Math.PI/2);
        } else {
            // Calculate the scale factor to fit the longest line in the bounding box
            float fitWidthScaleFactor = boundaryWidth / maxlineWidth;

            // Determine the value to scale the combined height of text to fit the boundary box
            float fitHeightScaleFactor = boundaryHeight / (lines.length*fontLeading);

            // Go with the smaller of the calculated width and height scale values
            if (fitHeightScaleFactor < fitWidthScaleFactor)
                autoScaleFactor = fitHeightScaleFactor;
            else
                autoScaleFactor = fitWidthScaleFactor;

            // Determine the Y offset for the starting point of the text
            float textYOffset = 0.0f;
            if (autoScaleFactor == fitWidthScaleFactor)
                textYOffset = (boundaryHeight-(lines.length)*fontSize)/2;
            else {
                if (autoScale)
                    textYOffset = (fontLeading - fontSize) * lines.length / 2;
                else
                    textYOffset = 0.0f;
            }
            matrix.translate(boundaryX,boundaryY-textYOffset);
        }


        /* Scale the text if desired */
        if (autoScale)
            matrix.scale(autoScaleFactor,autoScaleFactor);
        else
            autoScaleFactor = 1.0f;


        /* Apply the text matrix to the content stream */
        stream.setTextMatrix(matrix);


        /* Draw the lines of text */
        for (int i = 0; i < lines.length; i++)
        {
            // Default the line offset to zero for ALIGNMENT.LEFT and adjust for other types of alignment
            float lineOffset = 0f;

            if (alignment == ALIGNMENT.CENTER) {
                if (rotation == ROTATION.RIGHT || rotation == ROTATION.LEFT)
                    lineOffset = (boundaryHeight / autoScaleFactor / 2) - (font.getStringWidth(lines[i]) / 1000f * fontSize / 2);
                if (rotation == ROTATION.NONE)
                    lineOffset = (boundaryWidth / autoScaleFactor / 2) - (font.getStringWidth(lines[i]) / 1000f * fontSize / 2);
            }

            if (alignment == ALIGNMENT.RIGHT) {
                if (rotation == ROTATION.RIGHT || rotation == ROTATION.LEFT)
                    lineOffset = (boundaryHeight / autoScaleFactor) - (font.getStringWidth(lines[i]) / 1000f * fontSize);
                if (rotation == ROTATION.NONE)
                    lineOffset = (boundaryWidth / autoScaleFactor) - (font.getStringWidth(lines[i]) / 1000f * fontSize);
            }

            // Move the cursor to the appropriate new location relative to its current old location
            stream.newLineAtOffset(lineOffset, -fontLeading);

            // Draw the text
            stream.showText(lines[i]);

            // Reset the cursor to a predictable state for the next loop iteration
            stream.moveTextPositionByAmount(-lineOffset,0);
        }


        stream.endText();
        stream.close();
    }


}
