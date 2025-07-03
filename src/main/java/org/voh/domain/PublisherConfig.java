package org.voh.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum PublisherConfig {
    GENERAL("General",
            List.of(new Rectangle2D.Float(  0f, 50f, 306f, 742f)),
            List.of(new Rectangle2D.Float(306f, 50f, 306f, 742f)
    )),
    JEFF_STEVENS("Jeff Stevens Games",
            List.of(new Rectangle2D.Float( 70f,  50f, 220f, 680f)),
            List.of(new Rectangle2D.Float(300f, 50f, 220f, 680f)
    )),
    PAIZO("Paizo",
            List.of(new Rectangle2D.Float(90f, 80f, 220f, 635f),
                    new Rectangle2D.Float(60f, 80f, 220f, 635f)),
            List.of(new Rectangle2D.Float(320f, 80f, 220f, 635f),
                    new Rectangle2D.Float(290f, 80f, 220f, 635f)
            )),
    ROLL_FOR_COMBAT("Roll for Combat",
            List.of(new Rectangle2D.Float(90f, 80f, 230f, 645f),
                    new Rectangle2D.Float(50f, 80f, 230f, 645f)),
            List.of(new Rectangle2D.Float(330f, 80f, 230f, 645f),
                    new Rectangle2D.Float(290f, 80f, 230f, 645f)
    ));

    private final String name;
    private final List<Rectangle2D> leftRegionList;
    private final List<Rectangle2D> rightRegionList;

    public static List<String> displayNames() {
        return Arrays.stream(values()).map(s -> s.name).toList();
    }

    public static String defaultDisplayName() {
        return GENERAL.name;
    }

    public static PublisherConfig getFromName(String name) {
        return Arrays.stream(PublisherConfig.values())
                .filter(c -> c.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}
