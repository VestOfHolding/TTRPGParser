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

    //–– Duration Enricher Tests ––––––––––––––––––––––––––––––––––––––––––––––––––

    @Test
    void testEnrichDurationRounds() {
        assertEquals(
                "The poison lasts [[/r 1d4 #rounds]]{1d4 rounds}.",
                Dnd5ePostProcessor.enrichDuration("The poison lasts 1d4 rounds.")
        );
    }

    @Test
    void testEnrichDurationMinutes() {
        assertEquals(
                "Effect lasts [[/r 2d6 #minutes]]{2d6 minutes}.",
                Dnd5ePostProcessor.enrichDuration("Effect lasts 2d6 minutes.")
        );
    }

    @Test
    void testEnrichDurationHours() {
        assertEquals(
                "Stunned for [[/r 3d8 #hours]]{3d8 hours}.",
                Dnd5ePostProcessor.enrichDuration("Stunned for 3d8 hours.")
        );
    }

    @Test
    void testEnrichDurationDays() {
        assertEquals(
                "Frozen for [[/r 1d10 #days]]{1d10 days}.",
                Dnd5ePostProcessor.enrichDuration("Frozen for 1d10 days.")
        );
    }

    //–– Attack  Enricher Tests ––––––––––––––––––––––––––––––––––––––––––––––––––

    @Test
    void testEnrichAttackDiceWithType() {
        assertEquals(
                "Deals [[/damage 2d6 fire]].",
                Dnd5ePostProcessor.enrichAttacks("Deals 2d6 fire damage.")
        );
    }

    @Test
    void testEnrichAttackDiceWithBonusAndType() {
        assertEquals(
                "Deals [[/damage 2d6+3 fire]].",
                Dnd5ePostProcessor.enrichAttacks("Deals 2d6+3 fire damage.")
        );
    }

    @Test
    void testEnrichAttackFlatDamage() {
        assertEquals(
                "Takes [[/damage 5 acid]].",
                Dnd5ePostProcessor.enrichAttacks("Takes 5 acid damage.")
        );
    }

    @Test
    void testEnrichAttackDiceWithTypeAndComma() {
        assertEquals(
                "Hit for [[/damage 1d8 cold]], and runs.",
                Dnd5ePostProcessor.enrichAttacks("Hit for 1d8 cold, and runs.")
        );
    }

    @Test
    void testEnrichAttackDiceOnly() {
        assertEquals(
                "Hit with [[/damage 2d6]].",
                Dnd5ePostProcessor.enrichAttacks("Hit with 2d6 damage.")
        );
    }

    @Test
    void testEnrichAttackDiceWithTypeAndPeriod() {
        assertEquals(
                "Another [[/damage 2d6 cold]].",
                Dnd5ePostProcessor.enrichAttacks("Another 2d6 cold.")
        );
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