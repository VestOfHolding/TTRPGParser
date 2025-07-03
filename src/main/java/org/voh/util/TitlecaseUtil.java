package org.voh.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class TitlecaseUtil {
    // Words that every major style guide treats as "minor"
    private static final Set<String> MINOR_SHORT = Set.of(
            "a","an","the",
            "and","but","or","for","nor",
            "as","at","by","in","of","on","to","off","out","up"
    );

    // All common prepositions
    private static final Set<String> PREPOSITIONS = Set.of(
            "about","above","across","after","against","along","among","around",
            "as","at","before","behind","below","beneath","beside","between","beyond",
            "but","by","despite","down","during","except","for","from","in","inside",
            "into","like","near","of","off","on","onto","out","outside","over","past",
            "since","through","throughout","to","toward","under","until","up","upon",
            "with","within","without"
    );

    /**
     * Returns true if the line is title case under AP, Chicago, MLA, or APA (OR logic).
     */
    public static boolean isTitleCase(String line) {
        if (StringUtils.isBlank(line))
            return false;

        String[] tokens = line.trim().split("\\s+");

        for (int i = 0; i < tokens.length; i++) {
            String raw = tokens[i];
            // strip leading/trailing punctuation
            String word = raw.replaceAll("^[^\\p{L}]+|[^\\p{L}]+$", "");
            if (word.isEmpty())
                continue;

            if (i == 0 || i == tokens.length - 1) {
                if (!isCapitalized(word)) {
                    return false;
                }
            } else {
                if (!isAllowedLowercase(word) && !isCapitalized(word))
                    return false;
            }
        }
        return true;
    }

    private static boolean isAllowedLowercase(String word) {
        String lower = word.toLowerCase();
        boolean minorShort = (word.length() <= 3 && MINOR_SHORT.contains(lower));
        boolean longPrep = (word.length() > 3  && PREPOSITIONS.contains(lower));

        return minorShort || longPrep;
    }

    // Checks if a word is Title-Case: first letter uppercase, rest lowercase
    private static boolean isCapitalized(String word) {
        if (StringUtils.isBlank(word) || !Character.isUpperCase(word.charAt(0))) return false;
        for (int i = 1; i < word.length(); i++) {
            if (!Character.isLowerCase(word.charAt(i))) return false;
        }
        return true;
    }
}
