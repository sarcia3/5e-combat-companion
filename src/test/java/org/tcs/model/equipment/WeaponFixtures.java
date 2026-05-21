package org.tcs.model.equipment;

import org.tcs.model.Ability;
import org.tcs.model.Damage;

/** Test-only factories for {@link Weapon}, exposing its package-private constructor. */
public final class WeaponFixtures {
  private WeaponFixtures() {}

  public static Weapon longsword() {
    return new Weapon("Longsword", 8, Ability.STR, Damage.Type.SLASHING);
  }
}
