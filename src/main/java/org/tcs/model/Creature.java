package org.tcs.model;

import java.util.*;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.dice.RandomDiceRoller;
import org.tcs.model.equipment.Inventory;
import org.tcs.model.geometry.Point;

/** Player, monster, summon etc. Basically anything that exists, has hp and takes actions */
public class Creature implements HasInitiative, HasHitPoints {

  String name;
  int hitPoints = 0;
  int hitPointMaximum = 0;
  int temporaryHitPoints = 0;
  DiceRoller diceRoller;
  int proficiencyBonus = 2;
  private Point position;
  Inventory inventory = new Inventory();

  DeathTracker deathTracker = new DeathTracker();
  boolean isDead = false;

  Map<Ability, Integer> abilityScores = new EnumMap<>(Ability.class);

  double movementLeft = 0.0;
  double movementSpeed = 0.0;

  /** Creates a new creature. Random dice rolling by default. */
  public Creature(String name, Point position, int hitPointMaximum, double movementSpeed) {
    this(name, position, hitPointMaximum, movementSpeed, 2, new RandomDiceRoller());
  }

  public Creature(
      String name,
      Point position,
      int hitPointMaximum,
      double movementSpeed,
      int proficiencyBonus,
      DiceRoller diceRoller) {
    // this constructor should be deleted later. This is for the minimal working example
    this.name = name;
    this.position = position;
    this.hitPointMaximum = this.hitPoints = hitPointMaximum;
    this.proficiencyBonus = proficiencyBonus;
    this.movementSpeed = movementSpeed;
    this.movementLeft = movementSpeed;

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

  public String name() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
    int actualDamage = damage.byType.values().stream().mapToInt(Integer::intValue).sum();
    if (hitPoints == 0 && actualDamage > 0) {
      if (actualDamage <= hitPointMaximum) {
        isDead = true;
        return;
      }
      // TODO implement catching critical throws
      DeathTracker.Result result = deathTracker.takingDamage(false);
      if (result == DeathTracker.Result.DEATH) isDead = true;
    } else {
      hitPoints -= actualDamage;
      if (hitPoints < 0) {
        if (hitPoints <= -hitPointMaximum) isDead = true;
        hitPoints = 0;
      }
    }
  }

  public double movementLeft() {
    return movementLeft;
  }

  public double movementSpeed() {
    return movementSpeed;
  }

  public Inventory inventory() {
    return inventory;
  }

  @Override
  public int armorClass() {
    return inventory.armorClass(abilityModifier(Ability.DEX));
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

  public boolean isDead() {
    return isDead;
  }

  public boolean isUnconscious() {
    return hitPoints == 0;
  }

  // package private, should only be called by state
  void deathSavingThrow() {
    DeathTracker.Result result = deathTracker.proceed(diceRoller);
    if (result == DeathTracker.Result.DEATH) isDead = true;
    if (result == DeathTracker.Result.SAVE) {
      hitPoints = 1;
      deathTracker.reset();
    }
  }

  @Override
  public String toString() {
    return name;
  }
}
