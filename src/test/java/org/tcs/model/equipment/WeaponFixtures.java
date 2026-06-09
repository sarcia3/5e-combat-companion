package org.tcs.model.equipment;

import org.tcs.model.Ability;

/** Test-only factories for {@link Weapon}, exposing its package-private constructor. */
public final class WeaponFixtures {
  private WeaponFixtures() {}

  public static Weapon longsword() {
    return new Weapon("Longsword", "1d8 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD);
  }
}
