package org.tcs.model.activity;

import org.tcs.model.*;
import org.tcs.model.equipment.Weapon;

/**
 * General Melee Attack template: Roll d20 add modifiers (for example STR of proficiency bonus)
 * Check target's Armor Class If it gets defeated, roll dmg dice and add bonus
 */
public final class MeleeWeaponAttack implements WeaponAttack {
  final Creature attacker;
  final Weapon attackWeapon;

  MeleeWeaponAttack(Creature attacker, Weapon attackWeapon) {
    this.attacker = attacker;
    this.attackWeapon = attackWeapon;
  }

  @Override
  public AttackRoll attackRoll() {
    int roll = attacker.diceRoller().roll(20);
    // TODO add proficiencies and prof bonus
    int modifier = attacker.abilityModifier(attackWeapon.attackAbility);
    return new AttackRoll(roll, modifier);
  }

  @Override
  public Damage damageRoll() {
    // TODO allow for weapons that give more complicated dmg types
    Damage result = new Damage();
    result.add(attackWeapon.damageType, attacker.diceRoller().roll(attackWeapon.damageDice));
    return result;
  }
}
