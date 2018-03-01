package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.IOException;

class FormatterBase {
    final PDDocument document;

    FormatterBase(PDDocument document) {
        this.document = document;
    }

    /**
     * Draws the given string, optionally supports scaling to fit.
     * @param x Left side of text, or center point of text if centered (1/72 inch)
     * @param y Bottom of text, in points (1/72 inch)
     * @param text Text to draw
     * @param optOrig Resize Options
     * @throws IOException Error generating PDF
     */
    void drawStringWithResizing(PDPageContentStream stream, float x, float y, String text, ResizeOptions optOrig) throws IOException {
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
     * Draws the given string, optionally supports scaling to fit.
     * @param x Left side of text, or center point of text if centered (1/72 inch)
     * @param y Bottom of text, in points (1/72 inch)
     * @param text Text to draw
     * @param optOrig Resize Options
     * @throws IOException Error generating PDF
     */
    void drawStringRotatedWithResizing(PDPageContentStream stream, float x, float y, int width, int height, String text, ResizeOptions optOrig) throws IOException {
        stream.setNonStrokingColor(Color.gray);
        PDRectangle boundingBox = new PDRectangle(x, y, width, height);
        stream.fillRect(x, y, width, height);

        ResizeOptions opt = new ResizeOptions(optOrig);
        float textSize = opt.font.getStringWidth(text); // in thousandths of font pt size.
        float size = opt.size;

        float centeredXPosition = x + (boundingBox.getWidth()/2f);
        float stringWidth = opt.font.getStringWidth( text );
        float centeredYPosition = y + (boundingBox.getHeight() /2f);

        stream.setNonStrokingColor(Color.GREEN);
        stream.fillRect(centeredXPosition, centeredYPosition, 3, 3);

        Matrix offset = Matrix.getRotateInstance(90 * Math.PI * 0.25,
                centeredXPosition, centeredYPosition-(stringWidth/1000/2));
        stream.beginText();
        stream.setTextMatrix(offset);

        stream.setStrokingColor(Color.black);
        stream.setNonStrokingColor(Color.black);
        stream.setFont(opt.font, size);
        stream.showText(text);
        stream.endText();

//
//        // If text size is greater than maximum width, recalculate the correct font size, based on our restrictions
//        if (textSize * (size/1000.0f) > opt.maxTextWidth) {
//            size = opt.maxTextWidth * 1000.0f / textSize;
//            if (size < opt.minFontSize) {
//                // We have utterly failed to fit the text with the minimum font size,
//                // So we're forced to use that.
//                size = opt.minFontSize;
//            }
//        }
//
//        if (opt.centered) {
//            y -= textSize * (size/(2*1000.0f));
//        }
////
////        Matrix offset = Matrix.getRotateInstance(i * Math.PI * 0.25,
////                centered XPosition, pageSize.getHeight() - centeredYPosition)
//        Matrix offset = new Matrix();
//        offset.rotate(Math.toRadians(5));
//        offset.translate(100, 100);
//        // Actually draw the text
    }


}

