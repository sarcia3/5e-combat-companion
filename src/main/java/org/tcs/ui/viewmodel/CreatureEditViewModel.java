package org.tcs.ui.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.equipment.Weapon;

public class CreatureEditViewModel {
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();

  public CreatureEditViewModel() {
    creature.addListener(
        _ -> {
          if (creature.get() == null) return;

          weapons.setAll(creature.get().getWeapons());
        });
  }

  public void addWeapon(Weapon weapon) {
    creature.get().addWeapon(weapon);
    weapons.setAll(creature.get().getWeapons());
  }

  public ObjectProperty<Creature> creatureProperty() {
    return creature;
  }

  public ObservableList<Weapon> weaponsProperty() {
    return weapons;
  }
}
