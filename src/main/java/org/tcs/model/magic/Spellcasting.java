package org.tcs.model.magic;

import java.util.*;
import org.tcs.model.Ability;
import org.tcs.model.Creature;

public class Spellcasting {
  private final List<Spell> spells;
  private final SpellSlotTracker spellSlots;
  private final Ability spellcastingAbility;
  private final Creature caster;

  /**
   * Created the spellcasting tracker
   *
   * @param spells spells available to the spellcaster. For no spells pass empty collection
   * @param spellSlots spell slots available to the spellcaster. For no spells pass empty
   *     collection. Cantrips always available
   * @param spellcastingAbility spellcasting ability
   * @throws NullPointerException if any of the arguments is null
   */
  public Spellcasting(
      Collection<Spell> spells,
      Map<SpellLevel, Integer> spellSlots,
      Ability spellcastingAbility,
      Creature caster) {
    Objects.requireNonNull(spells, "spells must not be null");
    Objects.requireNonNull(spellSlots, "spellSlots must not be null");
    Objects.requireNonNull(spellcastingAbility, "spellcastingAbility must not be null");
    Objects.requireNonNull(caster, "caster must not be null");
    this.spells = new ArrayList<>(spells);
    this.spellSlots = new SpellSlotTracker(spellSlots);
    this.spellcastingAbility = spellcastingAbility;
    this.caster = caster;
  }

  public int spellAttackBonus() {
    return caster.proficiencyBonus() + caster.abilityModifier(spellcastingAbility);
  }

  public int spellSaveDifficultyClass() {
    // standard name is spellSaveDC, but with dedication to Michał I'm using the full name
    return 8 + caster.proficiencyBonus() + caster.abilityModifier(spellcastingAbility);
  }

  public boolean hasSlot(SpellLevel level) {
    return spellSlots.hasSlot(level);
  }

  /** Spends a spell slot for a leveled spell. Cantrips (level 0) consume no slot. */
  public void castSpell(SpellLevel level) {
    if (level.isCantrip()) return;
    spellSlots.expendSlot(level);
  }

  public List<Spell> getSpells() {
    return List.copyOf(spells);
  }

  public void addSpell(Spell spell) {
    if (!spells.contains(spell)) spells.add(spell);
  }

  public void removeSpell(Spell spell) {
    spells.remove(spell);
  }
}
