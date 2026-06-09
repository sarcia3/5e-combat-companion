package org.tcs.model.activity;

import org.tcs.model.*;
import org.tcs.model.dice.DamageRoll;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.equipment.Weapon;

/**
 * General Melee Attack template: Roll d20 add modifiers (for example STR of proficiency bonus)
 * Check target's Armor Class If it gets defeated, roll dmg dice and add bonus
 */
public final class MeleeWeaponAttack implements WeaponAttack {
  final Creature attacker;
  final DamageRoll damageRoll;
  final Ability ability;
  final Weapon weapon;

  public MeleeWeaponAttack(
      Creature attacker, DamageRoll damageRoll, Ability ability, Weapon weapon) {
    this.attacker = attacker;
    this.damageRoll = damageRoll;
    this.ability = ability;
    this.weapon = weapon;
  }

  @Override
  public AttackRoll attackRoll() {
    int roll =
        attacker
            .diceRoller()
            .roll(
                20,
                new DiceRoller.RollInformation(attacker.name(), "hitting with " + weapon.name()));
    // TODO add proficiencies and prof bonus
    int modifier = attacker.abilityModifier(ability);
    return new AttackRoll(roll, modifier);
  }

  @Override
  public Damage damageRoll(boolean isCritical) {
    if (isCritical) return damageRoll.critical().resolve(attacker.diceRoller(), attacker);
    return damageRoll.resolve(attacker.diceRoller(), attacker);
  }

  @Override
  public double getRange() {
    return 5.;
  }

  @Override
  public String toString() {
    return " using " + ability.toString();
  }
}
