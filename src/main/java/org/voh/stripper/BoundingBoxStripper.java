package org.voh.stripper;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Given an initial region, this class can be used to shrink that
 * region down to just where the text in that region is.
 */
public class BoundingBoxStripper extends PDFTextStripperByArea {
    private static final String REGION_NAME = "auto";

    /** keep a reference to our initial region */
    private final Rectangle2D initialRegion;

    /** collect every glyph inside that region */
    private final List<TextPosition> positions = new ArrayList<>();

    /**
     * @param initialRegion The rough box where you expect your text lives.
     */
    public BoundingBoxStripper(Rectangle2D initialRegion) throws IOException {
        super();
        setSortByPosition(true);
        this.initialRegion = initialRegion;
        addRegion(REGION_NAME, initialRegion);
    }

    @Override
    protected void processTextPosition(TextPosition tp) {
        // Only record glyphs that truly lie inside our initialRegion
        if (initialRegion.contains(tp.getXDirAdj(), tp.getYDirAdj())) {
            positions.add(tp);
            // still call super so the internal text‐buffer builds correctly
            super.processTextPosition(tp);
        }
    }

    /**
     * After calling extractRegions(page) you can call this to get
     * the minimal rectangle enclosing all recorded glyphs, padded by paddingPx.
     *
     * @param paddingPx how many PDF points extra to add on each edge
     */
    public Rectangle2D getTightBoundingBox(double paddingPx) {
        if (positions.isEmpty()) {
            // no text found → just return the original region
            return initialRegion;
        }

        double minX = positions.stream()
                .mapToDouble(TextPosition::getXDirAdj)
                .min().getAsDouble();

        double maxX = positions.stream()
                .mapToDouble(tp -> tp.getXDirAdj() + tp.getWidthDirAdj())
                .max().getAsDouble();

        double minY = positions.stream()
                .mapToDouble(TextPosition::getYDirAdj)
                .min().getAsDouble();

        double maxY = positions.stream()
                .mapToDouble(TextPosition::getYDirAdj)
                .max().getAsDouble();

        // clamp into original bounds plus padding
        double x0 = Math.max(initialRegion.getX(), minX - paddingPx);
        double y0 = Math.max(initialRegion.getY(), minY - paddingPx);
        double x1 = Math.min(initialRegion.getX() + initialRegion.getWidth(),  maxX + paddingPx);
        double y1 = Math.min(initialRegion.getY() + initialRegion.getHeight(), maxY + paddingPx);

        return new Rectangle2D.Float(
                (float) x0,
                (float) y0,
                (float) (x1 - x0),
                (float) (y1 - y0)
        );
    }

    public static void calculateMinimumRegion(Rectangle2D region, PDPage page, int pageNum) {
        try {
            BoundingBoxStripper stripper = new BoundingBoxStripper(region);
            stripper.extractRegions(page);
            Rectangle2D croppedRegion = stripper.getTightBoundingBox(2);

            String originalBounds = region.getMinX() + ", " + region.getMinY() +
                    " (" + region.getWidth() + ", " + region.getHeight() + ")";
            String croppedBounds = croppedRegion.getMinX() + ", " + croppedRegion.getMinY() +
                    " (" + croppedRegion.getWidth() + ", " + croppedRegion.getHeight() + ")";

            System.out.println("Page " + pageNum + " Original Region: " + originalBounds);
            System.out.println("Page " + pageNum + " Cropped Region: " + croppedBounds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
