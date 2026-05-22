package org.tcs.model;

import java.util.EnumMap;
import java.util.Map;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.dice.RandomDiceRoller;
import org.tcs.model.geometry.Point;

/** Player, monster, summon etc. Basically anything that exists, has hp and takes actions */
public class Creature implements HasInitiative, HasHitPoints {

  String name; // effectively final for the time being, but this might change in the future
  int hitPoints = 0;
  int hitPointMaximum = 0;
  int temporaryHitPoints = 0;
  DiceRoller diceRoller;
  int proficiencyBonus = 2;
  private Point position;

  Map<Ability, Integer> abilityScores = new EnumMap<>(Ability.class);

  /** movement speed in feet */
  double maxMovementSpeed = 0;

  double movementSpeed = 0;

  /** Creates a new creature. Random dice rolling by default. */
  Creature(String name, Point position, int hitPointMaximum, int movementSpeed) {
    this(name, position, hitPointMaximum, movementSpeed, 2, new RandomDiceRoller());
  }

  Creature(
      String name,
      Point position,
      int hitPointMaximum,
      int movementSpeed,
      int proficiencyBonus,
      DiceRoller diceRoller) {
    // this constructor should be deleted later. This is for the minimal working example
    this.name = name;
    this.position = position;
    this.hitPointMaximum = this.hitPoints = hitPointMaximum;
    this.proficiencyBonus = proficiencyBonus;
    this.movementSpeed = movementSpeed;

    for (Ability ability : Ability.values()) abilityScores.put(ability, 10);

    this.diceRoller = diceRoller;
  }

  public Point position() {
    return position;
  }

  // We make this package-private as it allows the state to be dessynchronized.
  void setPosition(Point position) {
    this.position = position;
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

  public int abilityModifier(Ability ability) {
    return Math.floorDiv(abilityScores.get(ability) - 10, 2);
  }

  @Override
  public void takeDamage(Damage damage) {
    // TODO actually implement, considering resistance immunities and so on
    // currently we just subtract the sum
    hitPoints -= damage.byType.values().stream().mapToInt(Integer::intValue).sum();
    if (hitPoints < 0) {
      // do something
      hitPoints = 0;
    }
  }

  @Override
  public int armorClass() {
    // TODO implement
    return 10;
  }

  public int proficiencyBonus() {
    return proficiencyBonus;
  }

  public DiceRoller diceRoller() {
    return diceRoller;
  }

  int criticalHitThreshold() {
    // there are some features that make it 19. Very rare, but no reason not to add it at this stage
    // as well.
    return 20;
  }
}
