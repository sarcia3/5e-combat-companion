package org.tcs.model;

import java.util.EnumMap;
import java.util.Map;

public class Damage {
  enum Type {
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
}
