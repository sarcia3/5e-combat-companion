package org.tcs.model;

import org.tcs.model.dice.DiceRoller;

/** Test-only factories for {@link Creature}, exposing its package-private constructor. */
public final class CreatureFixtures {
  private CreatureFixtures() {}

  public static Creature creature(
      String name, int hitPointMaximum, int proficiencyBonus, DiceRoller diceRoller) {
    return new Creature(name, hitPointMaximum, 30, proficiencyBonus, diceRoller);
  }

  /** Variant whose {@link Creature#armorClass()} returns a custom value. */
  public static Creature creatureWithArmorClass(
      String name,
      int hitPointMaximum,
      int proficiencyBonus,
      int armorClass,
      DiceRoller diceRoller) {
    return new Creature(name, hitPointMaximum, 30, proficiencyBonus, diceRoller) {
      @Override
      public int armorClass() {
        return armorClass;
      }
    };
  }
}
