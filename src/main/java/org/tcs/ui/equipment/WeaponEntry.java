package org.tcs.ui.equipment;

import javafx.scene.control.Label;
import org.tcs.model.equipment.Weapon;

// Currently relatively useless, but might grow later
public class WeaponEntry extends Label {
  public final Weapon weapon;

  public WeaponEntry(Weapon weapon) {
    this.weapon = weapon;
    setText(weapon.name());
  }
}
