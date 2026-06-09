// LLM generated to mirror WeaponsLibrary
// There are quite few armors in the game, so these are all armors
package org.tcs.model.equipment;

import java.util.*;
import org.tcs.model.equipment.Armor.Category;

public class ArmorLibrary {
  private ArmorLibrary() {
    throw new UnsupportedOperationException("Library is meant to be used as a static class.");
  }

  private static final Map<String, Armor> armors = new HashMap<>();

  static {
    // Light armor: AC = base + full Dex modifier
    add(new Armor("Padded", 11, Category.LIGHT));
    add(new Armor("Leather", 11, Category.LIGHT));
    add(new Armor("Studded Leather", 12, Category.LIGHT));

    // Medium armor: AC = base + Dex modifier (max +2)
    add(new Armor("Hide", 12, Category.MEDIUM));
    add(new Armor("Chain Shirt", 13, Category.MEDIUM));
    add(new Armor("Scale Mail", 14, Category.MEDIUM));
    add(new Armor("Breastplate", 14, Category.MEDIUM));
    add(new Armor("Half Plate", 15, Category.MEDIUM));

    // Heavy armor: AC = base (Dex ignored)
    add(new Armor("Ring Mail", 14, Category.HEAVY));
    add(new Armor("Chain Mail", 16, Category.HEAVY));
    add(new Armor("Splint", 17, Category.HEAVY));
    add(new Armor("Plate", 18, Category.HEAVY));
  }

  public static Collection<Armor> getArmors() {
    return List.copyOf(armors.values());
  }

  public static void add(Armor armor) {
    armors.put(armor.name(), armor);
  }

  /**
   * @return An armor associated with the given name.
   * @throws IllegalArgumentException if there is no item with this name.
   */
  public static Armor get(String name) {
    if (armors.containsKey(name)) return armors.get(name);
    throw new IllegalArgumentException("There is no armor with name " + name + ".");
  }
}
