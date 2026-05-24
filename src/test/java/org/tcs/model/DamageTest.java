package org.tcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class DamageTest {

  @Test
  void addAccumulatesAmountsOfTheSameType() {
    Damage damage = new Damage();
    damage.add(Damage.Type.FIRE, 5);
    damage.add(Damage.Type.FIRE, 3);

    assertEquals(8, damage.byType.get(Damage.Type.FIRE));
  }

  @Test
  void addKeepsDifferentTypesSeparate() {
    Damage damage = new Damage();
    damage.add(Damage.Type.FIRE, 5);
    damage.add(Damage.Type.COLD, 3);

    assertEquals(5, damage.byType.get(Damage.Type.FIRE));
    assertEquals(3, damage.byType.get(Damage.Type.COLD));
  }

  @Test
  void unsetTypesAreAbsentFromTheMap() {
    Damage damage = new Damage();
    damage.add(Damage.Type.FIRE, 5);

    assertNull(damage.byType.get(Damage.Type.RADIANT));
  }

  @Test
  void amountReturnsAccumulatedTotalForType() {
    Damage damage = new Damage();
    damage.add(Damage.Type.FIRE, 3);
    damage.add(Damage.Type.FIRE, 4);

    assertEquals(7, damage.amount(Damage.Type.FIRE));
  }

  @Test
  void amountReturnsZeroForUnsetType() {
    Damage damage = new Damage();
    damage.add(Damage.Type.FIRE, 5);

    assertEquals(0, damage.amount(Damage.Type.RADIANT));
  }
}
