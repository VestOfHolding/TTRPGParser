package org.voh.domain.dnd5e;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Skills5e {
    ACRO("Acrobatics", "acrobatics", "acr"),
    ANIMAL("Animal Handling", "animalHandling", "ani"),
    ARCANA("Arcana", "arcana", "arc"),
    ATHLETICS("Athletics", "athletics", "ath"),
    DEC("Deception", "deception", "dec"),
    HIST("History", "history", "his"),
    INSIGHT("Insight", "insight", "ins"),
    INTIMIDATION("Intimidation", "intimidation", "itm"),
    INVESTIGATION("Investigation", "investigation", "inv"),
    MEDICINE("Medicine", "medicine", "med"),
    NATURE("Nature", "nature", "nat"),
    PERCEPTION("Perception", "perception", "prc"),
    PERFORM("Performance", "performance", "prf"),
    PERSUASION("Persuasion", "persuasion", "per"),
    RELIGION("Religion", "religion", "rel"),
    SLEIGHT("Sleight of Hand", "sleightOfHand", "slt"),
    STEALTH("Stealth", "stealth", "ste"),
    SURVIVAL("Survival", "survival", "sur");

    private final String displayName;
    private final String fullID;
    private final String shortID;

    public String getEnricher() {
        return "[[/skill " + fullID + "]]";
    }

    public String getEnricher(int dc) {
        return "[[/skill skill=" + fullID + " dc=" + dc + "]]";
    }

    public static final String NAME_REGEX = Arrays.stream(values())
            .map(a -> a.displayName)
            .collect(Collectors.joining("|"));

    public static final Pattern SKILL_CHECK = Pattern.compile(
            "(?i)\\b(" +       // group(1)=optional leading verb+article (e.g. “make a”, “Attempt a”, etc.)
                    "(?:succeed(?:s|ing)?\\s+on" +
                    "|make(?:s|ing)?" +
                    "|attempt(?:s)?(?:\\s+a)?" +
                    "|a\\ssuccessful)\\s+)?" +
                    "(?:DC\\s*(\\d{1,3})\\s+)?" + // group(2)=DC if present
                    "(" + Abilities5e.NAME_REGEX + ")" +  // group(3)=Ability name
                    "\\s*\\((" +
                    NAME_REGEX +    // group(4)=Skill name
                    ")\\)(?=\\s+check\\b)"  // look-ahead for “ check”
    );

    public static final Map<String, Skills5e> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(
                    s -> s.displayName.toLowerCase(),
                    s -> s));
}
