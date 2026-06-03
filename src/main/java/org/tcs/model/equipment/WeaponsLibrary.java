package org.tcs.model.equipment;

import java.util.*;
import org.tcs.model.Ability;
import org.tcs.model.Damage;

public class WeaponsLibrary {
  private WeaponsLibrary() {
    throw new UnsupportedOperationException("Library is meant to be used as a static class.");
  }

  private static final Map<String, Weapon> weapons = new HashMap<>();

  public static void load() {
    addWeapon(
        new Weapon("Dagger", 4, List.of(Ability.DEX, Ability.STR), List.of(Damage.Type.PIERCING)));

    addWeapon(new Weapon("Club", 4, Ability.STR, Damage.Type.BLUDGEONING));

    addWeapon(new Weapon("Mace", 6, Ability.STR, Damage.Type.BLUDGEONING));

    // See https://5e.tools/variantrules.html#unarmed%20strike_xphb
    addWeapon(new Weapon("Fist", 1, Ability.STR, Damage.Type.BLUDGEONING));
  }

  public static Collection<Weapon> getWeapons() {
    return List.copyOf(weapons.values());
  }

  public static void addWeapon(Weapon weapon) {
    weapons.put(weapon.name(), weapon);
  }

  /**
   * @return A weapon associated with the given name.
   * @throws IllegalArgumentException if there is no item with this name.
   */
  public static Weapon getWeaponByName(String name) {
    if (weapons.containsKey(name)) return weapons.get(name);
    throw new IllegalArgumentException("There is no weapon with name " + name + ".");
  }
}
