package org.tcs.model.dice;

import java.util.ArrayDeque;
import java.util.Queue;

/** A {@link DiceRoller} that returns a predetermined sequence of values, ignoring the die size. */
public final class QueuedDiceRoller implements DiceRoller {
  private final Queue<Integer> values;

  public QueuedDiceRoller(int... rolls) {
    this.values = new ArrayDeque<>();
    for (int r : rolls) values.add(r);
  }

  @Override
  public int roll(int numberOfSides, RollInformation information) {
    if (numberOfSides < 1) throw new IllegalArgumentException();
    return values.poll();
  }
}
