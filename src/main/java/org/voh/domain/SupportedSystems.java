package org.voh.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum SupportedSystems {
    GENERAL("General"),
    DND_5E("D&D 5e"),
    PF_2E("Pathfinder 2e");

    @Getter
    private final String displayName;

    public static SupportedSystems fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(s -> s.displayName.equals(displayName))
                .findFirst().orElse(null);
    }

    public static List<String> displayNames() {
        return Arrays.stream(values()).map(s -> s.displayName).toList();
    }

    public static String defaultDisplayName() {
        return GENERAL.displayName;
    }
}
