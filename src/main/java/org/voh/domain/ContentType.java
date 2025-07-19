package org.voh.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ContentType {
    GENERAL("General"),
    FEAT("Feats"),
    NPC("NPCs");

    private final String displayName;

    public static List<String> displayNames() {
        return Arrays.stream(values()).map(s -> s.displayName).toList();
    }

    public static String defaultDisplayName() {
        return GENERAL.displayName;
    }
}
