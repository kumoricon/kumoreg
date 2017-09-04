package org.kumoricon.service.print.formatter;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Created by jason on 8/15/17.
 */
public class ResizeOptions {
    PDFont font = PDType1Font.HELVETICA_BOLD;
    float size = 14;
    float minFontSize = 10;
    float maxTextWidth = 0;
    boolean centered = true; // Should possibly be a Right | Left | Center enum?
    int lines = 1; // To support splitting into multiple lines. Currently unsupported.
    float lineSize = size * 1.3f;

    public ResizeOptions() {}
    public ResizeOptions(ResizeOptions other) {
        this.font = other.font;
        this.size = other.size;
        this.minFontSize = other.minFontSize;
        this.maxTextWidth = other.maxTextWidth;
        this.centered = other.centered;
        this.lines = other.lines;
        this.lineSize = other.lineSize;
    }
}
