package org.voh.domain.pf2e;

import java.util.regex.Pattern;

public enum SavesPf2e {
    FORTITUDE, REFLEX, WILL;

    private static final Pattern SAVE_PATTERN = Pattern.compile(
            "(?ix)" + // ignore-case + free-spacing
                    "\\b" + // word-boundary
                    "(?:basic\\s+)?" + // optional "basic "
                    "(?:DC\\s*(\\d{1,3})\\s+)?" + // opt. "DC 14 ", group(1)=14
                    "(Fortitude|Reflex|Will)" + // group(2)=save‐type
                    "\\s+save\\b"   // " save" + word boundary
    );

    public String getEnricher() {
        return "@Check[" + name() + "]";
    }

    public String getEnricher(String dc) {
        return getEnricher(dc, false);
    }

    public String getEnricher(String dc, boolean basic) {
        if (basic) {
            return "@Check[" + name() + "|dc:" + dc + "|basic]";
        }
        return "@Check[" + name() + "|dc:" + dc + "]";
    }
}
