package org.tcs.model;

public interface HasHitPoints {
  int hitPoints();

  int hitPointMaximum();

  void takeDamage(Damage damage);
}
