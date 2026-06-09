package org.tcs.model.equipment;

import java.util.*;
import org.tcs.model.Ability;

public class WeaponsLibrary {
  private WeaponsLibrary() {
    throw new UnsupportedOperationException("Library is meant to be used as a static class.");
  }

  private static final Map<String, Weapon> weapons = new HashMap<>();

  static {
    // See https://5e.tools/variantrules.html#unarmed%20strike_xphb
    add(new Weapon("Fist", "1d1 BLUDGEONING", Ability.STR, false));

    // Simple Melee Weapons
    add(new Weapon("Club", "1d4 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Sickle", "1d4 SLASHING", Ability.STR, false));
    add(new Weapon("Light Hammer", "1d4 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Mace", "1d6 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Quarterstaff", "1d6 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Spear", "1d6 PIERCING", Ability.STR, false));
    add(new Weapon("Javelin", "1d6 PIERCING", Ability.STR, false));

    // Martial Melee Weapons
    add(new Weapon("Battleaxe", "1d8 SLASHING", Ability.STR, false));
    add(new Weapon("Flail", "1d8 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Morningstar", "1d8 PIERCING", Ability.STR, false));
    add(new Weapon("War Pick", "1d8 PIERCING", Ability.STR, false));
    add(new Weapon("Warhammer", "1d8 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Trident", "1d6 PIERCING", Ability.STR, false));

    // Finesse Martial Weapons (DEX or STR)
    add(
        new Weapon(
            "Shortsword",
            List.of(
                new Weapon.Mode("1d6 PIERCING", Ability.DEX, false),
                new Weapon.Mode("1d6 PIERCING", Ability.STR, false))));

    add(
        new Weapon(
            "Scimitar",
            List.of(
                new Weapon.Mode("1d6 SLASHING", Ability.DEX, false),
                new Weapon.Mode("1d6 SLASHING", Ability.STR, false))));

    add(
        new Weapon(
            "Rapier",
            List.of(
                new Weapon.Mode("1d8 PIERCING", Ability.DEX, false),
                new Weapon.Mode("1d8 PIERCING", Ability.STR, false))));

    add(
        new Weapon(
            "Whip",
            List.of(
                new Weapon.Mode("1d4 SLASHING", Ability.DEX, false),
                new Weapon.Mode("1d4 SLASHING", Ability.STR, false))));
    add(
        new Weapon(
            "Dagger",
            List.of(
                new Weapon.Mode("1d4 PIERCING", Ability.DEX, false),
                new Weapon.Mode("1d4 PIERCING", Ability.STR, false))));
    // new Weapon.Mode("1d4 PIERCING", Ability.DEX, true) when we add ranged weapons

    // Heavy Martial Melee Weapons (TODO two-handed only)
    add(new Weapon("Greataxe", "1d12 SLASHING", Ability.STR, false));
    add(new Weapon("Greatsword", "2d6 SLASHING", Ability.STR, false));
    add(new Weapon("Maul", "2d6 BLUDGEONING", Ability.STR, false));
    add(new Weapon("Halberd", "1d10 SLASHING", Ability.STR, false));
    add(new Weapon("Glaive", "1d10 SLASHING", Ability.STR, false));
    add(new Weapon("Pike", "1d10 PIERCING", Ability.STR, false));
    add(new Weapon("Lance", "1d12 PIERCING", Ability.STR, false));
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
