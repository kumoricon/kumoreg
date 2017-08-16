package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.kumoricon.model.attendee.Attendee;

import java.io.IOException;

public class Staff2017 {
    public static void drawStaffName(PDPageContentStream stream, PDFont font, Attendee attendee) throws IOException {
        ResizeOptions opts = new ResizeOptions();
        opts.centered = true;
        opts.font = font;
        opts.lines = 2;
        opts.maxTextWidth = 180;
        opts.minFontSize = 8;
        opts.size = 36;

        BadgeLib.drawStringWithResizing(stream, 200, 190, attendee.getFirstName(), opts);
        BadgeLib.drawStringWithResizing(stream, 200, 160, attendee.getLastName(), opts);

    }
}
