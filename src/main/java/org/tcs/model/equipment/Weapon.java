package org.tcs.model.equipment;

import org.tcs.model.Ability;
import org.tcs.model.Damage;

public class Weapon {
  // draft version. Only basic properties
  public final String name;
  public final int damageDice;
  public final Ability attackAbility;
  public final Damage.Type damageType;

  Weapon(String name, int damageDice, Ability attackAbility, Damage.Type damageType) {
    this.name = name;
    this.damageDice = damageDice;
    this.attackAbility = attackAbility;
    this.damageType = damageType;
  }

  @Override
  public String toString() {
    return name;
  }
}
