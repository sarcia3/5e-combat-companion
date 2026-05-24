package org.tcs.model;

import java.util.*;

public class TurnHandler {
  List<Runnable> possibilities;

  public TurnHandler(Collection<Runnable> possibilities) {
    this.possibilities = new ArrayList<>(possibilities);
  }

  public Collection<Object> getPossibilities() {
    if (possibilities == null) throw new IllegalStateException("Turn already proceeded.");
    return List.copyOf(possibilities);
  }

  public void choosePossibility(Object choice) {
    if (possibilities == null) throw new IllegalStateException("Turn already proceeded.");

    if (!(choice instanceof Runnable runnable) || !possibilities.contains(choice))
      throw new IllegalArgumentException("Chosen object is not one of the possibilities.");

    runnable.run();
    possibilities = null;
  }
}
