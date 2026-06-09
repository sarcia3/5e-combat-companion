package org.tcs.model.equipment;

import java.util.*;

/** Class to store creature's inventory */
public class Inventory {
  private Armor wornArmor;
  private Shield equippedShield;

  /** equipped weapons don't count towards storedWeapons */
  private final List<Weapon> storedWeapons = new ArrayList<>();

  private final List<Weapon> equippedWeapons = new ArrayList<>();

  private final Map<Ammunition, Integer> ammunition = new EnumMap<>(Ammunition.class);

  public boolean addStoredWeapon(Weapon weapon) {
    return storedWeapons.add(weapon);
  }

  public boolean removeStoredWeapon(Weapon weapon) {
    if (!storedWeapons.remove(weapon)) throw new IllegalArgumentException();
    return true;
  }

  public Collection<Weapon> getEquippedWeapons() {
    return List.copyOf(equippedWeapons);
  }

  public Collection<Weapon> getStoredWeapons() {
    return List.copyOf(storedWeapons);
  }

  public int handsOccupied() {
    return equippedWeapons.stream().mapToInt(Weapon::handsOccupied).sum()
        + (equippedShield == null ? 0 : 1);
  }

  public boolean equipWeapon(Weapon weapon) {
    if (handsOccupied() + weapon.handsOccupied() > 2) {
      return false;
      // this might happen in normal code
    }

    if (!storedWeapons.remove(weapon)) {
      throw new IllegalArgumentException();
      // if this happens, we have a problem.
    }

    return equippedWeapons.add(weapon);
  }

  public boolean unequipWeapon(Weapon weapon) {
    if (!(equippedWeapons.contains(weapon))) throw new IllegalArgumentException();

    equippedWeapons.remove(weapon);
    storedWeapons.add(weapon);
    return true;
  }

  public void addAmmunition(Ammunition type, int count) {
    if (count < 0) throw new IllegalArgumentException();
    ammunition.put(type, ammunition.getOrDefault(type, 0) + count);
  }

  public boolean hasAmmunition(Ammunition type) {
    return ammunition.getOrDefault(type, 0) > 0;
  }

  public void useAmmunition(Ammunition type) {
    if (!hasAmmunition(type)) throw new IllegalStateException();
    else ammunition.put(type, ammunition.get(type) - 1);
  }

  public int armorClass(int dexMod) {
    return (wornArmor == null ? 10 + dexMod : wornArmor.armorClass(dexMod))
        + (equippedShield == null ? 0 : 2);
  }

  public Armor wornArmor() {
    return wornArmor;
  }

  public void setWornArmor(Armor armor) {
    wornArmor = armor;
  }
}
