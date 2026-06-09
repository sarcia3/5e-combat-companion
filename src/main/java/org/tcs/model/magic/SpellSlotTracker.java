package org.tcs.model.magic;

import java.util.HashMap;
import java.util.Map;

public class SpellSlotTracker {
  private final Map<SpellLevel, Integer> maxSpellSlots;
  private final Map<SpellLevel, Integer> spellSlotsExpended = new HashMap<>();

  public SpellSlotTracker(Map<SpellLevel, Integer> maxSpellSlots) {
    // todo add check if nonnegative
    this.maxSpellSlots = maxSpellSlots;
  }

  public boolean hasSlot(SpellLevel spellLevel) {
    int maxSlots = maxSpellSlots.getOrDefault(spellLevel, 0);
    return spellSlotsExpended.getOrDefault(spellLevel, 0) < maxSlots;
  }

  public void expendSlot(SpellLevel spellLevel) {
    if (!hasSlot(spellLevel)) {
      // todo is this the best exception for this situation
      throw new IllegalArgumentException();
    }

    spellSlotsExpended.put(spellLevel, spellSlotsExpended.getOrDefault(spellLevel, 0) + 1);
  }
}
