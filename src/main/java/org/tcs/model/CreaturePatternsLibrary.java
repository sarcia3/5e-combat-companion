package org.tcs.model;

import java.util.List;
import java.util.Map;

public class CreaturePatternsLibrary {
  private CreaturePatternsLibrary() {
    throw new UnsupportedOperationException("Library is meant to be used as a static class.");
  }

  private static Map<String, Creature.Builder> builders;

  static {
    Creature.Builder spider = new Creature.Builder();
    spider
        .fixedArmorClass(13)
        .hitPointMaximum(13)
        .movementSpeed(30.0)
        .vulnerabilities(List.of(Damage.Type.BLUDGEONING))
        .immunities(List.of(Damage.Type.POISON));
  }

  void add(String name, Creature.Builder builder) {
    builder.name(name);
    builders.put(name, builder);
  }
}
