package org.tcs.ui.viewmodel;

import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.NavMap;

public class CreatureViewModel {
  private final State model;
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final BooleanProperty isCurrent = new SimpleBooleanProperty(false);
  private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();
  private final ObservableList<StateProcess> attacks = FXCollections.observableArrayList();
  private final SimpleDoubleProperty movementLeft = new SimpleDoubleProperty(0.0);
  private NavMap navMap;
  private Runnable onPass = () -> {};

  public CreatureViewModel(State model, ObservableObjectValue<Creature> current) {
    this.model = model;

    creature.addListener(
        _ -> {
          if (creature.get() == null) return;

          weapons.setAll(creature.get().getWeapons());
          reloadNavMap();
        });
    isCurrent.bind(creature.isEqualTo(current));
  }

  public void loadAttacks(Weapon weapon) {
    attacks.setAll(model.getPossibleAttacks(creature.get(), weapon));
  }

  public void addWeapon(Weapon weapon) {
    creature.get().addWeapon(weapon);
    weapons.setAll(creature.get().getWeapons());
  }

  public void pass() {
    onPass.run();
  }

  public void setOnPass(Runnable onPass) {
    this.onPass = onPass;
  }

  public NavMap navMap() {
    return navMap;
  }

  void reloadNavMap() {
    if (creature.get() == null) return;
    movementLeft.set(creature.get().movementLeft());
    navMap = model.getMap().navMap(creature.get().position(), creature.get().movementLeft());
  }

  public DoubleProperty movementLeftProperty() {
    return movementLeft;
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

  public BooleanProperty isCurrentProperty() {
    return isCurrent;
  }
}
