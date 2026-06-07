package org.tcs.model.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Class to store creature's inventory */
public class Inventory {
  private Armor wornArmor;
  private Shield wieldedShield;

  public int AC(int dexMod) {
    return (wornArmor == null ? 10 + dexMod : wornArmor.AC(dexMod))
        + (wieldedShield == null ? 0 : 2);
  }

  /** wielded weapons don't count towards carriedWeapons */
  private final List<Weapon> carriedWeapons = new ArrayList<>();

  private final List<Weapon> wieldedWeapons = new ArrayList<>();

  public boolean addCarriedWeapon(Weapon weapon) {
    return carriedWeapons.add(weapon);
  }

  public Collection<Weapon> getWieldedWeapons() {
    return List.copyOf(wieldedWeapons);
  }

  public Collection<Weapon> getCarriedWeapons() {
    return List.copyOf(carriedWeapons);
  }

  public int handsOccupied() {
    return wieldedWeapons.stream().mapToInt(Weapon::handsOccupied).sum()
        + (wieldedShield == null ? 0 : 1);
  }

  public boolean wieldWeapon(Weapon weapon) {
    if (handsOccupied() + weapon.handsOccupied() > 2) {
      return false;
      // this might happen in normal code
    }

    if (!carriedWeapons.remove(weapon)) {
      throw new IllegalArgumentException();
      // if this happens, we have a problem.
    }

    return wieldedWeapons.add(weapon);
  }

  public boolean unwieldWeapon(Weapon weapon) {
    if (!(wieldedWeapons.contains(weapon))) throw new IllegalArgumentException();

    wieldedWeapons.remove(weapon);
    carriedWeapons.add(weapon);
    return true;
  }
}
