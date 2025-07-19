package org.voh.domain.dnd5e;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Feat5e {
    private String name;
    private String prerequisites;
    private String body;
}
