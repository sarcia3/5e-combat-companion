package org.tcs.model.dice;

/**
 * How a d20 roll is made: with advantage (roll twice, keep the higher), with disadvantage (roll
 * twice, keep the lower), or normally.
 */
public enum RollMode {
  ADVANTAGE,
  NORMAL,
  DISADVANTAGE;

  /**
   * Combines the sources affecting a roll into a single mode. Following the 5e rule, any source of
   * advantage and any source of disadvantage cancel out to a normal roll, and multiple sources of
   * the same kind do not stack.
   */
  public static RollMode of(boolean anyAdvantage, boolean anyDisadvantage) {
    if (anyAdvantage == anyDisadvantage) return NORMAL;
    return anyAdvantage ? ADVANTAGE : DISADVANTAGE;
  }

  /** Rolls a single die of the given size according to this mode. */
  public int roll(DiceRoller roller, int numberOfSides, DiceRoller.RollInformation information) {
    return switch (this) {
      case ADVANTAGE -> roller.rollWithAdvantage(numberOfSides, information);
      case NORMAL -> roller.roll(numberOfSides, information);
      case DISADVANTAGE -> roller.rollWithDisadvantage(numberOfSides, information);
    };
  }
}
