package org.tcs.ui.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.equipment.Weapon;

public class CreatureEditViewModel {
  private final State model;
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();

  public CreatureEditViewModel(State model) {
    this.model = model;

    creature.addListener(
        _ -> {
          if (creature.get() == null) return;

          weapons.setAll(creature.get().getWeapons());
        });
  }

  public ObjectProperty<Creature> creatureProperty() {
    return creature;
  }

  public ObservableList<Weapon> weaponsProperty() {
    return weapons;
  }
}
