package org.voh.stripper;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.voh.domain.PublisherConfig;
import org.voh.domain.Style;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class RegionalPdfHtmlStripper extends PDFText2HTML {
    private static final Pattern PAGE_FOOTER = Pattern.compile("(?m)^[0-9]{1,3}$");

    private final Rectangle2D region;

    public RegionalPdfHtmlStripper(Rectangle2D region) throws IOException {
        super();
        setSortByPosition(true);
        this.region = region;
    }

    @Override
    protected void startDocument(PDDocument document) {
        // no-op: skip writing <!DOCTYPE…> <html><head>…<body>
    }

    @Override
    public void endDocument(PDDocument document) {
        // no-op: skip writing </body></html>
    }

    @Override
    protected void startArticle(boolean isLTR) {
        // no-op: skip emitting <div>
    }

    @Override
    protected void endArticle() {
        // no-op: skip emitting </div>
    }

    @Override
    protected void writePageStart() {
        /* no-op */
    }

    @Override
    protected void writePageEnd() {
        /* no-op */
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        // Only emit this text if it’s inside our region
        if (region.contains(text.getXDirAdj(), text.getYDirAdj())) {
            super.processTextPosition(text);
        }
    }

    @Override
    protected void writeString(String text, List<TextPosition> positions) throws IOException {
        // if there is no positional info, just escape+write the raw text
        if (positions == null || positions.isEmpty()) {
            output.write(escapeHtml(text));
            return;
        }

        // BUILD A STYLED FRAGMENT (with <b> and <i> only where needed)
        StringBuilder styled = new StringBuilder();
        // get style of the first glyph
        Style current = Style.getStyle(positions.getFirst());
        // open initial tags if needed
        if (current.bold())   styled.append("<b>");
        if (current.italic()) styled.append("<i>");

        // walk each TextPosition → its unicode string
        for (TextPosition tp : positions) {
            Style s = Style.getStyle(tp);
            String uni = tp.getUnicode();
            if (uni == null) continue;
            // if style changed, close old tags then open new
            if (!s.equals(current)) {
                if (current.italic()) styled.append("</i>");
                if (current.bold())   styled.append("</b>");
                current = s;
                if (current.bold())   styled.append("<b>");
                if (current.italic()) styled.append("<i>");
            }
            // append each character in that TextPosition, escaped
            for (char c : uni.toCharArray()) {
                styled.append(escapeHtml(String.valueOf(c)));
            }
        }
        // close any dangling tags
        if (current.italic()) styled.append("</i>");
        if (current.bold())   styled.append("</b>");

        String fragment = styled.toString().trim();

        // NOW HEADER VS. BODY DECISION (on the raw font size of first glyph)
        double fs = positions.getFirst().getFontSizeInPt();
        if (fs >= 36) {
            writeHeaderText("1", fragment);
        }
        else if (fs >= 14) {
            writeHeaderText("2", fragment);
        }
        else if (fs >= 12) {
            writeHeaderText("3", fragment);
        }
        else {
            // body text: write the fully-styled fragment directly
            output.write(fragment);
        }
    }

    // Simple HTML escape; leave <b> and <i> tags alone because we insert them explicitly
    private String escapeHtml(String s) {
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }


    private void writeHeaderText(String headerNum, String text) throws IOException {
        writeParagraphEnd();
        output.write("<h" + headerNum + ">" + text + "</h" + headerNum + ">");
        writeParagraphStart();
    }

    public static String readTwoColumnStyled(File pdfFile, int startPage, int endPage, PublisherConfig publisher) throws IOException {
        StringBuilder html = new StringBuilder();

        try (PDDocument doc = Loader.loadPDF(pdfFile)) {
            int total = doc.getNumberOfPages();
            int from  = Math.max(1, startPage);
            int to    = Math.min(total, endPage);

            for (int i = from - 1; i < to; i++) {
                PDPage page = doc.getPage(i);
                int index = detectBestRegionIndex(page, publisher.getLeftRegionList());

                RegionalPdfHtmlStripper leftStripper =
                        new RegionalPdfHtmlStripper(publisher.getLeftRegionList().get(index));
                leftStripper.setStartPage(i+1);
                leftStripper.setEndPage(i+1);
                html.append(leftStripper.getText(doc));

                RegionalPdfHtmlStripper rightStripper =
                        new RegionalPdfHtmlStripper(publisher.getRightRegionList().get(index));
                rightStripper.setStartPage(i+1);
                rightStripper.setEndPage(i+1);
                html.append(rightStripper.getText(doc));

//                BoundingBoxStripper.calculateMinimumRegion(publisher.getLeftRegionList().get(index), page, i);
//                BoundingBoxStripper.calculateMinimumRegion(publisher.getRightRegionList().get(index), page, i);
            }
        }

        return html.toString();
    }

    /**
     * Returns 0 if the page-number appears in the left half of the footer,
     * 1 if it appears in the right half, or -1 if neither (fallback).
     */
    public static int detectBestRegionIndex(PDPage page, List<Rectangle2D> regions) throws IOException {
        if (regions.size() == 1) {
            return 0;
        }

        PDRectangle media = page.getMediaBox();
        float x = 0;
        float y = (float) regions.getFirst().getMaxY();
        float width = media.getWidth();
        float height = media.getHeight() - y;

        // split horizontally into two halves
        Rectangle2D leftFooter = new Rectangle2D.Float(x, y, width/2f, height);
        Rectangle2D rightFooter = new Rectangle2D.Float(x + width/2f, y, width/2f, height);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);

        // register both halves
        stripper.addRegion("left",  leftFooter);
        stripper.addRegion("right", rightFooter);

        stripper.extractRegions(page);

        String leftText  = stripper.getTextForRegion("left");
        String rightText = stripper.getTextForRegion("right");

        boolean leftHasDigit  = PAGE_FOOTER.matcher(leftText).find();
        boolean rightHasDigit = PAGE_FOOTER.matcher(rightText).find();

        if (leftHasDigit && !rightHasDigit)      return 0;
        else if (!leftHasDigit && rightHasDigit) return 1;
        else return 0;
    }
}
