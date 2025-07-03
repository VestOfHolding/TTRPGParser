package org.voh.postprocess.dnd5e;

import org.voh.domain.dnd5e.Abilities5e;
import org.voh.domain.dnd5e.Conditions5e;
import org.voh.domain.dnd5e.Skills5e;

public class Dnd5ePostProcessor {

    public static String postProcess(String html) {
        html = enrichSavingThrows(html);
        html = enrichConditions(html);
        html = enrichSkillChecks(html);

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
}
