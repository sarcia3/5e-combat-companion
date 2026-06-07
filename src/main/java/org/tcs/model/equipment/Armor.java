package org.tcs.model.equipment;

public record Armor(String name, int base, Category category) {
  public enum Category {
    LIGHT,
    MEDIUM,
    HEAVY;

    int dexBonus(int dexMod) {
      return switch (this) {
        case LIGHT -> dexMod;
        case MEDIUM -> Math.min(dexMod, 2);
        case HEAVY -> 0;
      };
    }
  }

  int armorClass(int dexMod) {
    return base + category.dexBonus(dexMod);
  }
}
