package org.tcs.model.activity;

public record AttackRoll(int naturalDice, int modifier) {
  public int total() {
    return naturalDice + modifier;
  }
}
