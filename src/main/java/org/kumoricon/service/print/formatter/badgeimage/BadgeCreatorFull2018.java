package org.kumoricon.service.print.formatter.badgeimage;


import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BadgeCreatorFull2018 implements BadgeCreator {
    private static final int DPI = 150;
    private static final int BADGE_WIDTH = 5*DPI;
    private static final int BADGE_HEIGHT = 4*DPI;

    @Override
    public byte[] createBadge(AttendeeBadgeDTO attendee) {
        BadgeImage b = new BadgeImage(BADGE_WIDTH, BADGE_HEIGHT, DPI);

        drawBadgeTypeStripe(b, attendee);
        drawAgeColorStripe(b, attendee);
        drawFanName(b, attendee);
        drawName(b, attendee);
        drawBadgeNumber(b, attendee);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(b.getImage(), "png", baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void drawAgeColorStripe(BadgeImage b, AttendeeBadgeDTO attendee) {
        Color bgColor = Color.decode(attendee.getAgeStripeBackgroundColor());
        Color fgColor = getForegroundColor(attendee.getAgeStripeBackgroundColor());
        Rectangle ageBackground = new Rectangle(60, 457, 630, 80);
        b.fillRect(ageBackground, bgColor);

        Rectangle ageTextBoundingBox = new Rectangle(70, 450, 610, 80);
        b.drawStretchedCenteredString(attendee.getAgeStripeText(), ageTextBoundingBox, nameFont(), fgColor);
    }

    private void drawName(BadgeImage b, AttendeeBadgeDTO attendee) {
        // If Fan Name is null, draw the full name in that spot instead in drawFanName()
        String fanName = attendee.getFanName();
        if (fanName != null) {
            Rectangle nameBg = new Rectangle(70, 340, 360, 40);
            b.drawStretchedCenteredString(attendee.getFirstName() + " " + attendee.getLastName(), nameBg, nameFont(), Color.BLACK);
        }
    }

    private static void drawFanName(BadgeImage b, AttendeeBadgeDTO attendee) {
        String name;
        Rectangle fanNameBg;
        if (attendee.getFanName() != null && !attendee.getFanName().trim().equals("")) {
            fanNameBg = new Rectangle(70, 275, 360, 70);
            name = attendee.getFanName();
        } else {
            // Draw regular name in the combined name and fan name spaces
            fanNameBg = new Rectangle(70, 275, 360, 130);
            name = attendee.getFirstName() + " " + attendee.getLastName();
        }

        b.drawStretchedLeftAlignedString(name, fanNameBg, nameFont(),Color.BLACK);
    }

    private static void drawBadgeTypeStripe(BadgeImage b, AttendeeBadgeDTO attendee) {
        if (attendee != null) {
            Color bgColor = Color.decode(attendee.getBadgeTypeBackgroundColor());
            Color fgColor = getForegroundColor(attendee.getBadgeTypeBackgroundColor());
            Rectangle badgeType = new Rectangle(60, 70, 630, 80);
            b.fillRect(badgeType, bgColor);

            Rectangle textBoundingBox = new Rectangle(80, 80, 610, 70);
            b.drawStretchedCenteredString(attendee.getBadgeTypeText(), textBoundingBox, nameFont(), fgColor);
        }
    }

    private static void drawBadgeNumber(BadgeImage b, AttendeeBadgeDTO attendee) {
        String badgeNumber = attendee.getBadgeNumber();
        Color fgColor = getForegroundColor(attendee.getAgeStripeBackgroundColor());
        if (badgeNumber.length() == 8) {
            String badgeNumber1 = badgeNumber.substring(0, 3);
            String badgeNumber2 = badgeNumber.substring(3);
            Rectangle badgeNumberBounds1 = new Rectangle(85, 460, 90, 35);
            Rectangle badgeNumberBounds2 = new Rectangle(85, 480, 90, 45);
            b.drawStretchedCenteredString(badgeNumber1, badgeNumberBounds1, nameFont(), fgColor);
            b.drawStretchedCenteredString(badgeNumber2, badgeNumberBounds2, nameFont(), fgColor);
        } else {
            Rectangle badgeNumberBounds = new Rectangle(85, 460, 90, 90);
            b.drawStretchedCenteredString(badgeNumber, badgeNumberBounds, nameFont(), fgColor);
        }
    }


    private static Font nameFont() {
        Font f = new Font("Dialog", Font.BOLD, 36);
        return f;
    }

    private static void save(BufferedImage image, String filename) {
        Path output = Paths.get(filename);
        try {
            ImageIO.write(image, "png", output.toFile());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
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
    private static Color getForegroundColor(String backgroundColor) {
        Color background = Color.decode(backgroundColor);
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;

        if (a < 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }
}
