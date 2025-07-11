package org.voh.domain.pf2e;

import java.util.regex.Pattern;

public class SavesPf2e {

    public static final Pattern BASIC = Pattern.compile(
            "(?i)\\b(?:" +
                    "DC\\s+(\\d+)\\s+basic\\s+(\\w+)\\s+save|" + // group(1)=DC, group(2)=type
                    "basic\\s+(\\w+)\\s+save\\s+DC\\s+(\\d+)|" + // group(3)=type, group(4)=DC
                    "basic\\s+(\\w+)\\s+DC\\s+(\\d+)"          + // group(5)=type, group(6)=DC
                    ")\\b"
    );

    // matches “DC 8 Reflex” or “Reflex DC 8”
    public static final Pattern SIMPLE_SAVE = Pattern.compile(
            "(?i)(?<=^|\\s)" +
                    "(?:DC\\s*(\\d{1,3})\\s+)?"+    // (1) optional “DC N”
                    "(Fortitude|Reflex|Will)" +     // (2) group=Type if DC came first
                    "(?:\\s+DC\\s*(\\d{1,3}))?" +    // (3) optional “DC N” after Type
                    "\\b"
    );

    // matches “Reflex (DC 5)” or “Reflex save (DC 5)”
    public static final Pattern PARENS_SAVE = Pattern.compile(
            "(?ix)\\b" +
                    "(Fortitude|Reflex|Will)" + // group(1)=the save type
                    "(?:\\s+save)?\\s*" +       // optional “ save”
                    "\\(DC\\s*(\\d{1,3})\\)"    // “(DC N)”, group(2)=the number
    );
}
