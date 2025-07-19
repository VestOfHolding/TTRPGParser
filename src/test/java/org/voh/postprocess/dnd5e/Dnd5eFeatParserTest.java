package org.voh.postprocess.dnd5e;

import org.junit.jupiter.api.Test;
import org.voh.domain.dnd5e.Feat5e;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Dnd5eFeatParserTest {
    private static final String INPUT = """
            <h2>Anoint Temporary Sanctuary</h2><p>
            <b><i>Prerequisites:</i></b> <i>5th level, allure draconic ancestry, Dragon Mage </i>
            </p>
            <p><i>archetype</i> As an action, you can form a temporary bond with a region of the wilderness, which becomes your sanctuary for 24 hours or until you anoint another sanctuary. The sanctuary is a 10-foot-radius area centered on yourself when you anoint the sanctuary. All creatures other than you and creatures you designate when you create the sanctuary treat the area as difficult terrain. In addition, plants within the area intervene to shield you from harm, causing creatures to have disadvantage on attack rolls against you and the designated creatures while you or the designated creature are inside the sanctuary.
            </p>
            <p>Once you use this gift, you must finish a long rest before you can do so again.
            </p>
            <h2>Antipodal Duality</h2><p>
            <b><i>Prerequisites:</i></b> <i>5th level, Draconic Ravager or Dragon Mage </i>
            </p>
            <p><i>archetype</i> While most dragons focus their magic toward either combat techniques or advanced spellcasting, the unique way you’ve progressed after the lux aeterna ritual makes you especially suited to</p>
            <p>learn it all. If your archetype is Draconic Ravager, you gain proficiency in your choice of Arcana, Nature, or Religion. If your archetype is Dragon Mage, you gain proficiency in your choice of Acrobatics, Athletics, or Intimidation. In addition, you are considered both a Draconic Ravager and a Dragon Mage for the purposes of meeting the prerequisites of draconic gifts.
            </p>
            <h2>Bite of Opportunity</h2><p>
            <b><i>Prerequisites:</i></b> <i>5th level, Draconic Ravager archetype</i> When you hit a creature with an opportunity attack using your bite attack, the creature’s speed is reduced to 0 for the rest of the turn.
            </p>
            <h2>Caustic Breakdown</h2><p>
            <b><i>Prerequisites: </i></b><i>5th level, mischief draconic ancestry</i> The acid in your jaws breaks down hard materials with ease. When you hit an object or construct with your bite attack, you deal the target additional acid damage equal to your proficiency bonus (or twice your proficiency bonus on a critical hit if you also have the Caustic Corrosion gift).
            </p>
            <h2>Conductive Scales</h2><p>
            <b><i>Prerequisites:</i></b> <i>5th level, Dragon Mage archetype, Dragon Scales </i>
            </p>
            <p><i>evolution</i> When you cast spells, magical power surges through your scales,
            </p>
            <p>briefly granting you increased protection. Whenever you cast a dragon mage spell of 1st-level or higher, you also
            </p>
            <p>gain a bonus to your AC equal to half the spell’s level (rounded down, minimum 1) until the start
            </p>
            <p>of your next turn.
            </p>
            <h2>Deep Breath</h2><p>
            <b><i>Prerequisites:</i></b> <i>5th level, Breath Weapon</i>
            </p>
            <p>You can breathe in deep to deliver a more powerful breath. When you use your breath weapon, you can choose
            </p>
            <p>to take a deep breath. If you do, your breath weapon deals two additional dice
            </p>
            <p>of damage. In addition, the area of your breath weapon increases to a 60-foot line if it was a
            </p>
            <p>30-foot line, a 30-foot cone if it was a 15-foot cone, or a 10-foot burst within 60 feet if it was a
            </p>
            <p>5-foot burst within 30 feet. Once you’ve used Deep Breath, you must finish a long rest before you can do so
            </p>
            <p>again.
            </p>
            <h2>Defensive Flutter</h2><p>
            <b><i>Prerequisites:</i></b> <i>5th level, pixie draconic ancestry, Draconic Ravager </i>
            </p>
            <p><i>archetype</i> When a creature you can see misses you with a melee attack, you can use your reaction to flutter your wings, showering the triggering creature with a cloud of sparkling pixie dust. The creature has disadvantage on attack rolls until the end of its turn. The creature can end the effect early by moving at least 10 feet away from its current location.</p>
            """;

    @Test
    public void testFearParser() {
        List<Feat5e> results = Dnd5eFeatParser.parseFeats(INPUT);

        assertEquals(7, results.size());

        assertEquals("Anoint Temporary Sanctuary", results.getFirst().getName());
        assertEquals("5th level, allure draconic ancestry, Dragon Mage archetype", results.getFirst().getPrerequisites());
        assertEquals("<p>As an action, you can form a temporary bond with a region of the wilderness, which becomes your sanctuary for 24 hours or until you anoint another sanctuary. The sanctuary is a 10-foot-radius area centered on yourself when you anoint the sanctuary. All creatures other than you and creatures you designate when you create the sanctuary treat the area as difficult terrain. In addition, plants within the area intervene to shield you from harm, causing creatures to have disadvantage on attack rolls against you and the designated creatures while you or the designated creature are inside the sanctuary." +
                "</p><p>Once you use this gift, you must finish a long rest before you can do so again.</p>", results.getFirst().getBody());

        assertEquals("Defensive Flutter", results.getLast().getName());
        assertEquals("5th level, pixie draconic ancestry, Draconic Ravager archetype", results.getLast().getPrerequisites());
        assertEquals("<p>When a creature you can see misses you with a melee attack, you can use your reaction to " +
                "flutter your wings, showering the triggering creature with a cloud of sparkling pixie dust. The creature " +
                "has disadvantage on attack rolls until the end of its turn. The creature can end the effect early by moving " +
                "at least 10 feet away from its current location.</p>", results.getLast().getBody());
    }
}