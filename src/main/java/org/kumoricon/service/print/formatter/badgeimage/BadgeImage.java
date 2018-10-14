package org.kumoricon.service.print.formatter.badgeimage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Class for creating badge images in PNG format. Contains functions to do the drawing
 * and text placement, plus the image itself.
 */
public class BadgeImage {
    private final BufferedImage image;
    private final Graphics2D g2;
    private final int dpi;

    /**
     * Create new BadgeImage instance
     * @param width Width in pixels
     * @param height Height in pixels
     * @param dpi DPI
     */
    BadgeImage(int width, int height, int dpi) {
        this.dpi = dpi;

        BufferedImage background;

        try {
            background = ImageIO.read(new File("badgebackground.png"));
        } catch (IOException e) {
            background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        image = background;
        g2 = image.createGraphics();
    }

    /**
     * Draw a string centered in the given rectangle, stretched to fill it minus 10 pixels of padding
     * @param text Text to draw
     * @param rect Rectangle the text will fill
     * @param font Text font
     * @param color Text color
     */
    void drawStretchedCenteredString(String text, Rectangle rect, Font font, Color color) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font);
        drawCenteredString(text, paddedRect, sizedFont, color);
    }


    /**
     * Draw a string that is stretched to fill the given rectangle, minus a 10 pixel padding
     * @param text Text to draw
     * @param rect Rectangle the text will fill
     * @param font Text font
     * @param color Text color
     */
    void drawStretchedLeftAlignedString(String text, Rectangle rect, Font font, Color color) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font);
        drawLeftAlignedString(text, paddedRect, sizedFont, color);
    }

    /**
     * Draw a string that is stretched to fill the given rectangle, minus a 10 pixel padding
     * @param text Text to draw
     * @param rect Rectangle the text will fill
     * @param font Text font
     * @param color Text color
     */
    void drawStretchedRightAlignedString(String text, Rectangle rect, Font font, Color color) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font);

        drawRightAlignedString(text, paddedRect, sizedFont, color);
    }


    void drawCenteredString(String text, Rectangle rect, Font font, Color color) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.setFont(font);

        drawTextOutline(text, color, x, y);

        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    void drawTextOutline(String text, Color color, int x, int y) {
        g2.setColor(getInverseColor(color));
        g2.drawString(text, x-2, y);
        g2.drawString(text, x+2, y);
        g2.drawString(text, x, y-2);
        g2.drawString(text, x, y+2);
        g2.drawString(text, x-2, y-2);
        g2.drawString(text, x-2, y+2);
        g2.drawString(text, x+2, y-2);
        g2.drawString(text, x+2, y+2);
    }

    void drawLeftAlignedString(String text, Rectangle rect, Font font, Color color) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + 10;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.setFont(font);
        drawTextOutline(text, color, x, y);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    void drawRightAlignedString(String text, Rectangle rect, Font font, Color color) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text));
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.setFont(font);
        drawTextOutline(text, color, x, y);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }


    Font scaleFont(String text, Rectangle rect, Font font) {
        float fontSizeByWidth = 100.0f;
        float fontSizeByHeight = 100.0f;
        Font tFont = font.deriveFont(fontSizeByWidth);
        int width = g2.getFontMetrics(tFont).stringWidth(text);
        fontSizeByWidth = (((float)rect.width / (float)width ) * fontSizeByWidth)-2;

        int height = g2.getFontMetrics(tFont).getHeight();
        fontSizeByHeight = ((float)rect.height / (float)height) * fontSizeByHeight;

        if (fontSizeByHeight < fontSizeByWidth) {
            return font.deriveFont(fontSizeByHeight);
        } else {
            return font.deriveFont(fontSizeByWidth);
        }
    }

    BufferedImage getImage() {
        return image;
    }

    void fillRect(final Rectangle rectangle, final Color color) {
        g2.setColor(color);
        g2.setBackground(color);
        g2.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }


    private Rectangle getPaddedRect(Rectangle rect) {
        if (rect.getHeight() > 40 && rect.getWidth() > 40) {
            return new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
        } else {
            int paddingWidth = Math.round((float)rect.getWidth() * .1f);
            int paddingHeight = Math.round((float)rect.getHeight() * .1f);
            if (paddingWidth < 2) paddingWidth = 2;
            if (paddingHeight < 2) paddingHeight = 2;
            return new Rectangle(rect.x + paddingWidth,
                    rect.y + paddingHeight,
                    rect.width-(paddingWidth*2),
                    rect.height-(paddingHeight*2));
        }
    }

    private static Color getInverseColor(Color background) {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;

        if (a < 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    void drawVerticalCenteredString(String ageStripeText, Rectangle ageBackground, Font font, Color fgColor) {
        String text = ageStripeText.toUpperCase();

        int letterBoundingBoxHeight = ageBackground.height / ageStripeText.length();

        for (int i = 0; i < ageStripeText.length(); i++) {
            Rectangle letterBoundingBox = new Rectangle(ageBackground.x,
                    ageBackground.y + (letterBoundingBoxHeight * i),
                    ageBackground.width,
                    letterBoundingBoxHeight);
            drawStretchedCenteredString(text.substring(i, i+1), letterBoundingBox, font, fgColor);
        }
    }

    static String buildTitleString(String input) {
        if (input == null) return "";
        char[] characters = input.toUpperCase().toCharArray();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            output.append(characters[i]);
            if (i < characters.length-1) output.append(" ");
        }
        return output.toString();
    }
}
