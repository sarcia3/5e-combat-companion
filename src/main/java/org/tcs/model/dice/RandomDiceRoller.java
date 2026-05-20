package org.tcs.model.dice;

import java.util.Random;

// TODO: reconsider for snapshots
public class RandomDiceRoller implements DiceRoller {
  Random random = new Random();

  @Override
  public int roll(int numberOfSides) {
    if (numberOfSides < 1) throw new IllegalArgumentException();

    return random.nextInt(1, numberOfSides + 1);
  }
}
