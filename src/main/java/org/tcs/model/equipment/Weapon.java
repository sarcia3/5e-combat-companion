package org.tcs.model.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.Damage;
import org.tcs.model.activity.MeleeWeaponAttack;
import org.tcs.model.activity.WeaponAttack;

// In most cases both attackAbilities and damageTypes will be one element, but it seems like the
// best construction for odd behaviours
public record Weapon(
    String name, int damageDice, List<Ability> attackAbilities, List<Damage.Type> damageTypes) {

  public Weapon(String name, int damageDice, Ability attackAbility, Damage.Type damageType) {
    this(name, damageDice, List.of(attackAbility), List.of(damageType));
  }

  public Weapon(
      String name, int damageDice, List<Ability> attackAbilities, Damage.Type damageType) {
    this(name, damageDice, attackAbilities, List.of(damageType));
  }

  public Weapon(String name, int damageDice, Ability attackAbility, List<Damage.Type> damageTypes) {
    this(name, damageDice, List.of(attackAbility), damageTypes);
  }

  public Collection<WeaponAttack> generateAttacks(Creature creature) {
    // temporarily every weapon returns the same thing
    // in the future this should be handled by some flags and whatnot
    List<WeaponAttack> list = new ArrayList<>();
    for (Ability ability : attackAbilities)
      list.add(new MeleeWeaponAttack(creature, this, ability));
    return list;
  }
}
