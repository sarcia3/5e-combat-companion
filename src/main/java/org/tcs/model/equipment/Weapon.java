package org.tcs.model.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.activity.MeleeWeaponAttack;
import org.tcs.model.activity.RangedWeaponAttack;
import org.tcs.model.activity.WeaponAttack;
import org.tcs.model.dice.DamageRoll;

public record Weapon(String name, List<Mode> possibleAttacks) {
  public Weapon(String name, Mode possibleAttack) {
    this(name, List.of(possibleAttack));
  }

  public Weapon(
      String name, String damageRollStr, Ability ability, boolean isRanged, WeaponRange range) {
    this(name, List.of(new Mode(damageRollStr, ability, isRanged, null, range)));
  }

  public Collection<WeaponAttack> generateAttacks(Creature creature) {
    List<WeaponAttack> list = new ArrayList<>();
    for (Mode mode : possibleAttacks) {
      // TODO fill in when ranged attacks are implemented
      //if (mode.isRanged()) list.add(new RangedWeaponAttack());
      //else
      list.add(new MeleeWeaponAttack(creature, mode.damageRoll(), mode.ability(), this));
    }
    return list;
  }

  public record Mode(
      DamageRoll damageRoll, Ability ability, boolean isRanged, Ammunition requiredAmmunition, WeaponRange range) {
    public Mode(
        String damageRollStr, Ability ability, boolean isRanged, Ammunition requiredAmmunition, WeaponRange range) {
      this(DamageRoll.parse(damageRollStr), ability, isRanged, requiredAmmunition, range);
    }
  }

  public int handsOccupied() {
    return 1; // temporary. Some weapons are gonna have that equal to 0 (e.g. spider's teeth)
  }
}
