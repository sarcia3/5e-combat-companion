package org.tcs.model.magic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SpellLibraryTest {

  @Test
  void loadsKnownSpellsByName() {
    Spell fireBolt = SpellLibrary.get("Fire Bolt");
    assertEquals("Fire Bolt", fireBolt.name());
    assertEquals(0, fireBolt.level().value());
    assertTrue(fireBolt.level().isCantrip());
    assertEquals(new Ranged(120), fireBolt.range());
    assertInstanceOf(SingleCreature.class, fireBolt.targeting());
    assertEquals(CastingTime.ACTION, fireBolt.castingTime());
  }

  @Test
  void includesALeveledSpell() {
    Spell guidingBolt = SpellLibrary.get("Guiding Bolt");
    assertEquals(1, guidingBolt.level().value());
    assertFalse(guidingBolt.level().isCantrip());
  }

  @Test
  void getThrowsForUnknownSpell() {
    assertThrows(IllegalArgumentException.class, () -> SpellLibrary.get("Wish"));
  }
}
