package org.tcs.model.dice;

public interface DiceRoller {
  /**
   * @throws IllegalArgumentException if numberOfSides less than 1
   */
  int roll(int numberOfSides);

  /**
   * @throws IllegalArgumentException if numberOfDice is less than 0 or numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int roll(int numberOfDice, int numberOfSides) {
    if (numberOfDice < 0) throw new IllegalArgumentException();
    int result = 0;
    for (int i = 0; i < numberOfDice; i++) {
      result += roll(numberOfSides);
    }
    return result;
  }

  /**
   * Advantage means making two rolls and taking the higher value.
   *
   * @throws IllegalArgumentException if numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int rollWithAdvantage(int numberOfSides) {
    return Integer.max(roll(numberOfSides), roll(numberOfSides));
  }

  /**
   * Disadvantage means making two rolls and taking the lower value.
   *
   * @throws IllegalArgumentException if numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int rollWithDisadvantage(int numberOfSides) {
    return Integer.min(roll(numberOfSides), roll(numberOfSides));
  }
}
