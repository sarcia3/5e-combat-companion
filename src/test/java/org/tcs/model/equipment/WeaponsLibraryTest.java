package org.tcs.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WeaponsLibraryTest {

  @Test
  void shortbowIsRangedAndUsesArrows() {
    Weapon.Mode mode = WeaponsLibrary.get("Shortbow").possibleAttacks().get(0);
    assertTrue(mode.isRanged());
    assertFalse(mode.isThrown());
    assertEquals(Ammunition.ARROW, mode.requiredAmmunition());
  }

  @Test
  void dartIsThrownAndUsesNoAmmunition() {
    Weapon.Mode mode = WeaponsLibrary.get("Dart").possibleAttacks().get(0);
    assertTrue(mode.isThrown());
    assertNull(mode.requiredAmmunition());
  }
}
