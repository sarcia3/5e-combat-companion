package org.tcs.model.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tcs.model.CreatureFixtures.creature;
import static org.tcs.model.CreatureFixtures.creatureWithArmorClass;
import static org.tcs.model.equipment.WeaponFixtures.longsword;

import java.util.ArrayDeque;
import java.util.Queue;
import org.junit.jupiter.api.Test;
import org.tcs.model.Creature;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.equipment.Weapon;

class MeleeWeaponAttackTest {

  /** A DiceRoller that returns a predetermined sequence of values, ignoring requested dice size. */
  private static final class QueuedDiceRoller implements DiceRoller {
    private final Queue<Integer> values;

    QueuedDiceRoller(int... rolls) {
      this.values = new ArrayDeque<>();
      for (int r : rolls) values.add(r);
    }

    @Override
    public int roll(int numberOfSides, RollInformation information) {
      if (numberOfSides < 1) throw new IllegalArgumentException();
      return values.poll();
    }
  }

  @Test
  void hitReducesTargetHitPointsByRolledDamage() {
    // attacker rolls 15 on d20, then 5 on d8 for damage
    Creature attacker = creature("Fighter", 20, 2, new QueuedDiceRoller(15, 5));
    Creature target = creature("Goblin", 10, 2, new QueuedDiceRoller());
    Weapon weapon = longsword();
    attacker.inventory().addStoredWeapon(weapon);
    WeaponAttack attack = weapon.generateAttacks(attacker).stream().findAny().get();

    attack.resolve(null, target);

    assertEquals(5, target.hitPoints()); // 10 - 5
  }

  @Test
  void naturalOneMissesEvenWithHighModifier() {
    // d20 = 1, no damage roll consumed because miss returns early
    Creature attacker = creature("Fighter", 20, 2, new QueuedDiceRoller(1));
    Creature target = creature("Goblin", 10, 2, new QueuedDiceRoller());
    Weapon weapon = longsword();
    attacker.inventory().addStoredWeapon(weapon);
    WeaponAttack attack = weapon.generateAttacks(attacker).stream().findAny().get();

    attack.resolve(null, target);

    assertEquals(10, target.hitPoints());
  }

  @Test
  void rollBelowArmorClassMisses() {
    // attacker has STR 10 (mod 0), target AC is 10; d20 = 5 → total 5, misses
    Creature attacker = creature("Fighter", 20, 2, new QueuedDiceRoller(5));
    Creature target = creature("Goblin", 10, 2, new QueuedDiceRoller());
    Weapon weapon = longsword();
    attacker.inventory().addStoredWeapon(weapon);
    WeaponAttack attack = weapon.generateAttacks(attacker).stream().findAny().get();

    attack.resolve(null, target);

    assertEquals(10, target.hitPoints());
  }

  @Test
  void naturalTwentyHitsEvenWhenTotalIsBelowArmorClass() {
    // attacker has STR 10 (mod 0), target AC is 25; d20 = 20 → total 20 < 25
    // but nat 20 is an automatic hit; then rolls 6 damage
    Creature attacker = creature("Fighter", 20, 2, new QueuedDiceRoller(20, 6));
    Creature target = creatureWithArmorClass("Stone Golem", 30, 2, 25, new QueuedDiceRoller());
    Weapon weapon = longsword();
    attacker.inventory().addStoredWeapon(weapon);
    WeaponAttack attack = weapon.generateAttacks(attacker).stream().findAny().get();
    attack.resolve(null, target);

    assertEquals(24, target.hitPoints()); // 30 - 6
  }
}
