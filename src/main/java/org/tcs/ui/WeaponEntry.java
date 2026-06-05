package org.tcs.ui;

import javafx.scene.control.Label;
import org.tcs.model.equipment.Weapon;

// Currently relatively useless, but might grow later
public class WeaponEntry extends Label {
  public WeaponEntry(Weapon weapon) {
    setText(weapon.name());
  }
}
