package org.voh.postprocess.pf2e;

import org.voh.domain.pf2e.ConditionsPf2e;

import java.util.regex.MatchResult;

import static org.voh.domain.pf2e.SavesPf2e.*;

public class Pf2ePostProcessor {

    public static String postProcess(String html) {
        html = enrichConditions(html);
        html = enrichSaves(html);

        return html;
    }

    public static String enrichConditions(String html) {
        return ConditionsPf2e.PATTERN
                .matcher(html)
                .replaceAll((MatchResult mr) -> {
                    ConditionsPf2e c = ConditionsPf2e.BY_NAME.get(mr.group(1).toLowerCase());
                    if (c == null) return mr.group(0);
                    String val = mr.group(2);
                    return (c.isHasValue() && val != null)
                            ? c.getEnricher(val)
                            : c.getEnricher();
                });
    }

    public static String enrichSaves(String html) {
        html = BASIC.matcher(html).replaceAll(mr -> {
            // whichever group isn’t null is the one that matched
            String dc = mr.group(1) != null ? mr.group(1)
                    : mr.group(4) != null ? mr.group(4)
                    : mr.group(6);
            String type = mr.group(2) != null ? mr.group(2)
                    : mr.group(3) != null ? mr.group(3)
                    : mr.group(5);
            return "@Check[type:" + type.toLowerCase() + "|dc:" + dc + "|basic] save";
        });

        html = PARENS_SAVE.matcher(html).replaceAll(mr ->
                "@Check[type:" + mr.group(1).toLowerCase() + "|dc:" + mr.group(2) + "] save");

        html = SIMPLE_SAVE.matcher(html).replaceAll(mr -> {
            String dc = mr.group(1) != null ? mr.group(1) : mr.group(3);
            return "@Check[type:" + mr.group(2).toLowerCase() + "|dc:" + dc + "]";
        });

        return html;
    }
}
