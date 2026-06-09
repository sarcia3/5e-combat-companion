// Mirrors WeaponsLibrary and ArmorLibrary, but for spells.
// Only simple, currently-implementable spells live here: single-target ranged
// spell attacks resolved via SpellAttackEffect. Saves, areas of effect, healing
// and conditions are intentionally left out until their effects are supported.
package org.tcs.model.magic;

import java.util.*;
import org.tcs.model.dice.DamageRoll;
import org.tcs.model.magic.effect.SpellAttackEffect;

public class SpellLibrary {
  private SpellLibrary() {
    throw new UnsupportedOperationException("Library is meant to be used as a static class.");
  }

  private static final Map<String, Spell> spells = new HashMap<>();

  static {
    // Cantrips (level 0): no spell slot required.
    add(
        new Spell(
            "Fire Bolt",
            "You hurl a mote of fire at a creature within range. On a hit it takes 1d10 fire"
                + " damage.",
            new SpellLevel(0),
            new Ranged(120),
            new SingleCreature(),
            CastingTime.ACTION,
            new SpellAttackEffect(DamageRoll.parse("1d10 fire"))));

    add(
        new Spell(
            "Ray of Frost",
            "A frigid beam of blue-white light streaks toward a creature, dealing 1d8 cold damage on"
                + " a hit.",
            new SpellLevel(0),
            new Ranged(60),
            new SingleCreature(),
            CastingTime.ACTION,
            new SpellAttackEffect(DamageRoll.parse("1d8 cold"))));

    add(
        new Spell(
            "Eldritch Blast",
            "A beam of crackling energy streaks toward a creature, dealing 1d10 force damage on a"
                + " hit.",
            new SpellLevel(0),
            new Ranged(120),
            new SingleCreature(),
            CastingTime.ACTION,
            new SpellAttackEffect(DamageRoll.parse("1d10 force"))));

    add(
        new Spell(
            "Chill Touch",
            "A spectral hand reaches out to a creature, dealing 1d8 necrotic damage on a hit.",
            new SpellLevel(0),
            new Ranged(120),
            new SingleCreature(),
            CastingTime.ACTION,
            new SpellAttackEffect(DamageRoll.parse("1d8 necrotic"))));

    // Leveled spells: require an available spell slot of the matching level.
    add(
        new Spell(
            "Witch Bolt",
            "A beam of crackling blue energy lances toward a creature, dealing 1d12 lightning damage"
                + " on a hit.",
            new SpellLevel(1),
            new Ranged(30),
            new SingleCreature(),
            CastingTime.ACTION,
            new SpellAttackEffect(DamageRoll.parse("1d12 lightning"))));

    add(
        new Spell(
            "Guiding Bolt",
            "A flash of light streaks toward a creature, dealing 4d6 radiant damage on a hit.",
            new SpellLevel(1),
            new Ranged(120),
            new SingleCreature(),
            CastingTime.ACTION,
            new SpellAttackEffect(DamageRoll.parse("4d6 radiant"))));
  }

  public static Collection<Spell> getSpells() {
    return List.copyOf(spells.values());
  }

  public static void add(Spell spell) {
    spells.put(spell.name(), spell);
  }

  /**
   * @return A spell associated with the given name.
   * @throws IllegalArgumentException if there is no spell with this name.
   */
  public static Spell get(String name) {
    if (spells.containsKey(name)) return spells.get(name);
    throw new IllegalArgumentException("There is no spell with name " + name + ".");
  }
}
