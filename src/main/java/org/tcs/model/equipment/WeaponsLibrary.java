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
    add(new Weapon("Fist", "1d1 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));

    // Simple Melee Weapons
    add(new Weapon("Club", "1d4 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Sickle", "1d4 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(
        new Weapon(
            "Light Hammer", "1d4 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Mace", "1d6 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(
        new Weapon(
            "Quarterstaff", "1d6 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Spear", "1d6 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Javelin", "1d6 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));

    // Martial Melee Weapons
    add(new Weapon("Battleaxe", "1d8 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Flail", "1d8 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Morningstar", "1d8 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("War Pick", "1d8 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Warhammer", "1d8 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Trident", "1d6 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));

    // Finesse Martial Weapons (DEX or STR)
    add(
        new Weapon(
            "Shortsword",
            List.of(
                new Weapon.Mode(
                    "1d6 PIERCING", Ability.DEX, false, null, WeaponRange.MELEE_STANDARD),
                new Weapon.Mode(
                    "1d6 PIERCING", Ability.STR, false, null, WeaponRange.MELEE_STANDARD))));

    add(
        new Weapon(
            "Scimitar",
            List.of(
                new Weapon.Mode(
                    "1d6 SLASHING", Ability.DEX, false, null, WeaponRange.MELEE_STANDARD),
                new Weapon.Mode(
                    "1d6 SLASHING", Ability.STR, false, null, WeaponRange.MELEE_STANDARD))));

    add(
        new Weapon(
            "Rapier",
            List.of(
                new Weapon.Mode(
                    "1d8 PIERCING", Ability.DEX, false, null, WeaponRange.MELEE_STANDARD),
                new Weapon.Mode(
                    "1d8 PIERCING", Ability.STR, false, null, WeaponRange.MELEE_STANDARD))));

    add(
        new Weapon(
            "Whip",
            List.of(
                new Weapon.Mode(
                    "1d4 SLASHING", Ability.DEX, false, null, WeaponRange.MELEE_STANDARD),
                new Weapon.Mode(
                    "1d4 SLASHING", Ability.STR, false, null, WeaponRange.MELEE_STANDARD))));
    add(
        new Weapon(
            "Dagger",
            List.of(
                new Weapon.Mode(
                    "1d4 PIERCING", Ability.DEX, false, null, WeaponRange.MELEE_STANDARD),
                new Weapon.Mode(
                    "1d4 PIERCING", Ability.STR, false, null, WeaponRange.MELEE_STANDARD),
                new Weapon.Mode("1d4 PIERCING", Ability.DEX, true, null, new WeaponRange(20, 60)),
                new Weapon.Mode(
                    "1d4 PIERCING", Ability.STR, true, null, new WeaponRange(20, 60)))));

    // Heavy Martial Melee Weapons (TODO two-handed only)
    add(new Weapon("Greataxe", "1d12 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Greatsword", "2d6 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Maul", "2d6 BLUDGEONING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Halberd", "1d10 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Glaive", "1d10 SLASHING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Pike", "1d10 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));
    add(new Weapon("Lance", "1d12 PIERCING", Ability.STR, false, WeaponRange.MELEE_STANDARD));

    // Simple Ranged Weapons
    add(new Weapon("Dart", "1d4 PIERCING", Ability.DEX, true, new WeaponRange(20, 60))); // thrown
    add(rangedWeapon("Sling", "1d4 BLUDGEONING", Ammunition.BULLET, new WeaponRange(30, 120)));
    add(rangedWeapon("Shortbow", "1d6 PIERCING", Ammunition.ARROW, new WeaponRange(80, 320)));
    add(rangedWeapon("Light Crossbow", "1d8 PIERCING", Ammunition.BOLT, new WeaponRange(80, 320)));

    // Martial Ranged Weapons
    add(rangedWeapon("Blowgun", "1d1 PIERCING", Ammunition.NEEDLE, new WeaponRange(25, 100)));
    add(rangedWeapon("Hand Crossbow", "1d6 PIERCING", Ammunition.BOLT, new WeaponRange(30, 120)));
    add(rangedWeapon("Longbow", "1d8 PIERCING", Ammunition.ARROW, new WeaponRange(150, 600)));
    add(
        rangedWeapon(
            "Heavy Crossbow", "1d10 PIERCING", Ammunition.BOLT, new WeaponRange(100, 400)));
  }

  public static Collection<Weapon> getWeapons() {
    return List.copyOf(weapons.values());
  }

  public static void add(Weapon weapon) {
    weapons.put(weapon.name(), weapon);
  }

  private static Weapon rangedWeapon(
      String name, String damageRollStr, Ammunition ammunition, WeaponRange range) {
    return new Weapon(name, new Weapon.Mode(damageRollStr, Ability.DEX, true, ammunition, range));
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
