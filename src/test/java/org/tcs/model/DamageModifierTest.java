// LLMed
package org.tcs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import org.tcs.model.dice.DiceRoller;

/** Resistance / vulnerability / immunity handling in {@link Creature#takeDamage}. */
public class DamageModifierTest {
  // takeDamage only rolls dice on the death-saving path; with full HP it is never invoked.
  private static final DiceRoller NO_ROLL =
      new DiceRoller() {
        @Override
        public int roll(int numberOfSides, RollInformation information) {
          throw new UnsupportedOperationException();
        }
      };

  /** Healthy target; tests add the modifier sets they need via the Builder. */
  private static Creature.Builder builder() {
    return new Creature.Builder()
        .name("Target")
        .hitPointMaximum(100)
        .diceRoller(NO_ROLL);
  }

  private static Damage damage(Damage.Type type, int amount) {
    Damage damage = new Damage();
    damage.add(type, amount);
    return damage;
  }

  @Test
  public void noModifiersTakesFullDamage() {
    Creature creature = builder().build();
    creature.takeDamage(damage(Damage.Type.FIRE, 7));
    assertEquals(100 - 7, creature.hitPoints);
  }

  @Test
  public void resistanceHalvesDamageRoundingDown() {
    Creature creature = builder().resistances(EnumSet.of(Damage.Type.FIRE)).build();
    creature.takeDamage(damage(Damage.Type.FIRE, 7)); // 7 / 2 == 3, not 4
    assertEquals(100 - 3, creature.hitPoints);
  }

  @Test
  public void vulnerabilityDoublesDamage() {
    Creature creature = builder().vulnerability(EnumSet.of(Damage.Type.FIRE)).build();
    creature.takeDamage(damage(Damage.Type.FIRE, 7));
    assertEquals(100 - 14, creature.hitPoints);
  }

  @Test
  public void immunityNegatesDamage() {
    Creature creature = builder().immunities(EnumSet.of(Damage.Type.FIRE)).build();
    creature.takeDamage(damage(Damage.Type.FIRE, 7));
    assertEquals(100, creature.hitPoints);
  }

  /** Resistance and vulnerability on the same type cancel: full damage, with no rounding loss. */
  @Test
  public void resistanceAndVulnerabilityCancelOut() {
    Creature creature =
        builder()
            .resistances(EnumSet.of(Damage.Type.FIRE))
            .vulnerability(EnumSet.of(Damage.Type.FIRE))
            .build();
    // Odd amount proves the order matters: double-then-halve (7*2/2 == 7),
    // not halve-then-double (7/2*2 == 6).
    creature.takeDamage(damage(Damage.Type.FIRE, 7));
    assertEquals(100 - 7, creature.hitPoints);
  }

  @Test
  public void immunityOverridesVulnerability() {
    Creature creature =
        builder()
            .immunities(EnumSet.of(Damage.Type.FIRE))
            .vulnerability(EnumSet.of(Damage.Type.FIRE))
            .build();
    creature.takeDamage(damage(Damage.Type.FIRE, 7));
    assertEquals(100, creature.hitPoints);
  }

  @Test
  public void modifiersApplyPerType() {
    Creature creature =
        builder()
            .resistances(EnumSet.of(Damage.Type.FIRE))
            .vulnerability(EnumSet.of(Damage.Type.COLD))
            .build();
    Damage mixed = new Damage();
    mixed.add(Damage.Type.FIRE, 8); // resisted -> 4
    mixed.add(Damage.Type.COLD, 5); // vulnerable -> 10
    mixed.add(Damage.Type.POISON, 3); // unmodified -> 3
    creature.takeDamage(mixed);
    assertEquals(100 - (4 + 10 + 3), creature.hitPoints);
  }
}
