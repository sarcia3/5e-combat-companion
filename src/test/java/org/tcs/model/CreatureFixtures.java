package org.tcs.model;

import org.tcs.model.dice.DiceRoller;

/** Test-only factories for {@link Creature}, exposing its package-private constructor. */
public final class CreatureFixtures {
  private CreatureFixtures() {}

  public static Creature creature(
      String name, int hitPointMaximum, int proficiencyBonus, DiceRoller diceRoller) {
    return new Creature.Builder()
        .name(name)
        .hitPointMaximum(hitPointMaximum)
        .movementSpeed(30)
        .proficiencyBonus(proficiencyBonus)
        .diceRoller(diceRoller)
        .build();
  }

  /** Variant whose {@link Creature#armorClass()} returns a custom value. */
  public static Creature creatureWithArmorClass(
      String name,
      int hitPointMaximum,
      int proficiencyBonus,
      int armorClass,
      DiceRoller diceRoller) {
    return new Creature.Builder()
        .name(name)
        .hitPointMaximum(hitPointMaximum)
        .movementSpeed(30)
        .proficiencyBonus(proficiencyBonus)
        .diceRoller(diceRoller)
        .overrideArmorClass(armorClass)
        .build();
  }
}
