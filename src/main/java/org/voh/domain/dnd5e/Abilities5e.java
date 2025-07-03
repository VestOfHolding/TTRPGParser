package org.voh.domain.dnd5e;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Abilities5e {
    STR("Strength"),
    DEX("Dexterity"),
    CON("Constitution"),
    INT("Intelligence"),
    WIS("Wisdom"),
    CHA("Charisma");

    private final String fullName;

    public static final String NAME_REGEX = Arrays.stream(values())
            .map(a -> a.fullName)
            .collect(Collectors.joining("|"));

    public static final Pattern SAVING_THROW = Pattern.compile(
            "(?i)\\b" +
                    "((?:a|an)\\s+)?" +           // (1) article + space
                    "(?:DC\\s*(\\d{1,3})\\s+)?" + // (2) optional DC number
                    "(" + NAME_REGEX + ")" +     // (3) the ability name
                    "(?=\\s+saving throw\\b)"    // look-ahead for " saving throw"
    );

    public String getEnricher() {
        return "[[/check " + fullName.toLowerCase() + "]]";
    }

    public String getEnricher(int dc) {
        return "[[/check ability=" + fullName.toLowerCase() + " dc=" + dc + "]]";
    }
}
