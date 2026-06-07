package org.tcs.model;

import org.tcs.model.dice.DiceRoller;

/**
 * A class to manage death mechanisms (<a
 * href="https://rpgbot.net/dnd5/how-to-play/damage-healing-dying/">see</a> for example).
 */
public class DeathTracker {
  int successes = 0;
  int failures = 0;
  boolean isStable = false;
  final Creature creature;

  DeathTracker(Creature creature) {
    this.creature = creature;
  }

  Result proceed(DiceRoller roller) {
    if (isStable) return Result.NONE;
    int score =
        roller.roll(20, new DiceRoller.RollInformation(creature.name, "death saving throw"));
    if (score < 10) failures++;
    else successes++;
    // Critical failure/success
    if (score == 1) failures++;
    if (score == 20) return Result.SAVE;
    if (successes > 2) {
      reset();
      isStable = true;
      return Result.NONE;
    }
    if (failures > 2) return Result.DEATH;
    return Result.NONE;
  }

  Result takingDamage(boolean isCritical) {
    isStable = false;
    failures++;
    if (isCritical) failures++;
    return failures > 2 ? Result.DEATH : Result.NONE;
  }

  void reset() {
    failures = 0;
    successes = 0;
    isStable = false;
  }

  void stabilise() {
    reset();
    isStable = true;
  }

  public enum Result {
    DEATH,
    NONE,
    SAVE
  }
}
