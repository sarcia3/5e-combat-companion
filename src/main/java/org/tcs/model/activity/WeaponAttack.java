package org.tcs.model.activity;

import org.tcs.model.Damage;
import org.tcs.model.HasHitPoints;
import org.tcs.model.State;

public sealed interface WeaponAttack permits MeleeWeaponAttack, RangedWeaponAttack {
  default void resolve(State state, HasHitPoints target) {
    // TODO how will view model get information about how this went? Probably shouldn't be a void,
    // but what else? Separate record?

    if (!inRange(target, state)) throw new IllegalArgumentException();

    AttackRoll attackRollGenerated = attackRoll();
    if (attackRollGenerated.naturalDice() == 1) {
      // critical failure. Attack fails.
      return;
    }

    // TODO allow for custom thresholds for given attacker
    boolean automaticHit = attackRollGenerated.naturalDice() == 20;
    if (!automaticHit && attackRollGenerated.total() < target.armorClass()) {
      // attack didn't meet AC
      return;
    }

    target.takeDamage(damageRoll());
  }

  AttackRoll attackRoll();

  Damage damageRoll();

  boolean inRange(HasHitPoints target, State state);
}
