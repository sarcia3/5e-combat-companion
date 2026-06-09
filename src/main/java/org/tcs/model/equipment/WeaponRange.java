package org.tcs.model.equipment;

/**
 * Straight roll when dist <= normalFt Roll with disadvantage when normalFt < LongFt Additional
 * rules apply for close combat for ranged If weapon is a Melee weapon then normalFt=longFt The only
 * reason for the Ft suffix is that long is reserved keyword. Ft stands for feet.
 */
public record WeaponRange(double normalFt, double longFt) {
  public static final WeaponRange MELEE_STANDARD = new WeaponRange(5, 5);

  public boolean isDisadvantaged(double distance) {
    return (normalFt < distance);
  }
}
