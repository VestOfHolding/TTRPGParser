package org.voh.domain;

import org.apache.pdfbox.text.TextPosition;

public record Style(boolean bold, boolean italic, String font, float size) {

    public static Style getStyle(TextPosition tp) {
        String fn = tp.getFont().getName();
        float fs = tp.getFontSizeInPt();
        boolean b = fn.toLowerCase().contains("bold");
        boolean i = fn.toLowerCase().contains("italic") || fn.toLowerCase().contains("oblique");

        return new Style(b,i, fn, fs);
    };
}
