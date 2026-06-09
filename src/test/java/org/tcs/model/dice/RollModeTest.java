package org.tcs.model.dice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RollModeTest {
  private static final DiceRoller.RollInformation INFO = new DiceRoller.RollInformation();

  @Test
  void ofCancelsOpposingSources() {
    assertEquals(RollMode.NORMAL, RollMode.of(false, false));
    assertEquals(RollMode.ADVANTAGE, RollMode.of(true, false));
    assertEquals(RollMode.DISADVANTAGE, RollMode.of(false, true));
    assertEquals(RollMode.NORMAL, RollMode.of(true, true)); // advantage + disadvantage cancel
  }

  @Test
  void advantageKeepsHigherOfTwoRolls() {
    assertEquals(15, RollMode.ADVANTAGE.roll(new QueuedDiceRoller(7, 15), 20, INFO));
  }

  @Test
  void disadvantageKeepsLowerOfTwoRolls() {
    assertEquals(7, RollMode.DISADVANTAGE.roll(new QueuedDiceRoller(7, 15), 20, INFO));
  }

  @Test
  void normalRollsExactlyOnce() {
    assertEquals(7, RollMode.NORMAL.roll(new QueuedDiceRoller(7, 15), 20, INFO));
  }
}
