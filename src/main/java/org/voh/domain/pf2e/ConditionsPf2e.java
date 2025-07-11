package org.voh.domain.pf2e;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ConditionsPf2e {
    BLINDED     ("Blinded",     "XgEqL1kFApUbl5Z2"),
    CLUMSY      ("Clumsy",      "i3OJZU2nk64Df3xm",    true),
    CONCEALED   ("Concealed",   "DmAIPqOBomZ7H95W"),
    CONFUSED    ("Confused",    "yblD8fOR1J8rDwEQ"),
    CONTROLLED  ("Controlled",  "9qGBRpbX9NEwtAAr"),
    DAZZLED     ("Dazzled",     "TkIyaNPgTZFBCCuh"),
    DEAFENED    ("Deafened",    "9PR9y0bi4JPKnHPR"),
    DOOMED      ("Doomed",      "3uh1r86TzbQvosxv",   true),
    DRAINED     ("Drained",     "4D2KBtexWXa6oUMR",   true),
    DYING       ("Dying",       "yZRUzMqrMmfLu0V1",   true),
    ENCUMBERED  ("Encumbered",  "D5mg6Tc7Jzrj6ro7"),
    ENFEEBLED   ("Enfeebled",   "MIRkyAjyBeXivMa7",   true),
    FASCINATED  ("Fascinated",  "AdPVz7rbaVSRxHFg"),
    FATIGUED    ("Fatigued",    "HL2l2VRSaQHu9lUw"),
    FLEEING     ("Fleeing",     "sDPxOjQ9kx2RZE8D"),
    FRIGHTENED  ("Frightened",  "TBSHQspnbcqxsmjL",   true),
    GRABBED     ("Grabbed",     "kWc1fhmv9LBiTuei"),
    HIDDEN      ("Hidden",      "iU0fEDdBp3rXpTMC"),
    IMMOBILIZED ("Immobilized", "eIcWbB5o3pP6OIMe"),
    INVISIBLE   ("Invisible",   "zJxUflt9np0q4yML"),
    OBSERVED    ("Observed",    "1wQY3JYyhMYeeV2G"),
    OFF_GUARD   ("Off-Guard",   "AJh5ex99aV6VTggg"),
    PARALYZED   ("Paralyzed",   "6uEgoh53GbXuHpTF"),
    PETRIFIED   ("Petrified",   "dTwPJuKgBQCMxixg"),
    PRONE       ("Prone",       "j91X7x0XSomq8d60"),
    QUICKENED   ("Quickened",   "nlCjDvLMf2EkV2dl"),
    RESTRAINED  ("Restrained",  "VcDeM8A5oI6VqhbM"),
    SICKENED    ("Sickened",    "fesd1n5eVhpCSS18",   true),
    SLOWED      ("Slowed",      "xYTAsEpcJE1Ccni3",   true),
    STUNNED     ("Stunned",     "dfCMdR4wnpbYNTix",   true),
    STUPEFIED   ("Stupefied",   "e1XGnhKNSQIm5IXg",   true),
    UNCONSCIOUS ("Unconscious", "fBnFDH2MTzgFijKf"),
    UNDETECTED  ("Undetected",  "VRSef5y1LmL2Hkjf"),
    WOUNDED     ("Wounded",     "Yl48xTdMh3aeQYL2");

    private final String displayName;
    private final String uuid;
    private final boolean hasValue;

    // two-arg constructor defaults hasValue to false
    ConditionsPf2e(String displayName, String uuid) {
        this(displayName, uuid, false);
    }

    public String getEnricher() {
        return "@UUID[Compendium.pf2e.conditionitems.Item." + uuid + "]{" + displayName + "}";
    }

    public String getEnricher(String value) {
        return "@UUID[Compendium.pf2e.conditionitems.Item." + uuid + "]{" + displayName + " " + value + "}";
    }

    // build a regex alternation of all display names (escaped)
    public static final String NAME_REGEX = Arrays.stream(values())
            .map(c -> Pattern.quote(c.displayName))
            .collect(Collectors.joining("|"));

    public static final Pattern PATTERN =
            Pattern.compile("(?i)\\b(" + NAME_REGEX + ")(?: (\\d))?\\b");

    public static final Map<String, ConditionsPf2e> BY_NAME =
            Stream.of(ConditionsPf2e.values())
                    .collect(Collectors.toMap(
                            c -> c.getDisplayName().toLowerCase(),
                            c -> c
                    ));
}

