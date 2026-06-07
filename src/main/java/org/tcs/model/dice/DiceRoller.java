package org.tcs.model.dice;

public interface DiceRoller {
  /**
   * @throws IllegalArgumentException if numberOfSides less than 1
   */
  int roll(int numberOfSides, RollInformation information);

  /**
   * @throws IllegalArgumentException if numberOfSides less than 1
   */
  default int roll(int numberOfSides) {
    return roll(numberOfSides, new RollInformation());
  }

  /**
   * @throws IllegalArgumentException if numberOfDice is less than 0 or numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int roll(int numberOfDice, int numberOfSides, RollInformation information) {
    if (numberOfDice < 0) throw new IllegalArgumentException();
    int result = 0;
    for (int i = 0; i < numberOfDice; i++) {
      result += roll(numberOfSides);
    }
    return result;
  }

  /**
   * @throws IllegalArgumentException if numberOfDice is less than 0 or numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int roll(int numberOfDice, int numberOfSides) {
    return roll(numberOfDice, numberOfSides, new RollInformation());
  }

  /**
   * Advantage means making two rolls and taking the higher value.
   *
   * @throws IllegalArgumentException if numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int rollWithAdvantage(int numberOfSides, RollInformation information) {
    return Integer.max(roll(numberOfSides, information), roll(numberOfSides, information));
  }

  /**
   * Advantage means making two rolls and taking the higher value.
   *
   * @throws IllegalArgumentException if numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int rollWithAdvantage(int numberOfSides) {
    return rollWithAdvantage(numberOfSides, new RollInformation());
  }

  /**
   * Disadvantage means making two rolls and taking the lower value.
   *
   * @throws IllegalArgumentException if numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int rollWithDisadvantage(int numberOfSides, RollInformation information) {
    return Integer.min(roll(numberOfSides, information), roll(numberOfSides, information));
  }

  /**
   * Disadvantage means making two rolls and taking the lower value.
   *
   * @throws IllegalArgumentException if numberOfSides is less than 1
   *     <p>Not intended to be overridden.
   */
  default int rollWithDisadvantage(int numberOfSides) {
    return rollWithDisadvantage(numberOfSides, new RollInformation());
  }

  record RollInformation(String origin, String reason) {
    RollInformation() {
      this("", "");
    }
  }
}
