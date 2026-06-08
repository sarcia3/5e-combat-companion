package org.tcs.model;

import java.util.*;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.dice.RandomDiceRoller;
import org.tcs.model.equipment.Inventory;
import org.tcs.model.geometry.Point;

/** Player, monster, summon etc. Basically anything that exists, has hp and takes actions */
public class Creature implements HasInitiative, HasHitPoints {
  String name;
  int hitPoints;
  int temporaryHitPoints;
  int hitPointMaximum;
  DiceRoller diceRoller;
  int proficiencyBonus;
  // Allow overriding the armor class for testing purposes
  Integer overrideArmorClass;
  private Point position;
  Inventory inventory = new Inventory();

  DeathTracker deathTracker = new DeathTracker(this);
  TurnTracker turnTracker;
  boolean isDead = false;

  Map<Ability, Integer> abilityScores = new EnumMap<>(Ability.class);

  private Creature(
      String name,
      Point position,
      int hitPointMaximum,
      int proficiencyBonus,
      DiceRoller diceRoller,
      TurnTracker turnTracker) {
    this.name = name;
    this.position = position;
    this.hitPointMaximum = this.hitPoints = hitPointMaximum;
    this.proficiencyBonus = proficiencyBonus;
    this.turnTracker = turnTracker;
    this.temporaryHitPoints = 0;

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
    return diceRoller.roll(
        20,
        new DiceRoller.RollInformation(name, "initiative")); // TODO add DEX modifier to the roll
  }

  @Override
  public int hitPoints() {
    return hitPoints;
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

    int mitigatedDamage = Math.min(actualDamage, temporaryHitPoints);
    temporaryHitPoints -= mitigatedDamage;
    actualDamage -= mitigatedDamage;

    if (hitPoints == 0 && actualDamage > 0) {
      if (actualDamage <= hitPointMaximum) {
        isDead = true;
        return;
      }
      DeathTracker.Result result = deathTracker.takingDamage(damage.isCritical());
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
    return turnTracker.movement;
  }

  public double movementSpeed() {
    return turnTracker.maxMovement;
  }

  public Inventory inventory() {
    return inventory;
  }

  @Override
  public int armorClass() {
    if (overrideArmorClass != null) return overrideArmorClass;
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
  public void turnReset() {
    turnTracker.reset();
  }

  @Override
  public String toString() {
    return name;
  }

  public static class Builder {
    private String name = "";
    private Point position;
    private int hitPointMaximum = 20;
    private int proficiencyBonus = 2;
    private DiceRoller diceRoller = new RandomDiceRoller();
    Integer overrideArmorClass;
    TurnTracker turnTracker = new TurnTracker(1, 1, 1, 1, 0, 10.0);

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder position(Point position) {
      this.position = position;
      return this;
    }

    public Builder hitPointMaximum(int hitPointMaximum) {
      this.hitPointMaximum = hitPointMaximum;
      return this;
    }

    public Builder movementSpeed(double movementSpeed) {
      turnTracker.maxMovement = movementSpeed;
      return this;
    }

    public Builder proficiencyBonus(int proficiencyBonus) {
      this.proficiencyBonus = proficiencyBonus;
      return this;
    }

    public Builder diceRoller(DiceRoller diceRoller) {
      this.diceRoller = diceRoller;
      return this;
    }

    public Builder overrideArmorClass(Integer overrideArmorClass) {
      this.overrideArmorClass = overrideArmorClass;
      return this;
    }

    public Builder actionsPerTurn(Integer actionsPerTurn) {
      turnTracker.maxActions = actionsPerTurn;
      return this;
    }

    public Builder bonusActionsPerTurn(Integer bonusActionsPerTurn) {
      turnTracker.maxBonusActions = bonusActionsPerTurn;
      return this;
    }

    public Builder reactionsPerTurn(Integer reactionsPerTurn) {
      turnTracker.maxReactions = reactionsPerTurn;
      return this;
    }

    public Builder attacksPerAction(Integer attacksPerAction) {
      turnTracker.attacksPerAction = attacksPerAction;
      return this;
    }

    public Builder spellSlots(Integer spellSlots) {
      turnTracker.spellSlots = spellSlots;
      return this;
    }

    public Creature build() {
      turnTracker.reset();
      return new Creature(
          name, position, hitPointMaximum, proficiencyBonus, diceRoller, turnTracker);
    }
  }
}
