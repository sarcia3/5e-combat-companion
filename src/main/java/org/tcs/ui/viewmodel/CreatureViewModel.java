package org.tcs.ui.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Weapon;

public class CreatureViewModel {
  private final State model;
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();
  private final ObservableList<StateProcess> attacks = FXCollections.observableArrayList();

  public CreatureViewModel(State model) {
    this.model = model;

    creature.addListener(
        _ -> {
          if (creature.get() == null) return;

          weapons.setAll(creature.get().getWeapons());
        });
  }

  public void loadAttacks(Weapon weapon) {
    attacks.setAll(model.getPossibleAttacks(creature.get(), weapon));
  }

  public ObjectProperty<Creature> creatureProperty() {
    return creature;
  }

  public ObservableList<Weapon> weaponsProperty() {
    return weapons;
  }

  public ObservableList<StateProcess> attacksProperty() {
    return attacks;
  }
}
