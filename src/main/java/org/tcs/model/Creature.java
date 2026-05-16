package org.tcs.model;

import java.util.EnumMap;
import java.util.Map;

/** Player, monster, summon etc. Basically anything that exists, has hp and takes actions */
public class Creature implements HasInitiative {

  int hitPoints = 0;
  int hitPointMaximum;
  int temporaryHitPoints = 0;

  String name; // effectively final for the time being, but this might change in the future
  Map<Ability, Integer> abilityScores = new EnumMap<>(Ability.class);

  /** movement speed in feet */
  int movementSpeed = 0;

  Creature(String name, int hitPointMaximum, int movementSpeed) {
    // this constructor should be deleted later. This is for the minimal working example
    this.name = name;
    this.hitPointMaximum = this.hitPoints = hitPointMaximum;
    movementSpeed = 30;
    for (Ability ability : Ability.values()) {
      abilityScores.put(ability, 10);
    }
  }

  @Override
  public int generateInitiative() {
    return 20;
    // TODO: Create randomness generator class in the future
  }
}
