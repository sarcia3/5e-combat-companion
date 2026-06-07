package org.tcs.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.tcs.model.equipment.Armor.Category;

class ArmorTest {

  @Test
  void lightArmorAddsFullDexModifier() {
    Armor leather = new Armor("Leather", 11, Category.LIGHT);
    assertEquals(14, leather.armorClass(3)); // 11 + 3, no cap
    assertEquals(11, leather.armorClass(0));
    assertEquals(10, leather.armorClass(-1)); // negative modifiers still apply
  }

  @Test
  void mediumArmorCapsDexModifierAtTwo() {
    Armor halfPlate = new Armor("Half Plate", 15, Category.MEDIUM);
    assertEquals(16, halfPlate.armorClass(1)); // below cap: 15 + 1
    assertEquals(17, halfPlate.armorClass(2)); // at cap: 15 + 2
    assertEquals(17, halfPlate.armorClass(5)); // above cap, still +2
  }

  @Test
  void heavyArmorIgnoresDexModifier() {
    Armor plate = new Armor("Plate", 18, Category.HEAVY);
    assertEquals(18, plate.armorClass(3));
    assertEquals(18, plate.armorClass(-2));
  }
}
