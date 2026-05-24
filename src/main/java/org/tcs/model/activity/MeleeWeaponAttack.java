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
  final Ability ability;

  public MeleeWeaponAttack(Creature attacker, Weapon attackWeapon, Ability ability) {
    this.attacker = attacker;
    this.attackWeapon = attackWeapon;
    this.ability = ability;
  }

  @Override
  public AttackRoll attackRoll() {
    int roll = attacker.diceRoller().roll(20);
    // TODO add proficiencies and prof bonus
    int modifier = attacker.abilityModifier(ability);
    return new AttackRoll(roll, modifier);
  }

  @Override
  public Damage damageRoll() {
    // TODO implement using the new DamageRoll class
    Damage result = new Damage();
    for (Damage.Type type : attackWeapon.damageTypes())
      result.add(type, attacker.diceRoller().roll(attackWeapon.damageDice()));
    return result;
  }

  @Override
  public double getRange() {
    return 5.;
  }

  @Override
  public String toString() {
    return attacker.toString()
        + " attacks with "
        + attackWeapon.toString()
        + " using "
        + ability.toString();
  }
}
