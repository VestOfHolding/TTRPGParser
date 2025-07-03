package org.voh.domain.dnd5e;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Conditions5e {
    BLINDED,
    CHARMED,
    DEAFENED,
    FRIGHTENED,
    GRAPPLED,
    INCAPACITATED,
    INVISIBLE,
    PARALYZED,
    PETRIFIED,
    POISONED,
    PRONE,
    RESTRAINED,
    STUNNED,
    UNCONSCIOUS;

    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public String getEnricher() {
        return "&Reference[" + getDisplayName() + "]";
    }

    private static final String NAME_REGEX = Arrays.stream(values())
            .map(Conditions5e::getDisplayName)
            .collect(Collectors.joining("|"));

    public static final Pattern PATTERN =
            Pattern.compile("(?i)\\b(" + NAME_REGEX + ")\\b");
}
