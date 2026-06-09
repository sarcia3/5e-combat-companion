package org.tcs.model.activity;

import org.tcs.model.*;
import org.tcs.model.dice.DamageRoll;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.equipment.Weapon;

/**
 * General Melee Attack template: Roll d20 add modifiers (for example STR of proficiency bonus)
 * Check target's Armor Class If it gets defeated, roll dmg dice and add bonus
 */
public final class MeleeWeaponAttack extends WeaponAttack {
  public MeleeWeaponAttack(Creature attacker, Weapon weapon, Weapon.Mode mode) {
    super(attacker, weapon, mode);
  }

  @Override
  public String toString() {
    return " using " + mode.ability();
  }
}
