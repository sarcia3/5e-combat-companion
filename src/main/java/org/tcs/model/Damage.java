package org.tcs.model;

import java.util.EnumMap;
import java.util.Map;

public class Damage {
  // TODO how would magical vs nonmagical dmg work?
  private final boolean isCritical;

  public Damage() {
    isCritical = false;
  }

  public Damage(boolean isCritical) {
    this.isCritical = isCritical;
  }

  public enum Type {
    ACID,
    BLUDGEONING,
    COLD,
    FIRE,
    FORCE,
    LIGHTNING,
    NECROTIC,
    PIERCING,
    POISON,
    PSYCHIC,
    RADIANT,
    SLASHING,
    THUNDER
  }

  public boolean isCritical() {
    return isCritical;
  }

  Map<Type, Integer> byType = new EnumMap<>(Type.class);

  public void add(Type type, int amount) {
    byType.put(type, byType.getOrDefault(type, 0) + amount);
  }

  /**
   * @return The accumulated amount of damage of the given type, or 0 if none has been added.
   */
  public int amount(Type type) {
    return byType.getOrDefault(type, 0);
  }
}
