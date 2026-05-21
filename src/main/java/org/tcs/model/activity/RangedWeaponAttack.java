package org.tcs.model.activity;

import org.tcs.model.Damage;
import org.tcs.model.HasHitPoints;
import org.tcs.model.State;

public final class RangedWeaponAttack implements WeaponAttack {
  @Override
  public AttackRoll attackRoll() {
    // TODO implement
    return null;
  }

  @Override
  public Damage damageRoll() {
    // TODO implement
    return null;
  }

  @Override
  public boolean inRange(HasHitPoints target, State state) {
    // TODO implement
    return false;
  }
}
