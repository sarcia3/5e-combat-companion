package org.tcs.model.magic;

public record Ranged(int value) implements SpellRange {
  public Ranged {
    if (value < 0) {
      throw new IllegalArgumentException();
    }
  }
}
