package org.kumoricon.service.print.formatter;


import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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

    private static String badgeResourcePath = "/tmp/training/badge";
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

    static PDPage importPageBackground(PDDocument document, String filename) {
        Path filePath = Paths.get(badgeResourcePath, filename);
        PDDocument background = null;
        try {
            background = PDDocument.load(filePath.toFile());
            PDPage templatePage = (PDPage)background.getDocumentCatalog().getAllPages().get(0);
            COSDictionary pageDict = templatePage.getCOSDictionary();
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
        } finally {
            if (background != null) {
                try {
                    background.close();
                } catch (IOException e) {
                    log.error("Error closing PDF", e);
                }
            }
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

}
