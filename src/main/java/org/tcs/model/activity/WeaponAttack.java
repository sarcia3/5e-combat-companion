package org.tcs.model.activity;

import org.tcs.model.Creature;
import org.tcs.model.Damage;
import org.tcs.model.HasHitPoints;
import org.tcs.model.State;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.dice.RollMode;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.equipment.WeaponRange;

public abstract sealed class WeaponAttack permits MeleeWeaponAttack, RangedWeaponAttack {
  protected final Creature attacker;
  protected final Weapon weapon;
  protected final Weapon.Mode mode;

  protected WeaponAttack(Creature attacker, Weapon weapon, Weapon.Mode mode) {
    this.attacker = attacker;
    this.weapon = weapon;
    this.mode = mode;
  }

  public void resolve(State state, HasHitPoints target, RollMode rollMode) {
    // TODO how will view model get information about how this went? Probably shouldn't be a void,
    // but what else? Separate record?

    AttackRoll attackRollGenerated = attackRoll(rollMode);

    if (attackRollGenerated.naturalDice() == 1) {
      // critical failure. Attack fails.
      return;
    }

    boolean criticalHit = attackRollGenerated.naturalDice() == 20;
    if (!criticalHit && attackRollGenerated.total() < target.armorClass()) {
      // attack didn't meet AC
      return;
    }

    target.takeDamage(damageRoll(criticalHit));
  }

  public AttackRoll attackRoll(RollMode rollMode) {
    int roll =
        rollMode.roll(
            attacker.diceRoller(),
            20,
            new DiceRoller.RollInformation(attacker.name(), "attacking with " + weapon.name()));
    // TODO add proficiencies and prof bonus
    int modifier = attacker.abilityModifier(mode.ability());
    return new AttackRoll(roll, modifier);
  }

  public Damage damageRoll(boolean isCritical) {
    if (isCritical) return mode.damageRoll().critical().resolve(attacker.diceRoller(), attacker);
    return mode.damageRoll().resolve(attacker.diceRoller(), attacker);
  }

  /**
   * @return furthest distance that can be reached with this weapon in this mode. In particular this
   *     includes the long-range for Ranged Weapons which does impose disadvantage.
   */
  public WeaponRange getRange() {
    return mode.range();
  }
}
