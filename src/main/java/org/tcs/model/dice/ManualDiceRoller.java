package org.tcs.model.dice;

import java.io.IOException;

/** Sources dice rolls directly from the user. */
public class ManualDiceRoller implements DiceRoller {

  @Override
  public int roll(int numberOfSides, RollInformation information) {
    if (numberOfSides < 1) throw new IllegalArgumentException();
    System.out.println(information);
    System.out.println("Number of sides: " + numberOfSides);
    int roll;
    try {
      roll = System.in.read();
    } catch (IOException e) {
      roll = 0;
    }
    return roll;
  }
}
