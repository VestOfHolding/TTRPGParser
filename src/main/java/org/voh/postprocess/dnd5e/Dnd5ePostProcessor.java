package org.voh.postprocess.dnd5e;

import org.voh.domain.dnd5e.Abilities5e;
import org.voh.domain.dnd5e.Conditions5e;
import org.voh.domain.dnd5e.Skills5e;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dnd5ePostProcessor {

    public static String postProcess(String html) {
        html = enrichSavingThrows(html);
        html = enrichConditions(html);
        html = enrichSkillChecks(html);
        html = enrichDuration(html);
        html = enrichAttacks(html);

        return html;
    }

    public static String enrichSavingThrows(String html) {
        return Abilities5e.SAVING_THROW
                .matcher(html).replaceAll(match -> {
                    String prefix = match.group(1) == null ? "" : match.group(1);
                    String dc = match.group(2);
                    String nm = match.group(3).substring(0,3).toUpperCase();

                    Abilities5e a = Abilities5e.valueOf(nm);
                    return prefix + ((dc == null)
                            ? a.getEnricher()
                            : a.getEnricher(Integer.parseInt(dc)));
        });
    }

    public static String enrichConditions(String html) {
        return Conditions5e.PATTERN.matcher(html)
                .replaceAll(match ->
                        Conditions5e.valueOf(match.group(1).toUpperCase()).getEnricher());
    }

    public static String enrichSkillChecks(String html) {
        return Skills5e.SKILL_CHECK.matcher(html)
                .replaceAll(match -> {
                    String prefix = match.group(1) == null ? "" : match.group(1);
                    String dcText     = match.group(2);
                    String skillName  = match.group(4).toLowerCase();

                    Skills5e skill = Skills5e.BY_NAME.get(skillName);
                    if (skill == null) return match.group(0); // fallback

                    return prefix + ((dcText == null)
                            ? skill.getEnricher()
                            : skill.getEnricher(Integer.parseInt(dcText)));
                });
    }

    public static String enrichDuration(String html) {
        return html.replaceAll(
                "(?i)\\b(\\d+)d(\\d+) (rounds|minutes|hours|days)\\b",
                "[[/r $1d$2 #$3]]{$1d$2 $3}"
        );
    }

    public static String enrichAttacks(String html) {
        List<int[]> enrichedSpans = extractEnrichedSpans(html);
        html = enrichDiceDamage(html);
        html = enrichFlatDamage(html, enrichedSpans);
        html = enrichTrailingDice(html);
        return html;
    }

    private static String enrichDiceDamage(String html) {
        Matcher matcher = Pattern.compile("(?i)\\b(\\d+)d(\\d+)(\\+\\d+)?(?: (\\w+))? damage\\b").matcher(html);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String replacement = formatDamage(matcher.group(1) + "d" + matcher.group(2) +
                    (matcher.group(3) != null ? matcher.group(3) : ""),
                    matcher.group(4));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String enrichFlatDamage(String html, List<int[]> enrichedSpans) {
        Pattern flatPattern = Pattern.compile("(?i)\\b(\\d+) (\\w+) damage\\b");
        Matcher matcher = flatPattern.matcher(html);

        int lastEnd = 0;
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            int start = matcher.start();
            sb.append(html, lastEnd, start);
            sb.append(isInsideEnriched(start, enrichedSpans) ? matcher.group() : formatDamage(matcher.group(1), matcher.group(2)));
            lastEnd = matcher.end();
        }

        sb.append(html.substring(lastEnd));
        return sb.toString();
    }

    private static String enrichTrailingDice(String html) {
        Pattern trailingDicePattern = Pattern.compile("(?i)\\b(\\d+)d(\\d+)(?: (\\w+))?([.,])");
        Matcher matcher = trailingDicePattern.matcher(html);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String replacement = formatDamage(matcher.group(1) + "d" + matcher.group(2),
                    matcher.group(3)) + matcher.group(4);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String formatDamage(String expression, String type) {
        return type != null ? "[[/damage " + expression + " " + type + "]]" : "[[/damage " + expression + "]]";
    }

    private static List<int[]> extractEnrichedSpans(String html) {
        return Pattern.compile("\\[\\[/damage .*?]]").matcher(html).results()
                .map(mr -> new int[]{mr.start(), mr.end()})
                .toList();
    }

    private static boolean isInsideEnriched(int pos, List<int[]> ranges) {
        return ranges.stream().anyMatch(range -> pos >= range[0] && pos < range[1]);
    }
}
