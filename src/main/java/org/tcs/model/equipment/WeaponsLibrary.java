package org.tcs.model.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.tcs.model.Ability;
import org.tcs.model.Damage;

public class WeaponsLibrary {
  private static List<Weapon> weapons;

  public static void load() {
    weapons = new ArrayList<>();
    weapons.add(
        new Weapon(
            "Dagger", 4, List.of(Ability.DEX, Ability.STR), List.of(Damage.Type.BLUDGEONING)));
  }

  // Maybe we can just make the list public?
  public static Collection<Weapon> getWeapons() {
    return List.copyOf(weapons);
  }

  public static void addWeapon(Weapon weapon) {
    weapons.add(weapon);
  }
}
