package org.tcs.model.dice;

/** Sources dice rolls directly from the user. */
public class ManualDiceRoller implements DiceRoller {

  @Override
  public int roll(int numberOfSides) {
    if (numberOfSides < 1) throw new IllegalArgumentException();
    // TODO implement
    return 1;
  }
}
