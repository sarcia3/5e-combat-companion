package org.tcs.model.equipment;

import java.util.Collection;
import java.util.List;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.Damage;
import org.tcs.model.activity.MeleeWeaponAttack;
import org.tcs.model.activity.WeaponAttack;

public class Weapon {
  // draft version. Only basic properties
  public final String name;
  public final int damageDice;
  public final Ability attackAbility;
  public final Damage.Type damageType;

  public Weapon(String name, int damageDice, Ability attackAbility, Damage.Type damageType) {
    this.name = name;
    this.damageDice = damageDice;
    this.attackAbility = attackAbility;
    this.damageType = damageType;
  }

  public Collection<WeaponAttack> generateAttacks(Creature creature) {
    // temporarily every weapon returns the same thing
    return List.of(new MeleeWeaponAttack(creature, this));
  }

  @Override
  public String toString() {
    return name;
  }
}
