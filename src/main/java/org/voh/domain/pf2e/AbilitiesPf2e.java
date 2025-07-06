package org.voh.domain.pf2e;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum AbilitiesPf2e {
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
}
