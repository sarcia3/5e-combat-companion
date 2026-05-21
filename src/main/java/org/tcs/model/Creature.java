package org.tcs.model;

import java.util.EnumMap;
import java.util.Map;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.dice.RandomDiceRoller;

/** Player, monster, summon etc. Basically anything that exists, has hp and takes actions */
public class Creature implements HasInitiative, HasHitPoints {

  int hitPoints = 0;
  int hitPointMaximum;
  int temporaryHitPoints = 0;
  DiceRoller diceRoller;

  String name; // effectively final for the time being, but this might change in the future
  Map<Ability, Integer> abilityScores = new EnumMap<>(Ability.class);

  /** movement speed in feet */
  int movementSpeed = 0;

  /** Creates a new creature. Random dice rolling by default. */
  Creature(String name, int hitPointMaximum, int movementSpeed) {
    this(name, hitPointMaximum, movementSpeed, new RandomDiceRoller());
  }

  Creature(String name, int hitPointMaximum, int movementSpeed, DiceRoller diceRoller) {
    // this constructor should be deleted later. This is for the minimal working example
    this.name = name;
    this.hitPointMaximum = this.hitPoints = hitPointMaximum;
    this.movementSpeed = movementSpeed;

    for (Ability ability : Ability.values()) abilityScores.put(ability, 10);

    this.diceRoller = new RandomDiceRoller();
  }

  @Override
  public int generateInitiative() {
    return diceRoller.roll(20); // TODO add DEX modifier to the roll
  }

  @Override
  public int hitPoints() {
    return hitPoints + temporaryHitPoints;
  }

  @Override
  public int hitPointMaximum() {
    return hitPointMaximum;
  }

  @Override
  public void takeDamage(Damage damage) {
    // TODO implement, take in mind resistance etc.
  }
}
