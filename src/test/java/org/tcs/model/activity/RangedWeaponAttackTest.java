package org.tcs.model.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.tcs.model.CreatureFixtures.creature;

import org.junit.jupiter.api.Test;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.dice.QueuedDiceRoller;
import org.tcs.model.dice.RollMode;
import org.tcs.model.equipment.Ammunition;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.equipment.WeaponRange;

class RangedWeaponAttackTest {

  private static Weapon shortbow() {
    return new Weapon(
        "Shortbow",
        new Weapon.Mode(
            "1d6 PIERCING", Ability.DEX, true, Ammunition.ARROW, new WeaponRange(80, 320)));
  }

  private static Weapon dart() {
    return new Weapon("Dart", "1d4 PIERCING", Ability.DEX, true, new WeaponRange(20, 60));
  }

  @Test
  void ammunitionIsConsumedOnAttack() {
    // d20 = 15 hits AC 10; then 4 on the damage die
    Creature attacker = creature("Archer", 20, 2, new QueuedDiceRoller(15, 4));
    Creature target = creature("Goblin", 10, 2, new QueuedDiceRoller());
    Weapon bow = shortbow();
    attacker.inventory().addStoredWeapon(bow);
    attacker.inventory().addAmmunition(Ammunition.ARROW, 1);
    WeaponAttack attack = bow.generateAttacks(attacker).stream().findAny().get();

    attack.resolve(null, target, RollMode.NORMAL);

    assertFalse(attacker.inventory().hasAmmunition(Ammunition.ARROW)); // the only arrow was spent
    assertEquals(6, target.hitPoints()); // hit: 10 - 4
  }

  @Test
  void attackWithoutAmmunitionThrows() {
    Creature attacker = creature("Archer", 20, 2, new QueuedDiceRoller(15, 4));
    Creature target = creature("Goblin", 10, 2, new QueuedDiceRoller());
    Weapon bow = shortbow();
    attacker.inventory().addStoredWeapon(bow);
    WeaponAttack attack = bow.generateAttacks(attacker).stream().findAny().get();

    assertThrows(IllegalStateException.class, () -> attack.resolve(null, target, RollMode.NORMAL));
  }

  @Test
  void thrownWeaponIsRemovedFromInventory() {
    Creature attacker = creature("Thrower", 20, 2, new QueuedDiceRoller(15, 3));
    Creature target = creature("Goblin", 10, 2, new QueuedDiceRoller());
    Weapon dart = dart();
    attacker.inventory().addStoredWeapon(dart);
    attacker.inventory().equipWeapon(dart);
    WeaponAttack attack = dart.generateAttacks(attacker).stream().findAny().get();

    attack.resolve(null, target, RollMode.NORMAL);

    assertFalse(attacker.inventory().getEquippedWeapons().contains(dart));
    assertFalse(attacker.inventory().getStoredWeapons().contains(dart));
  }
}
