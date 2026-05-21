package org.tcs.model;

/** Anything in the game world that can be attacked. */
public interface HasHitPoints {
  int hitPoints();

  int hitPointMaximum();

  void takeDamage(Damage damage);

  /**
   * @return the number an attack roll must meet or beat to hit.
   */
  int armorClass();
}
