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
  int hitPointMaximum;
  int temporaryHitPoints = 0;

  /** Resistance means taking half the damage of a given type */
  EnumSet<Damage.Type> resistances;

  /** Vulnerability means taking double the damage of a given type */
  EnumSet<Damage.Type> vulnerabilities;

  /** Immunity means taking no damage of a given type */
  EnumSet<Damage.Type> immunities;

  DiceRoller diceRoller;
  int proficiencyBonus;
  // Allow overriding the armor class for testing purposes
  Integer overrideArmorClass;
  private Point position;
  Inventory inventory = new Inventory();

  DeathTracker deathTracker = new DeathTracker(this);
  boolean isDead = false;

  Map<Ability, Integer> abilityScores = new EnumMap<>(Ability.class);

  double movementLeft;
  double movementSpeed;

  private Creature(
      String name,
      Point position,
      int hitPointMaximum,
      double movementSpeed,
      int proficiencyBonus,
      DiceRoller diceRoller,
      EnumSet<Damage.Type> resistances,
      EnumSet<Damage.Type> vulnerability,
      EnumSet<Damage.Type> immunities) {
    this.name = name;
    this.position = position;
    this.hitPointMaximum = this.hitPoints = hitPointMaximum;
    this.proficiencyBonus = proficiencyBonus;
    this.movementSpeed = movementSpeed;
    this.movementLeft = movementSpeed;
    this.resistances = resistances;
    this.vulnerabilities = vulnerability;
    this.immunities = immunities;

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
    // int actualDamage = damage.byType.values().stream().mapToInt(Integer::intValue).sum();
    int actualDamage = 0;
    for (var entry : damage.byType.entrySet()) {
      int effectiveDamage = entry.getValue();
      if (immunities.contains(entry.getKey())) effectiveDamage = 0;

      if (vulnerabilities.contains(entry.getKey())) effectiveDamage *= 2;

      if (resistances.contains(entry.getKey())) effectiveDamage /= 2;

      // the order is important, so rounding works properly. (res + vuln cancel each other)

      actualDamage += effectiveDamage;
    }

    if (hitPoints == 0 && actualDamage > 0) {
      if (actualDamage <= hitPointMaximum) {
        isDead = true;
        return;
      }
      // TODO implement catching critical throws
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
  public String toString() {
    return name;
  }

  public static class Builder {
    private String name = "";
    private Point position;
    private int hitPointMaximum = 20;
    private double movementSpeed = 5.0;
    private int proficiencyBonus = 2;
    private DiceRoller diceRoller = new RandomDiceRoller();
    Integer overrideArmorClass;
    private EnumSet<Damage.Type> resistances = EnumSet.noneOf(Damage.Type.class);
    private EnumSet<Damage.Type> vulnerability = EnumSet.noneOf(Damage.Type.class);
    private EnumSet<Damage.Type> immunities = EnumSet.noneOf(Damage.Type.class);

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
      this.movementSpeed = movementSpeed;
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

    public Builder resistances(EnumSet<Damage.Type> resistances) {
      this.resistances = resistances;
      return this;
    }

    public Builder vulnerability(EnumSet<Damage.Type> vulnerability) {
      this.vulnerability = vulnerability;
      return this;
    }

    public Builder immunities(EnumSet<Damage.Type> immunities) {
      this.immunities = immunities;
      return this;
    }

    public Creature build() {
      return new Creature(
          name,
          position,
          hitPointMaximum,
          movementSpeed,
          proficiencyBonus,
          diceRoller,
          resistances,
          vulnerability,
          immunities);
    }
  }
}
