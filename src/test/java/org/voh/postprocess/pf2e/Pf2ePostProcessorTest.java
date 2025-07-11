package org.voh.postprocess.pf2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Pf2ePostProcessorTest {

    @Nested
    class BasicSaves {
        @Test
        @DisplayName("DC before basic")
        void basic_DcBeforeBasic() {
            String in  = "DC 14 basic Fortitude save";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:fortitude|dc:14|basic] save", out);
        }

        @Test
        @DisplayName("Basic save then DC")
        void basic_BasicSaveThenDc() {
            String in  = "basic Reflex save DC 15";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:reflex|dc:15|basic] save", out);
        }

        @Test
        @DisplayName("Basic save then DC 2")
        void basic_BasicAbilityThenDc() {
            String in  = "basic Will DC 12";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:will|dc:12|basic] save", out);
        }
    }

    @Nested
    class SimpleSaves {
        @Test
        @DisplayName("DC N type")
        void simple_DcType() {
            String in  = "DC 8 Reflex";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:reflex|dc:8]", out);
        }

        @Test
        @DisplayName("type DC N")
        void simple_TypeDc() {
            String in  = "Will DC 9";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:will|dc:9]", out);
        }
    }

    @Nested
    class ParenSaves {
        @Test
        @DisplayName("type (DC N)")
        void parens_WithoutSaveWord() {
            String in  = "Reflex (DC 5)";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:reflex|dc:5] save", out);
        }

        @Test
        @DisplayName("type save (DC N)")
        void parens_WithSaveWord() {
            String in  = "Fortitude save (DC 6)";
            String out = Pf2ePostProcessor.enrichSaves(in);
            assertEquals("@Check[type:fortitude|dc:6] save", out);
        }
    }

    @Test
    @DisplayName("Mixed‐case basic")
    void mixedCase_Basic() {
        String in  = "dc 20 BASIC will SAVE";
        String out = Pf2ePostProcessor.enrichSaves(in);
        assertEquals("@Check[type:will|dc:20|basic] save", out);
    }

    @Test
    @DisplayName("No‐match remains unchanged")
    void noMatch_Unchanged() {
        String in  = "This text has no saves.";
        String out = Pf2ePostProcessor.enrichSaves(in);
        assertEquals(in, out);
    }

    @Test
    @DisplayName("Multiple occurrences")
    void multiple_Occurrences() {
        String in  = "DC 8 Reflex and Reflex DC 9";
        String out = Pf2ePostProcessor.enrichSaves(in);
        assertEquals("@Check[type:reflex|dc:8] and @Check[type:reflex|dc:9]", out);
    }
}