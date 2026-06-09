package org.tcs.model.activity;

import org.tcs.model.Creature;
import org.tcs.model.HasHitPoints;
import org.tcs.model.State;
import org.tcs.model.dice.RollMode;
import org.tcs.model.equipment.Weapon;

public final class RangedWeaponAttack extends WeaponAttack {
  public RangedWeaponAttack(Creature attacker, Weapon weapon, Weapon.Mode mode) {
    super(attacker, weapon, mode);
  }

  @Override
  public void resolve(State state, HasHitPoints target, RollMode rollMode) {
    if (!mode.isThrown())
      if (attacker.inventory().hasAmmunition(mode.requiredAmmunition()))
        attacker.inventory().useAmmunition(mode.requiredAmmunition());
      else throw new IllegalStateException("No ammunition");
    else {
      attacker.inventory().unequipWeapon(weapon);
      attacker.inventory().removeStoredWeapon(weapon);
    }
    super.resolve(state, target, rollMode);
  }

  @Override
  public String toString() {
    if (mode.isThrown()) {
      return attacker + " attacks by throwing " + weapon;
    } else {
      return attacker + " attacks by shooting " + mode.requiredAmmunition() + " from " + weapon;
    }
  }
}
