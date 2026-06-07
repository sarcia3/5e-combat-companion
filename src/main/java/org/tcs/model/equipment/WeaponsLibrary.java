package org.tcs.model.equipment;

import java.util.*;
import org.tcs.model.Ability;

public class WeaponsLibrary {
  private WeaponsLibrary() {
    throw new UnsupportedOperationException("Library is meant to be used as a static class.");
  }

  private static final Map<String, Weapon> weapons = new HashMap<>();

  public static void load() {
    add(
        new Weapon(
            "Dagger",
            List.of(
                new Weapon.Mode("1d4 PIERCING", Ability.DEX, false),
                new Weapon.Mode("1d4 PIERCING", Ability.STR, false))));
    // new Weapon.Mode("1d4 PIERCING", Ability.DEX, true) when we add ranged weapons

    add(new Weapon("Club", "1d4 BLUDGEONING", Ability.STR, false));

    add(new Weapon("Mace", "1d6 BLUDGEONING", Ability.STR, false));

    // See https://5e.tools/variantrules.html#unarmed%20strike_xphb
    add(new Weapon("Fist", "1d1 BLUDGEONING", Ability.STR, false));
  }

  public static Collection<Weapon> getWeapons() {
    return List.copyOf(weapons.values());
  }

  public static void add(Weapon weapon) {
    weapons.put(weapon.name(), weapon);
  }

  /**
   * @return A weapon associated with the given name.
   * @throws IllegalArgumentException if there is no item with this name.
   */
  public static Weapon get(String name) {
    if (weapons.containsKey(name)) return weapons.get(name);
    throw new IllegalArgumentException("There is no weapon with name " + name + ".");
  }
}
