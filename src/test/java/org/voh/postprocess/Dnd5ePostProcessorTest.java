package org.voh.postprocess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Dnd5ePostProcessorTest {

    //–– Saving Throw Enricher Tests –––––––––––––––––––––––––––––––––––––––––––––––––

    @Test
    @DisplayName("Saving Throw → no DC")
    void testEnrichSavingThrows_NoDC() {
        String in  = "You must make a Wisdom saving throw.";
        String out = Dnd5ePostProcessor.enrichSavingThrows(in);
        assertEquals(
                "You must make a [[/check wisdom]] saving throw.",
                out
        );
    }

    @Test
    @DisplayName("Saving Throw → with DC")
    void testEnrichSavingThrows_WithDC() {
        String in  = "Make a DC 14 Dexterity saving throw now!";
        String out = Dnd5ePostProcessor.enrichSavingThrows(in);
        assertEquals(
                "Make a [[/check ability=dexterity dc=14]] saving throw now!",
                out
        );
    }

    @Test
    @DisplayName("Saving Throw → mixed case and trailing punctuation")
    void testEnrichSavingThrows_MixedCase() {
        String in  = "sUcCeEd On An Intelligence saving throw?";
        String out = Dnd5ePostProcessor.enrichSavingThrows(in);
        assertEquals(
                "sUcCeEd On An [[/check intelligence]] saving throw?",
                out
        );
    }

    //–– Condition Enricher Tests ––––––––––––––––––––––––––––––––––––––––––––––––––

    @Test
    @DisplayName("Condition Enricher → single, mixed-case")
    void testEnrichConditions_Single() {
        String in  = "You are Blinded.";
        String out = Dnd5ePostProcessor.enrichConditions(in);
        assertEquals(
                "You are &Reference[Blinded].",
                out
        );
    }

    @Test
    @DisplayName("Condition Enricher → multiple, case-insensitive")
    void testEnrichConditions_Multiple() {
        String in  = "You are charmed and PRONE, then deafened.";
        String out = Dnd5ePostProcessor.enrichConditions(in);
        assertEquals(
                "You are &Reference[Charmed] and &Reference[Prone], then &Reference[Deafened].",
                out
        );
    }

    @Test
    @DisplayName("Condition Enricher → word-boundary safety")
    void testEnrichConditions_NoPartialMatch() {
        String in  = "The blindedness is a peculiar state.";
        String out = Dnd5ePostProcessor.enrichConditions(in);
        // "blindedness" should NOT match "Blinded"
        assertEquals(in, out);
    }

    //–– Skill-Check Enricher Tests ––––––––––––––––––––––––––––––––––––––––––––––––

    @Test
    @DisplayName("Skill Check → no DC, basic verb")
    void testEnrichSkillChecks_NoDC() {
        String in  = "Make a Charisma (Performance) check.";
        String out = Dnd5ePostProcessor.enrichSkillChecks(in);
        assertEquals(
                "Make a [[/skill performance]] check.",
                out
        );
    }

    @Test
    @DisplayName("Skill Check → with DC and varied verbs/punctuation")
    void testEnrichSkillChecks_WithDC_Variety() {
        String[] inputs = {
                "succeed on a DC 21 Intelligence (Investigation) check!",
                "Attempt a Charisma (Performance) check?",
                "a successful DC 22 Wisdom (Medicine) check."
        };
        String[] expecteds = {
                "succeed on a [[/skill skill=investigation dc=21]] check!",
                "Attempt a [[/skill performance]] check?",
                "a successful [[/skill skill=medicine dc=22]] check."
        };
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(expecteds[i],
                    Dnd5ePostProcessor.enrichSkillChecks(inputs[i]),
                    "Failed on: " + inputs[i]);
        }
    }

    @Test
    @DisplayName("Skill Check → fallback if skill unknown")
    void testEnrichSkillChecks_UnknownSkill() {
        String in  = "make a Charisma (FooBar) check.";
        String out = Dnd5ePostProcessor.enrichSkillChecks(in);
        // should leave it untouched
        assertEquals(in, out);
    }

    //–– Full Pipeline via postProcess(…) –––––––––––––––––––––––––––––––––––––––––

    @Test
    @DisplayName("Full postProcess: mixed content")
    void testPostProcess_Combined() {
        String in = """
            While you are Blinded, you must succeed on a DC 15 Strength saving throw
            before you can make a Dexterity (Stealth) check.
            """.replace("\n", " ");
        String out = Dnd5ePostProcessor.postProcess(in);
        String expected = """
            While you are &Reference[Blinded], you must succeed on a [[/check ability=strength dc=15]] saving throw
            before you can make a [[/skill stealth]] check.
            """.replace("\n", " ");
        assertEquals(expected, out);
    }
}