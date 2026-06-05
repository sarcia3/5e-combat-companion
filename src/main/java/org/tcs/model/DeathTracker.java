package org.tcs.model;

import java.util.*;
import org.tcs.model.dice.DiceRoller;

/**
 * A class to manage death mechanisms (<a
 * href="https://rpgbot.net/dnd5/how-to-play/damage-healing-dying/">see</a> for example).
 */
public class DeathTracker {
  int positiveRolls = 0;
  int negativeRolls = 0;
  boolean isStable = false;

  Result proceed(DiceRoller roller) {
    if (isStable) return Result.NONE;
    int score = roller.roll(20);
    if (score < 10) negativeRolls++;
    else positiveRolls++;
    // Critical failure/success
    if (score == 1) negativeRolls++;
    if (score == 20) positiveRolls++;
    if (positiveRolls > 2) return Result.SAVE;
    if (negativeRolls > 2) return Result.DEATH;
    return Result.NONE;
  }

  Result savingThrow(DiceRoller roller) {
    int score = roller.roll(20);

    if (score < 10) negativeRolls++;
    if (score == 1) negativeRolls++;
    return negativeRolls > 2 ? Result.DEATH : Result.NONE;
  }

  void reset() {
    negativeRolls = 0;
    positiveRolls = 0;
    isStable = false;
  }

  void stabilise() {
    isStable = true;
  }

  public enum Result {
    DEATH,
    NONE,
    SAVE
  }
}
