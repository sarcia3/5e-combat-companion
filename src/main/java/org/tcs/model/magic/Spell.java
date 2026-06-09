package org.tcs.model.magic;

import java.util.Objects;

public record Spell(
    String name,
    String description,
    SpellLevel level,
    SpellRange range,
    Targeting targeting,
    CastingTime castingTime,
    SpellEffect effect) {
  public Spell {
    Objects.requireNonNull(name, "spell name must not be null");
    Objects.requireNonNull(level, "spell level must not be null");
    Objects.requireNonNull(range, "spell range must not be null, consider self instead");
    Objects.requireNonNull(targeting, "spell targeting must not be null");
    Objects.requireNonNull(castingTime, "spell casting time must not be null");
    Objects.requireNonNull(effect, "spell effect must not be null");
    if (range instanceof Touch && !(targeting instanceof SingleCreature)) {
      throw new IllegalArgumentException("Touch spells must target a single creature");
    }
  }
}
