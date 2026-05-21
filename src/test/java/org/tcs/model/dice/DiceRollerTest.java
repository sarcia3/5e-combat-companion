package org.tcs.model.dice;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DiceRollerTest {

  @Test
  void rollMethodsThrowOnInvalidArguments() {
    DiceRoller r = new RandomDiceRoller();
    assertThrows(IllegalArgumentException.class, () -> r.roll(0));
    assertThrows(IllegalArgumentException.class, () -> r.roll(-1));
    assertThrows(IllegalArgumentException.class, () -> r.roll(3, 0));
    assertThrows(IllegalArgumentException.class, () -> r.roll(-1, 6));
    assertThrows(IllegalArgumentException.class, () -> r.rollWithAdvantage(0));
    assertThrows(IllegalArgumentException.class, () -> r.rollWithDisadvantage(0));
  }
}
