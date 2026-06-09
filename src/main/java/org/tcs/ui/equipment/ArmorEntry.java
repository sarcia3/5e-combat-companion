package org.tcs.ui.equipment;

import javafx.scene.control.Label;
import org.tcs.model.equipment.Armor;

public class ArmorEntry extends Label {
  public final Armor armor;

  public ArmorEntry(Armor armor) {
    this.armor = armor;
    setText(armor.name());
  }
}
