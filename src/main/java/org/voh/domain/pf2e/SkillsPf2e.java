package org.voh.domain.pf2e;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SkillsPf2e {
    ACROBATICS,
    ARCANA,
    ATHLETICS,
    CRAFTING,
    DECEPTION,
    DIPLOMACY,
    INTIMIDATION,
    MEDICINE,
    NATURE,
    PERCEPTION,
    OCCULTISM,
    PERFORMANCE,
    RELIGION,
    SOCIETY,
    STEALTH,
    SURVIVAL,
    THIEVERY;

    public String getEnricher() {
        return "@Check[" + name() + "]";
    }

    public String getEnricher(String dc) {
        return "@Check[" + name() + "|dc:" + dc + "]";
    }

    public static final String NAME_REGEX = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.joining("|")) + "|Lore\\([^)]*\\)";

    private static final Pattern SKILL_CHECK = Pattern.compile(
            "(?ix)" + // ignore-case + free-spacing
                    "\\b" + // word-boundary
                    "(?:(?:succeed(?:s|ing)?\\s+on|" + // "succeed on", "succeeds on", "succeeding on"
                    "make(?:s|ing)?(?:\\s+a)?|" + // "make", "makes", "making", "make a"
                    "attempt(?:s)?(?:\\s+a)?|" + // "attempt", "attempts", "attempt a"
                    "a\\s+successful)\\s+)?" + // "a successful"
                    "(?:DC\\s*(\\d{1,3})\\s+)?" + // optional "DC 15 ", group(1)=15
                    "(" + NAME_REGEX + ")" + // group(2)=skill name
                    "\\s+check\\b"    // " check" + word-boundary
    );

    public static final Map<String, SkillsPf2e> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(
                    s -> s.name().toLowerCase(),
                    s -> s));
}
