package org.tcs.model;

import java.util.EnumMap;
import java.util.Map;

public class Damage {
  // TODO how would magical vs nonmagical dmg work?
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

  Map<Type, Integer> byType = new EnumMap<>(Type.class);

  public void add(Type type, int amount) {
    byType.put(type, byType.getOrDefault(type, 0) + amount);
  }
}
