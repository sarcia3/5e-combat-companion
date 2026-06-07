package org.tcs.model.equipment;

/** Class to store creature's inventory */
public class Inventory {
  private Armor wornArmor;
  private Shield wieldedShield;

  public int AC(int dexMod) {
    return (wornArmor == null ? 10 + dexMod : wornArmor.AC(dexMod))
        + (wieldedShield == null ? 0 : 2);
  }
}
